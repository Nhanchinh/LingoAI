package com.example.myapplication.demo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.models.Character
import com.example.myapplication.ui.chat.CharacterSelectionScreen
import com.example.myapplication.ui.chat.ChatSmartAiChatScreen

/**
 * Demo composable showing how to integrate Character Selection with Chat
 * This demonstrates the complete flow:
 * 1. User selects a character from CharacterSelectionScreen
 * 2. Chat opens with the selected character
 * 3. AI responses use the character's personality and voice
 */
@Composable
fun CharacterChatDemo() {
    var currentScreen by remember { mutableStateOf<DemoScreen>(DemoScreen.CharacterSelection) }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    var initialMessage by remember { mutableStateOf("Hello! I'm excited to chat with you.") }

    when (currentScreen) {
        DemoScreen.CharacterSelection -> {
            CharacterSelectionScreen(
                onBack = { 
                    // Handle back navigation - could go to main menu
                },
                onCharacterSelected = { character ->
                    selectedCharacter = character
                    currentScreen = DemoScreen.Chat
                }
            )
        }
        
        DemoScreen.Chat -> {
            selectedCharacter?.let { character ->
                ChatSmartAiChatScreen(
                    sentence = initialMessage,
                    selectedCharacter = character,
                    onBack = { 
                        currentScreen = DemoScreen.CharacterSelection
                    },
                    onRecordStart = {
                        // Handle recording start
                    },
                    onRecordStop = { callback ->
                        // Handle recording stop
                        // callback("Recorded text here")
                    },
                    onPlayAudio = { text ->
                        // Handle audio playback
                    }
                )
            }
        }
    }
}

enum class DemoScreen {
    CharacterSelection,
    Chat
}

/**
 * Integration example showing how to add character selection to existing navigation
 */
@Composable
fun ExampleIntegration() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Character Selection Integration Example",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "To integrate character selection into your app:",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("1. Add CharacterSelectionScreen to your navigation")
        Text("2. Pass selected character to ChatSmartAiChatScreen")
        Text("3. Character personality and voice are automatically used")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Features included:",
            style = MaterialTheme.typography.titleMedium
        )
        Text("• Pre-defined characters with different personalities")
        Text("• Custom character creation with personality prompts")
        Text("• Voice selection from 20+ available voices")
        Text("• Character-specific TTS voices")
        Text("• Conversation context and memory")
        Text("• API integration for character management")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { /* Show demo */ }
        ) {
            Text("Run Demo")
        }
    }
}