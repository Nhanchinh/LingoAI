



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

// Thêm imports cho LazyColumn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.tooling.preview.Preview


import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.rememberInfiniteTransition



//@Composable
//fun ChatSmartAiChatScreen(
//    sentence: String,
//    onBack: () -> Unit = {},
//    onRecordStart: () -> Unit = {},
//    onRecordStop: (((String) -> Unit) -> Unit) = {},  //  kiểu có nhận callback
//    onPlayAudio: (String) -> Unit = {}
//) {
//    val coroutineScope = rememberCoroutineScope()
//    var messages by remember {
//        mutableStateOf(listOf(ChatMessage("User", sentence, true)))
//    }
//
//    val isRecording = remember { mutableStateOf(false) }
//    val calledUserMessages = remember { mutableStateListOf<String>() } // nhớ những user messages đã gọi API
//
//    // Thêm scrollState cho LazyColumn
//    val scrollState = rememberLazyListState()
//
//    // Tự động cuộn xuống khi có tin nhắn mới
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            scrollState.animateScrollToItem(messages.size - 1)
//        }
//    }
//
//    //  Khi có tin nhắn mới từ user, gọi API để lấy phản hồi
//    LaunchedEffect(messages.lastOrNull()) {
//        val lastMessage = messages.lastOrNull()
//        if (lastMessage?.isUser == true && !calledUserMessages.contains(lastMessage.text)) {
//            // đánh dấu đã gọi API cho message này
//            calledUserMessages.add(lastMessage.text)
//
//            ApiService.generateText(lastMessage.text) { code, response ->
//                if (code == 200 && response != null) {
//                    try {
//                        val json = JSONObject(response)
//                        val textResponse = json.optString("response", "")
//                        if (textResponse.isNotEmpty()) {
//                            val aiMessage = ChatMessage("Lingoo", textResponse, false)
//                            coroutineScope.launch {
//                                messages = messages + aiMessage
//                                onPlayAudio(textResponse)
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF3CFE2))
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp)
//        ) {
//            // Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 40.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
//                        modifier = Modifier.size(32.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    "ChatSmart AI",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 28.sp,
//                    fontFamily = FontFamily.Serif
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Thay thế Column bằng LazyColumn
//            LazyColumn(
//                state = scrollState,
//                modifier = Modifier.weight(1f)
//            ) {
//                items(messages) { msg ->
//                    ChatBubble(msg, onPlayAudio)
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//
//            // Icon mic ở dưới
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 38.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(56.dp)
//                        .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(28.dp))
//                        .pointerInput(Unit) {
//                            detectTapGestures(
//                                onPress = {
//                                    isRecording.value = true
//                                    onRecordStart()
//                                    tryAwaitRelease()  // đợi người dùng thả tay
//                                    isRecording.value = false
//                                    onRecordStop { transcription ->
//                                        Log.d("Transcription", transcription)
//                                        if (transcription.isNotBlank()) {
//                                            // Thêm message của User với transcription vừa thu được
//                                            coroutineScope.launch {
//                                                messages = messages + ChatMessage("User", transcription, true)
//                                            }
//                                        }
//                                    }
//                                }
//                            )
//                        },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_mic),
//                        contentDescription = "Mic",
//                        modifier = Modifier.size(32.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//        }
//    }
//}

@Composable
fun ChatSmartAiChatScreen(
    sentence: String,
    onBack: () -> Unit = {},
    onRecordStart: () -> Unit = {},
    onRecordStop: (((String) -> Unit) -> Unit) = {},  //  kiểu có nhận callback
    onPlayAudio: (String) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var messages by remember {
        mutableStateOf(listOf(ChatMessage("User", sentence, true)))
    }

    // Thêm state để theo dõi trạng thái đang chờ phản hồi
    var isWaitingForResponse by remember { mutableStateOf(false) }

    val isRecording = remember { mutableStateOf(false) }
    val calledUserMessages = remember { mutableStateListOf<String>() } // nhớ những user messages đã gọi API

    // Thêm scrollState cho LazyColumn
    val scrollState = rememberLazyListState()

    // Tự động cuộn xuống khi có tin nhắn mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    //  Khi có tin nhắn mới từ user, gọi API để lấy phản hồi
    LaunchedEffect(messages.lastOrNull()) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage?.isUser == true && !calledUserMessages.contains(lastMessage.text)) {
            // đánh dấu đã gọi API cho message này
            calledUserMessages.add(lastMessage.text)

            // Hiển thị animation chờ
            isWaitingForResponse = true

            ApiService.generateText(lastMessage.text) { code, response ->
                // Khi có phản hồi, ẩn animation chờ
                isWaitingForResponse = false

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

            // Thay thế Column bằng LazyColumn
            LazyColumn(
                state = scrollState,
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg, onPlayAudio)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Hiển thị loading animation khi đang chờ phản hồi
                if (isWaitingForResponse) {
                    item {
                        TypingIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Icon mic ở dưới
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 38.dp),
                horizontalArrangement = Arrangement.Center
            ) {
          if(!isWaitingForResponse){

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
                Spacer(modifier = Modifier.height(16.dp))
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
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            message.sender,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!message.isUser) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFD1C4E9), // Màu tím nhạt cho Lingoo
                    modifier = Modifier
                        .defaultMinSize(minWidth = 80.dp)
                        .weight(1f, fill = false)
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
            } else {
                IconButton(onClick = { onPlayAudio(message.text) }) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play audio",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3F2FD), // Màu xanh nhạt cho User
                    modifier = Modifier
                        .defaultMinSize(minWidth = 80.dp)
                        .weight(1f, fill = false)
                ) {
                    Text(
                        message.text,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun TypingIndicator() {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Lingoo",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFD1C4E9), // Màu tím nhạt cho Lingoo
                modifier = Modifier.defaultMinSize(minWidth = 56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
                ) {
                    LoadingDots()
                }
            }
        }
    }
}

@Composable
fun LoadingDots() {
    val dotSize = 8.dp
    val dotColor = Color(0xFF6A1B9A) // Màu tím đậm

    val infiniteTransition = rememberInfiniteTransition()
    val dotAnimations = List(3) { index ->
        val delay = index * 300 // Mỗi chấm có độ trễ khác nhau
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0f at delay
                    1f at delay + 300
                    0f at delay + 600
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dotAnimations.forEach { animation ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = (-8 * animation.value).dp)
                    .background(dotColor, CircleShape)
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatSmartAiChatScreenPreview() {
    ChatSmartAiChatScreen(
        sentence = "Xin chào, bạn khỏe không?",
        onBack = {},
        onRecordStart = {},
        onRecordStop = { callback -> callback("Tôi khỏe, cảm ơn!") },
        onPlayAudio = { text -> println("Phát âm thanh: $text") }
    )
}
