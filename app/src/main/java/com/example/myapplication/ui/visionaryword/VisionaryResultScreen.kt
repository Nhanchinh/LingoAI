package com.example.myapplication.ui.visionaryword

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.ui.common.BottomNavBar
import org.json.JSONObject
import java.io.File

// Thêm imports cho scroll và animation
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.TextPrimary

// THÊM: Imports cho animation
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.launch

fun Context.bitmapToFile(bitmap: Bitmap): File {
    val file = File(cacheDir, "temp_image.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file
}

@Composable
fun VisionaryResultScreen(
    context: Context,
    image: Bitmap?,
    onRetake: () -> Unit,
    onNavItemSelected: (String) -> Unit = {},
    onPlayAudio: (String) -> Unit = {},
    onSaveWord: (String) -> Unit = {}
) {
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var objects by remember { mutableStateOf<List<DetectedObject>>(emptyList()) }
    
    // THÊM: State tracking loading audio cho từng từ
    var loadingAudioWords by remember { mutableStateOf<Set<String>>(emptySet()) }

    // THÊM: Animation cho loading icon
    val infiniteTransition = rememberInfiniteTransition(label = "audio_loading_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    // Khi image thay đổi, gọi API với logic chống lặp
    LaunchedEffect(image) {
        if (image != null) {
            loading = true
            error = null

            val imageFile = context.bitmapToFile(image)

            ApiService.detectObject(imageFile) { responseJson ->
                loading = false
                if (responseJson == null) {
                    error = "Lỗi kết nối API"
                    return@detectObject
                }

                try {
                    val json = JSONObject(responseJson)
                    imageUrl = json.optString("image_url")
                    val jsonObjects = json.getJSONArray("objects")
                    
                    // LOGIC CHỐNG LẶP
                    val addedWords = mutableSetOf<String>()
                    val tempList = mutableListOf<DetectedObject>()
                    
                    for (i in 0 until jsonObjects.length()) {
                        val item = jsonObjects.getJSONObject(i)
                        val word = item.optString("word").lowercase().trim()
                        
                        // CHỈ THÊM NẾU TỪ CHƯA TỒN TẠI
                        if (word.isNotEmpty() && !addedWords.contains(word)) {
                            tempList.add(
                                DetectedObject(
                                    ipa = item.optString("ipa"),
                                    meaning = item.optString("meaning"),
                                    word = item.optString("word")
                                )
                            )
                            addedWords.add(word)
                        }
                    }
                    
                    objects = tempList
                    error = null
                } catch (e: Exception) {
                    error = "Lỗi xử lý dữ liệu"
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Photo Result",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Ảnh được chụp
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(4 / 3f)
                    .background(Color.LightGray, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(color = ButtonPrimary)
                } else if (error != null) {
                    Text(error ?: "", color = Color.Red)
                } else {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Detected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text("No image available", color = Color.Gray)
                    }
                }
            }

            // Nút "Take another photo" 
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRetake,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take another photo", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // THAY ĐỔI THIẾT KẾ CÁC THẺ TỪ VỰNG với loading animation
            objects.forEach { obj ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(ButtonSecondary, shape = RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "${obj.word} /${obj.ipa}/",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            obj.meaning,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }

                    // Các nút Play và Save với loading animation
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { 
                                if (!loadingAudioWords.contains(obj.word)) {
                                    // Bắt đầu loading
                                    loadingAudioWords = loadingAudioWords + obj.word
                                    
                                    // Gọi audio API
                                    onPlayAudio(obj.word)
                                    
                                    // Simulate audio duration (có thể thay bằng callback thật từ audio manager)
                                    kotlinx.coroutines.GlobalScope.launch {
                                        kotlinx.coroutines.delay(3000) // 3 giây
                                        loadingAudioWords = loadingAudioWords - obj.word
                                    }
                                }
                            }
                        ) {
                            if (loadingAudioWords.contains(obj.word)) {
                                // HIỂN THỊ LOADING ANIMATION
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Loading audio",
                                    tint = ButtonPrimary,
                                    modifier = Modifier.rotate(rotation)
                                )
                            } else {
                                // HIỂN THỊ NÚT PLAY BÌNH THƯỜNG
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Play audio",
                                    tint = ButtonPrimary
                                )
                            }
                        }
                        
                        IconButton(
                            onClick = { onSaveWord(obj.word) }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Save word",
                                tint = ButtonPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class DetectedObject(
    val ipa: String,
    val meaning: String,
    val word: String
)