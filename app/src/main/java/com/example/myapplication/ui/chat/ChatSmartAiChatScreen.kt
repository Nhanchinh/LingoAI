
package com.example.myapplication.ui.chat

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.models.Character
import com.example.myapplication.models.VoiceOption
import com.example.myapplication.ui.chat.AudioManager
import com.example.myapplication.ui.common.AudioScreenWrapper
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.MainColor

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.json.JSONObject

@Composable
fun ChatSmartAiChatScreen(
    sentence: String,
    selectedCharacter: Character? = null,
    onBack: () -> Unit = {},
    onRecordStart: () -> Unit = {},
    onRecordStop: (((String) -> Unit) -> Unit) = {},
    onPlayAudio: (String) -> Unit = {}
) {
    // Tạo screenId cho audio management
    val screenId = remember { "chat_smart_ai_${System.currentTimeMillis()}" }
    
    // Character management - use selected character or default
    var currentCharacter by remember { 
        mutableStateOf(selectedCharacter ?: Character.DEFAULT_CHARACTERS[1]) // Default to Heart
    }
    
    // Conversation management
    var conversationId by remember { mutableStateOf<String?>(null) }

    // ✅ BỎ AudioScreenWrapper, tự quản lý DisposableEffect
    DisposableEffect(screenId) {
        onDispose {
            AudioManager.onScreenExit(screenId)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var messages by remember {
        mutableStateOf(listOf(ChatMessage("User", sentence, true)))
    }

    // Thêm state để theo dõi trạng thái đang chờ phản hồi
    var isWaitingForResponse by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var isProcessingAudio by remember { mutableStateOf(false) }

    // ✅ THÊM STATE ĐỂ THEO DÕI MESSAGE ĐÃ ĐƯỢC XỬ LÝ
    var lastProcessedMessageIndex by remember { mutableStateOf(-1) }

    // Thêm scrollState cho LazyColumn
    val scrollState = rememberLazyListState()

    // Animation cho mic xoay
    val infiniteTransition = rememberInfiniteTransition(label = "mic_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Animation cho processing
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Tự động cuộn xuống khi có tin nhắn mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    // ✅ SỬA LOGIC HOÀN TOÀN: Sử dụng index để track message đã xử lý
    LaunchedEffect(messages.size) {
        val lastMessage = messages.lastOrNull()
        val currentIndex = messages.size - 1

        // Chỉ gọi API nếu:
        // 1. Message cuối là user message
        // 2. Chưa xử lý message này (index > lastProcessedMessageIndex)
        if (lastMessage?.isUser == true && currentIndex > lastProcessedMessageIndex) {
            Log.d(
                "ChatSmartAI",
                "Processing message at index $currentIndex: ${lastMessage.text}"
            )

            // Đánh dấu đang xử lý message này
            lastProcessedMessageIndex = currentIndex

            // Hiển thị animation chờ
            isWaitingForResponse = true

            // Use enhanced generateText with character and conversation context
            ApiService.generateText(
                query = lastMessage.text,
                characterId = currentCharacter.id.ifEmpty { null },
                conversationId = conversationId,
                userId = null // Will use default USER_ID from ApiService
            ) { code, response ->
                // Khi có phản hồi, ẩn animation chờ
                isWaitingForResponse = false

                if (code == 200 && response != null) {
                    try {
                        val json = JSONObject(response)
                        val textResponse = json.optString("response", "")
                        
                        // Update conversation ID if returned from API
                        val returnedConversationId = json.optString("conversation_id", "")
                        if (returnedConversationId.isNotEmpty() && conversationId == null) {
                            conversationId = returnedConversationId
                            Log.d("ChatSmartAI", "Set conversation ID: $conversationId")
                        }
                        
                        if (textResponse.isNotEmpty()) {
                            val aiMessage = ChatMessage(currentCharacter.name, textResponse, false)
                            coroutineScope.launch {
                                messages = messages + aiMessage
                                Log.d("ChatSmartAI", "Added AI response from ${currentCharacter.name}: $textResponse")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("ChatSmartAI", "Error parsing response: ${e.message}")
                    }
                } else {
                    Log.e("ChatSmartAI", "API call failed with code: $code")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
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
                IconButton(
                    onClick = {
                        // ✅ THÊM manual cleanup
                        AudioManager.onScreenExit(screenId)
                        onBack()
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "ChatSmart AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        "Chatting with ${currentCharacter.name}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily.Default
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Debug info - có thể bỏ sau khi test xong
//            Text(
//                "Messages: ${messages.size}, LastProcessed: $lastProcessedMessageIndex",
//                fontSize = 12.sp,
//                color = Color.Gray,
//                modifier = Modifier.padding(4.dp)
//            )

            // Thay thế Column bằng LazyColumn
            LazyColumn(
                state = scrollState,
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { msg ->
                    ChatBubbleWithAnimation(
                        message = msg, 
                        character = currentCharacter,
                        screenId = screenId,
                        onPlayAudio = onPlayAudio
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Hiển thị loading animation khi đang chờ phản hồi
                if (isWaitingForResponse) {
                    item {
                        TypingIndicator(characterName = currentCharacter.name)
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
                if (!isWaitingForResponse) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = when {
                                    isRecording -> ButtonPrimary
                                    isProcessingAudio -> ButtonPrimary
                                    else -> ButtonSecondary
                                },
                                shape = RoundedCornerShape(28.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isRecording = true
                                        onRecordStart()
                                        tryAwaitRelease()
                                        isRecording = false
                                        isProcessingAudio = true // Bắt đầu xử lý audio
                                        onRecordStop { transcription ->
                                            Log.d("Transcription", "Received: $transcription")
                                            coroutineScope.launch {
                                                if (transcription.isNotBlank()) {
                                                    messages = messages + ChatMessage(
                                                        "User",
                                                        transcription,
                                                        true
                                                    )
                                                    Log.d(
                                                        "ChatSmartAI",
                                                        "Added user message: $transcription"
                                                    )
                                                }
                                                isProcessingAudio =
                                                    false // Kết thúc xử lý audio
                                            }
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // CircularProgressIndicator cho trạng thái recording và processing
                        if (isRecording || isProcessingAudio) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .graphicsLayer(
                                        scaleX = if (isProcessingAudio) pulseAnimation else 1f,
                                        scaleY = if (isProcessingAudio) pulseAnimation else 1f
                                    ),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        }

                        // Icon mic với animation
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = "Mic",
                            modifier = Modifier
                                .size(32.dp)
                                .graphicsLayer(
                                    rotationZ = if (isRecording) rotation else 0f,
                                    scaleX = if (isProcessingAudio) pulseAnimation else 1f,
                                    scaleY = if (isProcessingAudio) pulseAnimation else 1f
                                ),
                            tint = when {
                                isRecording -> Color.Red
                                isProcessingAudio -> Color.Green
                                else -> Color.Black
                            }
                        )
                    }
                }
            }
        }
    }
}

// ✅ CHAT BUBBLE VỚI ANIMATION GIỐNG VOCABINFO
@Composable
fun ChatBubbleWithAnimation(
    message: ChatMessage,
    character: Character,
    screenId: String,
    onPlayAudio: (String) -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    // ✅ ANIMATION: Rotation cho loading
    val infiniteTransition = rememberInfiniteTransition(label = "play_loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

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
                IconButton(
                    onClick = {
                        if (!isLoading) {
                            AudioManager.stopCurrentAudio()
                            isLoading = true

                            // ✅ Sử dụng voice của character cho AI responses
                            AudioManager.playAudioFromText(
                                context = context,
                                text = message.text,
                                screenId = screenId,
                                voice = character.voiceId, // Use character voice for AI responses
                                onStateChange = { isPlaying ->
                                    if (isPlaying) {
                                        kotlinx.coroutines.GlobalScope.launch {
                                            delay(5500)
                                            isLoading = false
                                        }
                                    } else {
                                        isLoading = false
                                    }
                                }
                            )
                        }
                    }
                ) {
                    // ✅ ANIMATION: Play và Refresh icon
                    if (isLoading) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Loading audio",
                            modifier = Modifier.rotate(rotation)
                        )
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play audio",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                IconButton(
                    onClick = {
                        if (!isLoading) {
                            AudioManager.stopCurrentAudio()
                            isLoading = true

                            // ✅ Sử dụng voice mặc định cho user messages (hoặc user voice preference)
                            AudioManager.playAudioFromText(
                                context = context,
                                text = message.text,
                                screenId = screenId,
                                voice = null, // Use default/user preference voice for user messages
                                onStateChange = { isPlaying ->
                                    if (isPlaying) {
                                        kotlinx.coroutines.GlobalScope.launch {
                                            delay(5500)
                                            isLoading = false
                                        }
                                    } else {
                                        isLoading = false
                                    }
                                }
                            )
                        }
                    }
                ) {
                    // ✅ ANIMATION: Play và Refresh icon
                    if (isLoading) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Loading audio",
                            modifier = Modifier.rotate(rotation)
                        )
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play audio",
                            modifier = Modifier.size(20.dp)
                        )
                    }
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
fun TypingIndicator(characterName: String = "Lingoo") {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                characterName,
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
