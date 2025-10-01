package com.example.myapplication.ui.auth



import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.UserPreferences
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    // Lấy thông tin người dùng từ DataStore
    val username = userPreferences.username.collectAsState(initial = "").value ?: "User"
    val userId = userPreferences.userId.collectAsState(initial = "").value ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
               // .padding(16.dp)
        ) {
            // Header với nút Back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    "Thông tin cá nhân",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Avatar và thông tin người dùng
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tên người dùng
                Text(
                    text = username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ID người dùng
                Text(
                    text = "ID: $userId",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- AI Settings ---
            val voices = listOf(
                "af_heart", "af_bella", "af_sarah", "af_sky",
                "am_michael", "am_onyx", "am_fenrir"
            )

            val currentVoice = userPreferences.aiVoice.collectAsState(initial = "af_heart").value ?: "af_heart"

            var expandedVoice by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text("Cài đặt AI", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Voice selector
                ExposedDropdownMenuBox(
                    expanded = expandedVoice,
                    onExpandedChange = { expandedVoice = !expandedVoice }
                ) {
                    OutlinedTextField(
                        value = currentVoice,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Giọng AI") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedVoice,
                        onDismissRequest = { expandedVoice = false }
                    ) {
                        voices.forEach { v ->
                            DropdownMenuItem(
                                text = { Text(v) },
                                onClick = {
                                    expandedVoice = false
                                    coroutineScope.launch { userPreferences.saveAiVoice(v) }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Nút đăng xuất
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Xóa dữ liệu người dùng
                        userPreferences.clearUserData()

                        // Thông báo đăng xuất thành công
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                        }

                        // Gọi callback đăng xuất
                        onLogout()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp)
            ) {
                Text(
                    "Đăng xuất",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onBack = {},
        onLogout = {}
    )
}