package com.example.myapplication.ui.components

import android.util.Log
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp



import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.R

import kotlinx.coroutines.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import com.example.myapplication.api.ApiService
import com.example.myapplication.ui.chat.ChatMessage
import org.json.JSONObject


@Composable
fun ChatSmartAiChatScreen(

    sentence: String,
    onBack: () -> Unit = {},
    onRecordStart: () -> Unit = {},
    onRecordStop: (((String) -> Unit) -> Unit) = {},  // 👈 kiểu có nhận callback
    onPlayAudio: (String) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var messages by remember {
        mutableStateOf(listOf(ChatMessage("User", sentence, true)))
    }

    val isRecording = remember { mutableStateOf(false) }
    val calledUserMessages = remember { mutableStateListOf<String>() } // nhớ những user messages đã gọi API
    // 👇 Khi có tin nhắn mới từ user, gọi API để lấy phản hồi
    LaunchedEffect(messages.lastOrNull()) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage?.isUser == true && !calledUserMessages.contains(lastMessage.text)) {
            // đánh dấu đã gọi API cho message này
            calledUserMessages.add(lastMessage.text)

            ApiService.generateText(lastMessage.text) { code, response ->
                if (code == 200 && response != null) {
                    try {
                        val json = JSONObject(response)
                        val textResponse = json.optString("response", "")
                        if (textResponse.isNotEmpty()) {
                            val aiMessage = ChatMessage("Lingoo", textResponse, false)
                            coroutineScope.launch {
                                messages = messages + aiMessage
                                onPlayAudio(textResponse)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

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
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(28.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isRecording.value = true
                                    onRecordStart()
                                    tryAwaitRelease()  // đợi người dùng thả tay
                                    isRecording.value = false
                                    onRecordStop { transcription ->
                                        Log.d("Transcription", transcription)
                                        if (transcription.isNotBlank()) {
                                            // Thêm message của User với transcription vừa thu được
                                            coroutineScope.launch {
                                                messages = messages + ChatMessage("User", transcription, true)
                                            }
                                        }
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic),
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


//@Preview(showBackground = true)
//@Composable
//fun PreviewChatSmartAiChatScreen() {
//    val messages = listOf(
//        ChatMessage("HN143", "Hello, I'm Ngan", true),
//        ChatMessage("Lingoo", "Hi Ngan, I'm Lingoo.\nCan I help you?", false),
//        ChatMessage("HN143", "I want to practice English skills", true),
//        ChatMessage("Lingoo", "Okay, let's go.", false)
//    )
//    ChatSmartAiChatScreen(messages)
//}