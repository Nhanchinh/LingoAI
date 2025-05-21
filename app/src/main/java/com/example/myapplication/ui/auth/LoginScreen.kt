package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults

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
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.Pink80

import com.example.myapplication.ui.theme.AppText
import com.example.myapplication.ui.common.KeyboardDismissWrapper

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

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
                    if (username == "admin" && password == "123") {
                        onLoginSuccess()
                    } else {
                        error = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink80,
                    contentColor = AppText
                )
            ) {
                Text("Đăng nhập")
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
