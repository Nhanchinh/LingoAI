package com.example.myapplication.ui.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.TextPrimary

// Demo video data class
data class DemoVideo(
    val title: String,
    val description: String,
    val duration: String,
    val level: String,
    val videoId: String
)

// VIDEO STUDY CONTENT - Nội dung học qua video
@Composable
fun VideoStudyContent(
    onVideoClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = ButtonPrimary
                        )
                        Column {
                            Text(
                                "Video học từ vựng",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Học qua video với phát âm chuẩn",
                                fontSize = 14.sp,
                                color = TextPrimary.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Demo video cards với YouTube video IDs và phụ đề
        val demoVideos = listOf(
            VideoWithSubtitle(
                videoId = "wVhG59zh4uQ",
                title = "Học từ vựng cơ bản",
                description = "100 từ vựng thông dụng nhất",
                duration = "15 phút",
                level = "Beginner",
                subtitleFileName = "subtitle_basic_vocab.json"
            ),
            VideoWithSubtitle(
                videoId = "wVhG59zh4uQ",
                title = "Phát âm chuẩn",
                description = "Học cách phát âm 44 âm tiết",
                duration = "20 phút",
                level = "Intermediate",
                subtitleFileName = "subtitle_pronunciation.json"
            ),
            VideoWithSubtitle(
                videoId = "wVhG59zh4uQ",
                title = "Từ vựng TOEIC",
                description = "500 từ vựng TOEIC cần thiết",
                duration = "45 phút",
                level = "Advanced",
                subtitleFileName = "subtitle_toeic.json"
            ),
            VideoWithSubtitle(
                videoId = "wVhG59zh4uQ",
                title = "Từ vựng IELTS",
                description = "300 từ vựng IELTS Academic",
                duration = "30 phút",
                level = "Advanced",
                subtitleFileName = "subtitle_basic_vocab.json"
            ),
            VideoWithSubtitle(
                videoId = "wVhG59zh4uQ",
                title = "Từ vựng giao tiếp",
                description = "200 cụm từ giao tiếp hàng ngày",
                duration = "25 phút",
                level = "Intermediate",
                subtitleFileName = "subtitle_pronunciation.json"
            )
        )

        items(demoVideos) { video ->
            DemoVideoCard(
                video = video,
                onClick = {
                    onVideoClick(video.videoId, video.title, video.description)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// Demo video card
@Composable
fun DemoVideoCard(
    video: VideoWithSubtitle,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video thumbnail placeholder
            Card(
                modifier = Modifier
                    .size(80.dp, 60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ButtonPrimary.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = ButtonPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = video.description,
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextPrimary.copy(alpha = 0.6f)
                        )
                        Text(
                            text = video.duration,
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextPrimary.copy(alpha = 0.6f)
                        )
                        Text(
                            text = video.level,
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Xem video",
                modifier = Modifier.size(20.dp),
                tint = ButtonPrimary
            )
        }
    }
}
