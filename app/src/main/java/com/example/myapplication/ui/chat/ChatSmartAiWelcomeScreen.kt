package com.example.myapplication.ui.components

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp




import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle

import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import com.example.myapplication.ui.components.WordCamScreen


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.LearningProgressSection
import com.example.myapplication.ui.components.LogoutButton
import com.example.myapplication.ui.components.UserHeader
import com.example.myapplication.ui.components.UserInfoSection


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.TopicDropdown
import com.example.myapplication.ui.components.VocabularyList
import kotlinx.coroutines.launch

import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*


import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MainColor
import kotlinx.coroutines.launch














data class ChatMessage(
    val sender: String, // "Lingoo" hoặc "HN143"
    val text: String,
    val isUser: Boolean
)


@Composable
fun ChatSmartAiWelcomeScreen(
    onBack: () -> Unit = {},
    onRecord: () -> Unit = {},
    onNavItemSelected: (String) -> Unit = {} // Thêm tham số này
) {
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
            IconButton(
                onClick = onRecord,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(28.dp))
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

