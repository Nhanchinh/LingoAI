package com.example.myapplication.ui.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.TextPrimary
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

@Composable
fun VideoPlayerScreen(
    videoId: String,
    title: String = "Video học từ vựng",
    description: String = "Học từ vựng qua video với phát âm chuẩn",
    subtitleFileName: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentSubtitle by remember { mutableStateOf("") } // State để giữ phụ đề
    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    
    // Speech recognition states
    val speechManager = remember { SpeechRecognitionManager(context) }
    val isRecording by speechManager.isRecordingState
    val recordingError by speechManager.recordingErrorState
    
    // Pronunciation result states
    var showResultDialog by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var pronunciationScore by remember { mutableStateOf(PronunciationScore.FAIR) }
    var similarity by remember { mutableStateOf(0.0) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack, 
                        contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            // VIDEO PLAYER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    YouTubePlayerComposable(
                        videoId = videoId,
                        subtitleFileName = subtitleFileName,
                        onSubtitleUpdate = { subtitle ->
                            currentSubtitle = subtitle // Cập nhật phụ đề từ player
                        },
                        onPlayerReady = { player ->
                            youTubePlayer = player
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Phần phụ đề với chiều cao cố định để tránh layout jump
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp), // Chiều cao cố định
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentSubtitle.isNotEmpty()) 
                                Color.Black.copy(alpha = 0.8f) 
                            else 
                                Color.Transparent
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (currentSubtitle.isNotEmpty()) 4.dp else 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentSubtitle.isNotEmpty()) {
                                Text(
                                    text = currentSubtitle,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                    
                    // Nút thu âm
                    if (currentSubtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (isRecording) {
                                    speechManager.stopListening()
                                } else {
                                    // Pause video trước khi thu âm
                                    youTubePlayer?.pause()
                                    
                                    speechManager.startListening(
                                        onResult = { result ->
                                            spokenText = result
                                            similarity = TextSimilarity.calculateSimilarity(result, currentSubtitle)
                                            pronunciationScore = TextSimilarity.getPronunciationScore(similarity)
                                            showResultDialog = true
                                        },
                                        onError = { error ->
                                            // Hiển thị lỗi nếu cần
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) Color.Red else ButtonPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = if (isRecording) "Dừng thu âm" else "Thu âm",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRecording) "Đang thu âm..." else "Thu âm phát âm",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Hiển thị trạng thái thu âm
                        if (isRecording) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🎤 Đang nghe... Hãy nói câu: \"$currentSubtitle\"",
                                fontSize = 14.sp,
                                color = TextPrimary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Hiển thị lỗi thu âm nếu có
                        recordingError?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "❌ Lỗi: $error",
                                fontSize = 14.sp,
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // VIDEO INFO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Justify
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Video stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        VideoStatItem(
                            icon = "🎥",
                            label = "Loại",
                            value = "Video học"
                        )
                        
                        VideoStatItem(
                            icon = "⏱️",
                            label = "Thời lượng",
                            value = "~10 phút"
                        )
                        
                        VideoStatItem(
                            icon = "📚",
                            label = "Cấp độ",
                            value = "Beginner"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    
    // Dialog kết quả phát âm
    PronunciationResultDialog(
        isVisible = showResultDialog,
        spokenText = spokenText,
        targetText = currentSubtitle,
        similarity = similarity,
        pronunciationScore = pronunciationScore,
        onDismiss = {
            showResultDialog = false
            // Resume video sau khi đóng dialog
            youTubePlayer?.play()
        }
    )
}

@Composable
fun VideoStatItem(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextPrimary.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
