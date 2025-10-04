package com.example.myapplication.ui.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.NotificationReceiver
import com.example.myapplication.NotificationPreferences
import com.example.myapplication.ui.theme.MainColor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val notificationPrefs = remember { NotificationPreferences(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var notificationEnabled by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(8) }
    var selectedMinute by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // Load saved settings
    LaunchedEffect(Unit) {
        val settings = notificationPrefs.getCurrentSettings()
        notificationEnabled = settings.first
        selectedHour = settings.second
        selectedMinute = settings.third
    }
    
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
                    text = "Cài đặt thông báo",
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
                    // Enable/Disable switch đơn giản
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bật thông báo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Nhắc nhở học từ vựng hàng ngày",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { enabled ->
                                notificationEnabled = enabled
                                coroutineScope.launch {
                                    notificationPrefs.setNotificationEnabled(enabled)
                                    if (enabled) {
                                        scheduleNotification(context, selectedHour, selectedMinute)
                                    } else {
                                        cancelNotification(context)
                                    }
                                }
                            }
                        )
                    }
                    
                    if (notificationEnabled) {
                        Divider()
                        
                        // Time picker section đơn giản
                        Column {
                            Text(
                                text = "Thời gian nhắc nhở",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Display current time
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Thời gian hiện tại: ${String.format("%02d:%02d", selectedHour, selectedMinute)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Time input đơn giản
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Hour input
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Giờ (0-23)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = selectedHour.toString(),
                                        onValueChange = { value ->
                                            try {
                                                val hour = value.toIntOrNull()
                                                if (hour != null && hour in 0..23) {
                                                    selectedHour = hour
                                                } else if (value.isEmpty()) {
                                                    selectedHour = 0
                                                }
                                            } catch (e: Exception) {
                                                // Ignore invalid input
                                            }
                                        },
                                        modifier = Modifier.width(100.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        ),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Giờ") }
                                    )
                                }
                                
                                Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE48ED4))
                                
                                // Minute input
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Phút (0-59)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = selectedMinute.toString(),
                                        onValueChange = { value ->
                                            try {
                                                val minute = value.toIntOrNull()
                                                if (minute != null && minute in 0..59) {
                                                    selectedMinute = minute
                                                } else if (value.isEmpty()) {
                                                    selectedMinute = 0
                                                }
                                            } catch (e: Exception) {
                                                // Ignore invalid input
                                            }
                                        },
                                        modifier = Modifier.width(100.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        ),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Phút") }
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Nút xác nhận
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        notificationPrefs.setNotificationTime(selectedHour, selectedMinute)
                                        scheduleNotification(context, selectedHour, selectedMinute)
                                    }
                                    Toast.makeText(context, "Đã lưu thời gian: ${String.format("%02d:%02d", selectedHour, selectedMinute)}", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE48ED4)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Lưu thời gian", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Quick time buttons đơn giản
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        selectedHour = 7
                                        selectedMinute = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("07:00", fontSize = 12.sp)
                                }
                                
                                Button(
                                    onClick = {
                                        selectedHour = 8
                                        selectedMinute = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("08:00", fontSize = 12.sp)
                                }
                                
                                Button(
                                    onClick = {
                                        selectedHour = 9
                                        selectedMinute = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("09:00", fontSize = 12.sp)
                                }
                                
                                Button(
                                    onClick = {
                                        selectedHour = 20
                                        selectedMinute = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("20:00", fontSize = 12.sp)
                                }
                            }
                        }
                        
                        // Test section - ẩn mặc định, chỉ hiện khi cần debug
                        if (false) { // Thay false thành true nếu muốn hiện test buttons
                            Divider()
                            
                            // Test buttons
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "🧪 Test thông báo (Developer)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // Test ngay lập tức
                                            val intent = Intent(context, NotificationReceiver::class.java)
                                            context.sendBroadcast(intent)
                                            Toast.makeText(context, "Đã gửi thông báo test!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Test ngay", fontSize = 14.sp)
                                    }
                                    
                                    Button(
                                        onClick = {
                                            // Test sau 10 giây
                                            scheduleTestNotification(context, 10)
                                            Toast.makeText(context, "Thông báo test sẽ hiện sau 10 giây", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2196F3)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Test 10s", fontSize = 14.sp)
                                    }
                                }
                            }
                        }
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
                        text = "ℹ️ Lưu ý",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D6E63)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Thông báo hoạt động khi app ở chế độ nền\n• Thông báo VẪN hoạt động khi xóa tiến trình từ đa nhiệm\n• Không hoạt động khi Force Stop app trong Settings\n• Cần cấp quyền POST_NOTIFICATIONS",
                        fontSize = 12.sp,
                        color = Color(0xFF8D6E63)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


/**
 * Lên lịch thông báo hàng ngày
 */
private fun scheduleNotification(context: Context, hour: Int, minute: Int) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Nếu thời gian đã qua hôm nay, lên lịch cho ngày mai
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Sử dụng setExact như test alarm để đảm bảo hoạt động
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        
        // Lên lịch cho 7 ngày tiếp theo để đảm bảo có thông báo
        for (i in 1..7) {
            val nextDayCalendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_MONTH, i)
            }
            
            val nextDayPendingIntent = PendingIntent.getBroadcast(
                context, i, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                nextDayCalendar.timeInMillis,
                nextDayPendingIntent
            )
        }
        
        val nextTime = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(calendar.time)
        
        android.util.Log.d("NotificationSettings", "Đã lên lịch thông báo hàng ngày lúc $nextTime")
        
    } catch (e: Exception) {
        android.util.Log.e("NotificationSettings", "Lỗi khi lên lịch thông báo", e)
    }
}

/**
 * Hủy thông báo
 */
private fun cancelNotification(context: Context) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        
        // Hủy tất cả các alarm (từ 0 đến 7)
        for (i in 0..7) {
            val pendingIntent = PendingIntent.getBroadcast(
                context, i, intent, PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
        
        android.util.Log.d("NotificationSettings", "Đã hủy tất cả thông báo")
    } catch (e: Exception) {
        android.util.Log.e("NotificationSettings", "Lỗi khi hủy thông báo", e)
    }
}

/**
 * Lên lịch thông báo test sau X giây
 */
private fun scheduleTestNotification(context: Context, seconds: Int) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 9999, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val testTime = System.currentTimeMillis() + (seconds * 1000L)
        
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            testTime,
            pendingIntent
        )
        
        android.util.Log.d("NotificationSettings", "Đã lên lịch thông báo test sau $seconds giây")
        
    } catch (e: Exception) {
        android.util.Log.e("NotificationSettings", "Lỗi khi lên lịch thông báo test", e)
    }
}
