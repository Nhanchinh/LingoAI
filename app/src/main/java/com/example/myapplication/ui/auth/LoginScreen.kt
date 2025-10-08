package com.example.myapplication.ui.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.myapplication.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.ApiService
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.Pink80

import com.example.myapplication.ui.theme.AppText
import com.example.myapplication.ui.common.KeyboardDismissWrapper
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.example.myapplication.UserPreferences
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    KeyboardDismissWrapper {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = "Splash Logo",
                modifier = Modifier.size(160.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.lingo_ai),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    error = false
                },
                label = { Text("Username") },
                shape = RoundedCornerShape(12.dp),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    error = false
                }
                ,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            if (error) {
                Text("Sai tài khoản hoặc mật khẩu", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    ApiService.loginUser(username, password) { code, responseBody ->
                        if (code == 200 && responseBody != null) {
                            try {
                                val jsonResponse = JSONObject(responseBody)
                                if (jsonResponse.has("user_id")) {
                                    val userId = jsonResponse.getString("user_id")

                                    // Lưu ID người dùng vào ApiService
                                    ApiService.setUserId(userId)

                                    // Lưu thông tin đăng nhập vào UserPreferences
                                    coroutineScope.launch {
                                        val userPreferences = UserPreferences(context)
                                        userPreferences.saveUserData(userId, username)
                                        Log.d("LoginScreen", "Saved user data: $userId, $username")
                                    }
                                }

                                // UI thread để hiển thị Toast và điều hướng
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                }
                            } catch (e: Exception) {
                                Log.e("LoginScreen", "Error parsing login response: ${e.message}")
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Lỗi xử lý dữ liệu đăng nhập", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    context,
                                    if (code == -1) "Lỗi kết nối" else "Sai tên đăng nhập hoặc mật khẩu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink80,
                    contentColor = AppText
                )
            ) {
                Text(
                    text = "Đăng nhập",
                    fontSize = 22.sp
                )
            }

            // Offline guest login
            OutlinedButton(
                onClick = {
                    val guestId = "guest_" + System.currentTimeMillis()
                    // Lưu local user và điều hướng
                    coroutineScope.launch {
                        val userPreferences = UserPreferences(context)
                        userPreferences.saveUserData(guestId, "Guest")
                        ApiService.setUserId(guestId)
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "Đang dùng chế độ offline (Guest)", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Dùng tài khoản khách", fontSize = 18.sp)
            }

            Text(
                text = "Bạn chưa có tài khoản? Đăng ký",
                modifier = Modifier
                    .clickable { onNavigateToRegister() }
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
                color = AppText,
                textDecoration = TextDecoration.Underline
            )
        }


    }

}


@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Login Screen Preview"
)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = { },
        onNavigateToRegister = { }
    )
}



