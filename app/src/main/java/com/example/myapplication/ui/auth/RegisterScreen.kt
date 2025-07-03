package com.example.myapplication.ui.auth

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.ui.common.KeyboardDismissWrapper
import com.example.myapplication.ui.theme.AppText
import com.example.myapplication.ui.theme.Pink80
import com.example.myapplication.UserPreferences
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    KeyboardDismissWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8CFEA)), // Hồng nhạt
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hình cừu
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )

            // Tên app
            Image(
                painter = painterResource(id = R.drawable.lingo_ai),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            // Ô nhập Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Username") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 16.dp)
            )

            // Ô nhập Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 24.dp)
            )

            // ✅ SỬA NÚT ĐĂNG KÝ - lưu thông tin user đầy đủ
            Button(
                onClick = {
                    ApiService.registerUser(username, password) { statusCode, responseJson ->
                        if (statusCode == 200 && responseJson != null) {
                            try {
                                val jsonObject = JSONObject(responseJson)
                                val userId = jsonObject.optString("user_id")

                                // ✅ LƯU ID VÀO APISERVICE
                                ApiService.setUserId(userId)

                                // ✅ LƯU THÔNG TIN USER VÀO USERPREFERENCES (giống LoginScreen)
                                coroutineScope.launch {
                                    val userPreferences = UserPreferences(context)
                                    userPreferences.saveUserData(userId, username)
                                    Log.d("RegisterScreen", "Saved user data: $userId, $username")

                                    // ✅ HIỂN THỊ TOAST VÀ CHUYỂN MÀNG HÌNH TRONG UI THREAD
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("RegisterScreen", "Error parsing register response: ${e.message}")
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Lỗi xử lý dữ liệu đăng ký", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            val errorMsg = try {
                                JSONObject(responseJson ?: "").optString("error", "Đăng ký thất bại")
                            } catch (e: Exception) {
                                "Đăng ký thất bại (lỗi bất định)"
                            }
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(75.dp)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink80,
                    contentColor = AppText
                )
            ) {
                Text(
                    text = "Đăng ký", 
                    fontSize = 22.sp
                )
            }

            Text(
                text = "Bạn đã có tài khoản? Đăng Nhập",
                modifier = Modifier
                    .clickable { onBackToLogin() }
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
                color = AppText,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun showPreview(){
    RegisterScreen(
        onRegisterSuccess = {},
        onBackToLogin = { }
    )
}


