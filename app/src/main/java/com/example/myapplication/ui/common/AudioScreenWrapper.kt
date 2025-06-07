package com.example.myapplication.ui.common



import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.myapplication.ui.chat.AudioManager

@Composable
fun AudioScreenWrapper(
    screenName: String,
    content: @Composable () -> Unit
) {
    // Tạo unique screenId cho mỗi màn hình
    val screenId = remember { "${screenName}_${System.currentTimeMillis()}" }

    // Cleanup audio khi màn hình bị dispose
    DisposableEffect(screenId) {
        onDispose {
            AudioManager.onScreenExit(screenId)
        }
    }

    // Render content
    content()
}