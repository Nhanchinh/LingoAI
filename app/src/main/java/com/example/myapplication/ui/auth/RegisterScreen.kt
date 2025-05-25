//package com.example.myapplication.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun RegisterScreen(onRegisterSuccess: () -> Unit) {
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Đăng ký", style = MaterialTheme.typography.headlineMedium)
//
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text("Username") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        )
//
//        OutlinedTextField(
//            value = confirmPassword,
//            onValueChange = { confirmPassword = it },
//            label = { Text("Nhập lại Password") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        )
//
//        Button(
//            onClick = { /* logic sau */ },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Xác nhận")
//        }
//    }
//}




package com.example.myapplication.ui.auth

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
                painter = painterResource(id = R.drawable.sheep_logo), // Thay bằng resource hình cừu của bạn
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
                // singleLine = true,
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
                // singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),

                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 24.dp)
            )

            // Nút Sign up
            Button(
                onClick = {
                    ApiService.registerUser(username, password) { statusCode, responseJson ->
                        coroutineScope.launch {
                            if (statusCode == 200 && responseJson != null) {
                                val jsonObject = JSONObject(responseJson)
                                val userId = jsonObject.optString("user_id")
                                Toast.makeText(context, "Login thành công!", Toast.LENGTH_SHORT).show()
                                ApiService.setUserId(userId)
                                onRegisterSuccess()
                            } else {
                                val errorMsg = try {
                                    JSONObject(responseJson ?: "").optString("error", "Đăng nhập thất bại")
                                } catch (e: Exception) {
                                    "Đăng nhập thất bại (lỗi bất định)"
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD1BFE6),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(48.dp)
            ) {
                Text("Đăng kí", fontSize = 20.sp)
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
        onRegisterSuccess ={},
         onBackToLogin ={true}
    )
}


