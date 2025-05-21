package com.example.myapplication


import com.example.myapplication.ui.theme.MainColor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hiển thị splash logo trong 2s rồi chuyển qua MainActivity
        setContent {
            SplashScreenUI()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000) // Delay 2 giây
    }
}

@Preview
@Composable
fun SplashScreenUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor),
        contentAlignment = Alignment.Center
    ) {
        // Stack 2 hình chồng lên nhau, nếu bạn muốn tách vị trí thì dùng Column hoặc thêm Modifier.offset()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp) // hoặc 0.dp
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
        }

    }
}

