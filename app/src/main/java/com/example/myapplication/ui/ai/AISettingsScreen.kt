package com.example.myapplication.ui.ai

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AISettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    // Lấy giọng AI hiện tại
    val currentVoice = userPreferences.aiVoice.collectAsState(initial = "af_heart").value ?: "af_heart"
    var expandedVoice by remember { mutableStateOf(false) }

    // Danh sách các giọng AI
    val voices = listOf(
        "af_heart" to "Heart (Nữ, ấm áp)",
        "af_bella" to "Bella (Nữ, trẻ trung)", 
        "af_sarah" to "Sarah (Nữ, chuyên nghiệp)",
        "af_sky" to "Sky (Nữ, năng động)",
        "am_michael" to "Michael (Nam, ấm áp)",
        "am_onyx" to "Onyx (Nam, trầm ấm)",
        "am_fenrir" to "Fenrir (Nam, mạnh mẽ)"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header đơn giản
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Cài đặt AI",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp)) // Cân bằng với back button
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Main content đơn giản
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header section
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFF6A4C93),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🤖 Cài đặt AI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748)
                            )
                            Text(
                                text = "Tùy chỉnh giọng nói AI",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                    
                    Divider()
                    
                    // Voice selector
                    Column {
                        Text(
                            text = "Giọng AI hiện tại",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Display current voice
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = voices.find { it.first == currentVoice }?.second ?: currentVoice,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Voice selector dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedVoice,
                            onExpandedChange = { expandedVoice = !expandedVoice }
                        ) {
                            OutlinedTextField(
                                value = voices.find { it.first == currentVoice }?.second ?: currentVoice,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Chọn giọng AI") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVoice) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedVoice,
                                onDismissRequest = { expandedVoice = false }
                            ) {
                                voices.forEach { (voiceId, voiceName) ->
                                    DropdownMenuItem(
                                        text = { Text(voiceName) },
                                        onClick = {
                                            expandedVoice = false
                                            coroutineScope.launch { 
                                                userPreferences.saveAiVoice(voiceId)
                                                Toast.makeText(context, "Đã đổi giọng AI: $voiceName", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Information section
                    Column {
                        Text(
                            text = "ℹ️ Thông tin",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Giọng AI sẽ được sử dụng cho tính năng ChatSmart\n• Có thể thay đổi giọng bất cứ lúc nào\n• Mỗi giọng có đặc điểm riêng phù hợp với từng tình huống",
                            fontSize = 13.sp,
                            color = Color(0xFF666666),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Info card đơn giản
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Tip",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D6E63)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thử các giọng khác nhau để tìm ra giọng phù hợp nhất với bạn!",
                        fontSize = 12.sp,
                        color = Color(0xFF8D6E63)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
