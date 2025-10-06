package com.example.myapplication.ui.chat

import androidx.compose.runtime.*
import com.example.myapplication.models.Character

/**
 * Main Chat Screen that manages the complete flow:
 * 1. Welcome Screen -> Character Selection -> Chat
 * 2. Handles navigation between screens
 * 3. Manages selected character state
 */
@Composable
fun ChatSmartAiMainScreen(
    onBack: () -> Unit = {},
    onRecordStart: () -> Unit = {},
    onRecordStop: (((String) -> Unit) -> Unit) = {},
    onPlayAudio: (String) -> Unit = {}
) {
    // State để quản lý màn hình hiện tại và character đã chọn
    var currentScreen by remember { mutableStateOf(ChatScreen.Welcome) }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    var initialMessage by remember { mutableStateOf("") }

    when (currentScreen) {
        ChatScreen.Welcome -> {
            ChatSmartAiWelcomeScreen(
                onBack = onBack,
                onRecordStart = onRecordStart,
                onRecordStop = onRecordStop,
                onNavigate = { transcription ->
                    // Khi người dùng ghi âm xong, chuyển đến character selection
                    initialMessage = transcription as? String ?: "Hello! I'm excited to chat with you."
                    currentScreen = ChatScreen.CharacterSelection
                },
                onCharacterSelect = {
                    // Khi người dùng chọn character, chuyển đến character selection
                    println("DEBUG: Character select button clicked, navigating to character selection")
                    // Nếu chưa có initial message, set default
                    if (initialMessage.isEmpty()) {
                        initialMessage = "Hello! I'm excited to chat with you."
                    }
                    currentScreen = ChatScreen.CharacterSelection
                }
            )
        }
        
        ChatScreen.CharacterSelection -> {
            CharacterSelectionScreen(
                onBack = { 
                    currentScreen = ChatScreen.Welcome
                },
                onCharacterSelected = { character ->
                    selectedCharacter = character
                    currentScreen = ChatScreen.Chat
                }
            )
        }
        
        ChatScreen.Chat -> {
            selectedCharacter?.let { character ->
                ChatSmartAiChatScreen(
                    sentence = initialMessage,
                    selectedCharacter = character,
                    onBack = { 
                        currentScreen = ChatScreen.CharacterSelection
                    },
                    onRecordStart = onRecordStart,
                    onRecordStop = onRecordStop,
                    onPlayAudio = onPlayAudio
                )
            }
        }
    }
}

enum class ChatScreen {
    Welcome,
    CharacterSelection,
    Chat
}