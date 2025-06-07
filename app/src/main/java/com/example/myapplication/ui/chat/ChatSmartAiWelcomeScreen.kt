//package com.example.myapplication.ui.chat
//
//import android.Manifest
//import android.content.pm.PackageManager
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.unit.sp
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//import androidx.compose.foundation.Image
//
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import com.example.myapplication.R
//
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.shape.RoundedCornerShape
//
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.core.content.ContextCompat
//import com.example.myapplication.ui.common.BottomNavBar
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//
//data class ChatMessage(
//    val sender: String, // "Lingoo" hoặc "HN143"
//    val text: String,
//    val isUser: Boolean
//)
//
//
//@Composable
//fun ChatSmartAiWelcomeScreen(
//    onBack: () -> Unit = {},
//    onRecordStart: () -> Unit = {},
//    onRecordStop: (((String) -> Unit) -> Unit) = {},  //  kiểu có nhận callback
//    onNavigate: (Any?) -> Unit = {},
//   // onNavItemSelected: (String) -> Unit = {} // Thêm tham số này
//) {
//    val context = LocalContext.current
//    val isRecording = remember { mutableStateOf(false) }
//    var pressStartTime by remember { mutableStateOf(0L) }
//    // Khai báo launcher để request quyền
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // Quyền đã được cấp, có thể ghi âm
//            onRecordStart()
//        } else {
//            // Quyền bị từ chối
//            Log.d("Permissions", "Permission denied")
//        }
//    }
//
//    Log.d("testt", "on chat")
//
//    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//        Log.d("Permissions", "Permission checkSelfPermission")
//        // Yêu cầu cấp quyền
//        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MainColor)
//    ) {
//        Column(
//            modifier = Modifier
//
//                .fillMaxSize()
//                .padding(  bottom = 80.dp), // Thêm padding bottom để chừa chỗ cho navbar
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Hình cừu và bong bóng
//            Image(
//                painter = painterResource(id = R.drawable.sheep_logo),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(180.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                "ChatSmart AI",
//                fontWeight = FontWeight.Bold,
//                fontSize = 32.sp,
//                fontFamily = FontFamily.Serif
//            )
//            Text(
//                "Hãy bắt đầu cuộc hội thoại thôi nào!",
//                fontSize = 18.sp,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                "Nhấn giữ để ghi âm",
//                fontSize = 16.sp,
//                color = Color.DarkGray
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Icon mic
//            Box(
//                modifier = Modifier
//                    .size(56.dp)
//                    .background(ButtonSecondary, shape = RoundedCornerShape(28.dp))
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onPress = {
//                                pressStartTime = System.currentTimeMillis()
//                                isRecording.value = true
//                                onRecordStart()
//
//                                val released = tryAwaitRelease()
//
//                                isRecording.value = false
//                                val pressDuration = System.currentTimeMillis() - pressStartTime
//
//                                if (pressDuration < 300) {
//                                    // Nếu nhấn quá nhanh
//                                    Toast.makeText(context, "Bạn cần giữ mic để ghi âm", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    onRecordStop { transcription ->
//                                        Log.d("Transcription", transcription)
//                                        CoroutineScope(Dispatchers.Main).launch {
//                                            onNavigate(transcription)
//                                        }
//                                    }
//                                }
//                            }
//                        )
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_mic),
//                    contentDescription = "Mic",
//                    modifier = Modifier.size(32.dp)
//
//                )
//            }
//
//
//            Spacer(modifier = Modifier.weight(1f))
//        }
//
//        // Đặt BottomNavBar trong Box để có thể sử dụng Modifier.align
////        BottomNavBar(
////            currentRoute = "chat_smart_ai", // Sửa route để match với tên thật
////            onNavItemSelected = onNavItemSelected,
////            modifier = Modifier.align(Alignment.BottomCenter)
////        )
//    }
//}
//
//
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewChatSmartAiWelcomeScreen() {
//    ChatSmartAiWelcomeScreen()
//}
//




package com.example.myapplication.ui.chat

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.MainColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String,
    val text: String,
    val isUser: Boolean
)

@Composable
fun ChatSmartAiWelcomeScreen(
    onBack: () -> Unit = {},
    onRecordStart: () -> Unit = {},
    onRecordStop: (((String) -> Unit) -> Unit) = {},
    onNavigate: (Any?) -> Unit = {}
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isProcessingAudio by remember { mutableStateOf(false) }
    var pressStartTime by remember { mutableStateOf(0L) }

    // Animation cho mic xoay
    val infiniteTransition = rememberInfiniteTransition(label = "mic_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Animation cho processing (pulse effect)
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Animation cho sheep logo
    val sheepScale by animateFloatAsState(
        targetValue = if (isRecording) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "sheep_scale"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onRecordStart()
        } else {
            Log.d("Permissions", "Permission denied")
        }
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sheep logo với animation scale
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer(
                        scaleX = sheepScale,
                        scaleY = sheepScale
                    )
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

            // Mic button với animations
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(ButtonSecondary, shape = RoundedCornerShape(28.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                pressStartTime = System.currentTimeMillis()
                                isRecording = true
                                onRecordStart()

                                val released = tryAwaitRelease()

                                isRecording = false
                                isProcessingAudio = true
                                val pressDuration = System.currentTimeMillis() - pressStartTime

                                if (pressDuration < 300) {
                                    isProcessingAudio = false
                                    Toast.makeText(context, "Bạn cần giữ mic để ghi âm", Toast.LENGTH_SHORT).show()
                                } else {
                                    onRecordStop { transcription ->
                                        Log.d("Transcription", transcription)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            isProcessingAudio = false
                                            onNavigate(transcription)
                                        }
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // CircularProgressIndicator cho cả hai trạng thái
                if (isRecording || isProcessingAudio) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .graphicsLayer(
                                scaleX = if (isProcessingAudio) scale else 1f,
                                scaleY = if (isProcessingAudio) scale else 1f
                            ),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }

                // Mic icon với animation
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = "Mic",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(
                            rotationZ = if (isRecording) rotation else 0f,
                            scaleX = if (isProcessingAudio) scale else 1f,
                            scaleY = if (isProcessingAudio) scale else 1f
                        ),
                    tint = when {
                        isRecording -> Color.Red
                        isProcessingAudio -> Color.Green
                        else -> Color.Black
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatSmartAiWelcomeScreen() {
    ChatSmartAiWelcomeScreen()
}