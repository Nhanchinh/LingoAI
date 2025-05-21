package com.example.myapplication.ui.components

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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




@Composable
fun ChatSmartAiChatScreen(
    messages: List<ChatMessage>,
    onBack: () -> Unit = {},
    onRecord: () -> Unit = {},
    onPlayAudio: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3CFE2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                           // .padding(top =40.dp)

                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "ChatSmart AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Danh sách tin nhắn
            Column(
                modifier = Modifier.weight(1f)
            ) {
                messages.forEach { msg ->
                    ChatBubble(msg, onPlayAudio)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Icon mic ở dưới
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onRecord,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(28.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic), // Thay bằng icon mic thật
                        contentDescription = "Mic",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }


        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onPlayAudio: (String) -> Unit
) {
    Column(
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            message.sender,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFD1C4E9),
                modifier = Modifier
                    .defaultMinSize(minWidth = 80.dp)
            ) {
                Text(
                    message.text,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            IconButton(onClick = { onPlayAudio(message.text) }) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play audio",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewChatSmartAiChatScreen() {
    val messages = listOf(
        ChatMessage("HN143", "Hello, I'm Ngan", true),
        ChatMessage("Lingoo", "Hi Ngan, I'm Lingoo.\nCan I help you?", false),
        ChatMessage("HN143", "I want to practice English skills", true),
        ChatMessage("Lingoo", "Okay, let's go.", false)
    )
    ChatSmartAiChatScreen(messages)
}