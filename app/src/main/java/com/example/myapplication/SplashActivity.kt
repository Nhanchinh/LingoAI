package com.example.myapplication


import android.annotation.SuppressLint
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.ApiService




import android.util.Log

import androidx.lifecycle.lifecycleScope
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hiển thị splash logo
        setContent {
            SplashScreenUI()
        }

        // Kiểm tra trạng thái đăng nhập
        lifecycleScope.launch {
            val userPreferences = UserPreferences(applicationContext)
            val isLoggedIn = userPreferences.isLoggedIn.first()
            val userId = userPreferences.userId.first()

            // Cập nhật userId trong ApiService nếu đã đăng nhập
            if (isLoggedIn && !userId.isNullOrBlank()) {
                ApiService.setUserId(userId)
            }

            ApiService.fetchAndSetBaseUrl { success ->
                runOnUiThread {
                    if (success) {
                        // BASE_URL đã được cập nhật, có thể gọi api khác
                        Log.i("SplashActivity", "API URL updated successfully")
                    } else {
                        // Xử lý lỗi
                        Log.e("SplashActivity", "Failed to update API URL")
                    }

                    // Chờ 2 giây rồi chuyển màn hình
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@SplashActivity, MainActivity::class.java).apply {
                            // Truyền trạng thái đăng nhập qua Intent
                            putExtra("is_logged_in", isLoggedIn)
                        }
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
        }
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

