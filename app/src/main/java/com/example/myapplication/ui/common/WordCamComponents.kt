package com.example.myapplication.ui.common


import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun WordCamScreen() {
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var recognizedWord by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Intent để chụp ảnh
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedImage = bitmap
            isProcessing = true
            coroutineScope.launch {
                kotlinx.coroutines.delay(1500)
                recognizedWord = "🍌 Banana" // Giả lập kết quả nhận diện
                isProcessing = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📷 Word Cam", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị ảnh vừa chụp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                capturedImage != null -> {
                    Image(
                        bitmap = capturedImage!!.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Text("Chưa có ảnh", color = Color.Gray)
                }
            }

            if (isProcessing) {
                CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút chụp ảnh
        Button(
            onClick = {
                recognizedWord = null
                cameraLauncher.launch(null)
            },
            enabled = !isProcessing
        ) {
            Text("Chụp ảnh")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Kết quả nhận diện
        recognizedWord?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔍 Đã nhận diện:", fontWeight = FontWeight.Bold)
                    Text(it, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}