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




package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackToLogin: () -> Boolean) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        Text(
            "LingoAI",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Ô nhập Username
        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Username") },
            singleLine = true,
            //shape = RoundedCornerShape(16.dp),

            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(bottom = 16.dp)
        )

        // Ô nhập Password
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            singleLine = true,
            //shape = RoundedCornerShape(16.dp),
            visualTransformation = PasswordVisualTransformation(),

            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(bottom = 24.dp)
        )

        // Nút Sign up
        Button(
            onClick = { onRegisterSuccess() },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD1BFE6),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(48.dp)
        ) {
            Text("Sign up", fontSize = 20.sp)
        }
    }
}