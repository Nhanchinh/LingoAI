package com.example.myapplication.ui.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UltraSimpleStreakCard(
    streakInfo: UltraSimpleStreakInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header với icon và title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "Streak App",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = getMotivationalMessage(streakInfo),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            // Main stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Current Streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${streakInfo.currentStreak}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35)
                    )
                    Text(
                        text = "Hiện tại",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                // Today Status
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (streakInfo.isTodayOpened) "🔥" else "💤",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Hôm nay",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                // Best Streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${streakInfo.bestStreak}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "Kỷ lục",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tiến độ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3748)
                )
                
                Text(
                    text = "${getNextMilestone(streakInfo.currentStreak)} ngày tới mốc tiếp theo",
                    fontSize = 12.sp,
                    color = Color(0xFF6A4C93)
                )
            }
            
            // Simple progress bar
            androidx.compose.material3.LinearProgressIndicator(
                progress = getProgressPercent(streakInfo.currentStreak),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFFE48ED4),
                trackColor = Color(0xFFF3E5F5)
            )
            
            // Achievement level
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getAchievementColor(streakInfo.currentStreak).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = getAchievementEmoji(streakInfo.currentStreak),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cấp độ: ${getAchievementName(streakInfo.currentStreak)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = getAchievementColor(streakInfo.currentStreak)
                        )
                    }
                    Text(
                        text = "${streakInfo.currentStreak} ngày",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = getAchievementColor(streakInfo.currentStreak)
                    )
                }
            }
        }
    }
}

private fun getMotivationalMessage(streakInfo: UltraSimpleStreakInfo): String {
    return when {
        streakInfo.currentStreak == 0 -> "Bắt đầu hành trình học tập! 🚀"
        streakInfo.currentStreak < 3 -> "Tuyệt vời! Hãy tiếp tục! 💪"
        streakInfo.currentStreak < 7 -> "Bạn đang trên đúng hướng! 🌟"
        streakInfo.currentStreak < 14 -> "Xuất sắc! Thói quen tốt! ⚡"
        streakInfo.currentStreak < 30 -> "Ấn tượng! Gương mẫu! 🎯"
        streakInfo.currentStreak < 60 -> "Phi thường! Cảm hứng! 🌈"
        else -> "Huyền thoại! Bậc thầy! 🏆"
    }
}

private fun getNextMilestone(currentStreak: Int): Int {
    return when {
        currentStreak < 3 -> 3 - currentStreak
        currentStreak < 7 -> 7 - currentStreak
        currentStreak < 14 -> 14 - currentStreak
        currentStreak < 30 -> 30 - currentStreak
        currentStreak < 60 -> 60 - currentStreak
        else -> 0
    }
}

private fun getProgressPercent(currentStreak: Int): Float {
    return when {
        currentStreak < 3 -> currentStreak / 3f
        currentStreak < 7 -> (currentStreak - 3) / 4f
        currentStreak < 14 -> (currentStreak - 7) / 7f
        currentStreak < 30 -> (currentStreak - 14) / 16f
        currentStreak < 60 -> (currentStreak - 30) / 30f
        else -> 1f
    }
}

private fun getAchievementEmoji(streak: Int): String {
    return when {
        streak == 0 -> "🌱"
        streak < 3 -> "🗺️"
        streak < 7 -> "📚"
        streak < 14 -> "🎓"
        streak < 30 -> "👑"
        streak < 60 -> "🏆"
        else -> "🔥"
    }
}

private fun getAchievementName(streak: Int): String {
    return when {
        streak == 0 -> "Người mới"
        streak < 3 -> "Khám phá"
        streak < 7 -> "Chăm chỉ"
        streak < 14 -> "Học giả"
        streak < 30 -> "Bậc thầy"
        streak < 60 -> "Huyền thoại"
        else -> "Bất tử"
    }
}

private fun getAchievementColor(streak: Int): Color {
    return when {
        streak == 0 -> Color(0xFF9E9E9E)
        streak < 3 -> Color(0xFF4CAF50)
        streak < 7 -> Color(0xFF2196F3)
        streak < 14 -> Color(0xFF9C27B0)
        streak < 30 -> Color(0xFFFF9800)
        streak < 60 -> Color(0xFFE91E63)
        else -> Color(0xFFFFD700)
    }
}


