package com.example.myapplication.ui.chat

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.Image

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.R

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.common.BottomNavBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class ChatMessage(
    val sender: String, // "Lingoo" hoặc "HN143"
    val text: String,
    val isUser: Boolean
)


@Composable
fun ChatSmartAiWelcomeScreen(
    onBack: () -> Unit = {},
    onRecordStart: () -> Unit = {},
    onRecordStop: (((String) -> Unit) -> Unit) = {},  // 👈 kiểu có nhận callback
    onNavigate: (Any?) -> Unit = {},
    onNavItemSelected: (String) -> Unit = {} // Thêm tham số này
) {
    val context = LocalContext.current
    val isRecording = remember { mutableStateOf(false) }

    // Khai báo launcher để request quyền
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Quyền đã được cấp, có thể ghi âm
            onRecordStart()
        } else {
            // Quyền bị từ chối
            Log.d("Permissions", "Permission denied")
        }
    }

    Log.d("testt", "on chat")

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        Log.d("Permissions", "Permission checkSelfPermission")
        // Yêu cầu cấp quyền
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3CFE2))
    ) {
        Column(
            modifier = Modifier

                .fillMaxSize()
                .padding( 16.dp, bottom = 80.dp), // Thêm padding bottom để chừa chỗ cho navbar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hình cừu và bong bóng
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "ChatSmart AI",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = FontFamily.Serif
            )
            Text(
                "Hãy bắt đầu cuộc hội thoại thôi nào!",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Nhấn giữ để ghi âm",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Icon mic
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(28.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isRecording.value = true
                                onRecordStart()
                                // Chờ người dùng thả tay
                                tryAwaitRelease()

                                isRecording.value = false
                                onRecordStop { transcription ->
                                    Log.d("Transcription", transcription)
                                    // Chuyển sang main thread để điều hướng
                                    CoroutineScope(Dispatchers.Main).launch {
                                        onNavigate(transcription) // bên trong navigate()
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = "Mic",
                    modifier = Modifier.size(32.dp)
                )
            }


            Spacer(modifier = Modifier.weight(1f))
        }

        // Đặt BottomNavBar trong Box để có thể sử dụng Modifier.align
        BottomNavBar(
            currentRoute = "chat_smart_ai", // Sửa route để match với tên thật
            onNavItemSelected = onNavItemSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewChatSmartAiWelcomeScreen() {
    ChatSmartAiWelcomeScreen()
}

