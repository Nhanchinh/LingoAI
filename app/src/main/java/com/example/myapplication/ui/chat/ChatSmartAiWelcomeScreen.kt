
package com.example.myapplication.ui.chat

import android.Manifest
import android.util.Log
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.models.Character
import com.example.myapplication.models.VoiceOption
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.MainColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String,
    val text: String,
    val isUser: Boolean
)

@Composable
fun ChatSmartAiWelcomeScreen(
    onBack: () -> Unit = {},
    onRecordStart: () -> Unit = {},
    onRecordStop: (((String) -> Unit) -> Unit) = {},
    onNavigate: (Any?) -> Unit = {},
    onCharacterSelect: () -> Unit = {}, // Deprecated - not used anymore
    onCharacterSelected: ((Character) -> Unit)? = null // New callback for direct character selection
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isProcessingAudio by remember { mutableStateOf(false) }
    var pressStartTime by remember { mutableStateOf(0L) }
    
    // Character management
    var availableCharacters by remember { mutableStateOf<List<Character>>(Character.DEFAULT_CHARACTERS) }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    var showCreateCharacterDialog by remember { mutableStateOf(false) }

    // Animation cho mic xoay
    val infiniteTransition = rememberInfiniteTransition(label = "mic_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Animation cho processing (pulse effect)
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Animation cho sheep logo
    val sheepScale by animateFloatAsState(
        targetValue = if (isRecording) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "sheep_scale"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onRecordStart()
        } else {
            Log.d("Permissions", "Permission denied")
        }
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                }
                
                // Nút chọn Character với visual feedback
                Surface(
                    modifier = Modifier
                        .size(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = ButtonPrimary.copy(alpha = 0.1f),
                    onClick = {
                        Log.d("ChatWelcome", "Character selection button clicked")
                        onCharacterSelect()
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = "Select Character",
                            modifier = Modifier.size(28.dp),
                            tint = ButtonPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sheep logo với animation scale
            Image(
                painter = painterResource(id = R.drawable.sheep_chat),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer(
                        scaleX = sheepScale,
                        scaleY = sheepScale
                    )
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Character Selection Section
            Text(
                "Chọn nhân vật AI:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 20.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Horizontal Character List
            CharacterSelectionRow(
                characters = availableCharacters,
                selectedCharacter = selectedCharacter,
                onCharacterSelected = { character ->
                    selectedCharacter = character
                    Log.d("ChatWelcome", "Selected character: ${character.name}")
                    onCharacterSelected?.invoke(character)
                },
                onCreateNew = {
                    showCreateCharacterDialog = true
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Nhấn giữ để ghi âm",
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mic button với animations
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(ButtonSecondary, shape = RoundedCornerShape(28.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                pressStartTime = System.currentTimeMillis()
                                isRecording = true
                                onRecordStart()

                                val released = tryAwaitRelease()

                                isRecording = false
                                isProcessingAudio = true
                                val pressDuration = System.currentTimeMillis() - pressStartTime

                                if (pressDuration < 300) {
                                    isProcessingAudio = false
                                    Toast.makeText(context, "Bạn cần giữ mic để ghi âm", Toast.LENGTH_SHORT).show()
                                } else {
                                    onRecordStop { transcription ->
                                        Log.d("Transcription", transcription)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            isProcessingAudio = false
                                            onNavigate(transcription)
                                        }
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // CircularProgressIndicator cho cả hai trạng thái
                if (isRecording || isProcessingAudio) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .graphicsLayer(
                                scaleX = if (isProcessingAudio) scale else 1f,
                                scaleY = if (isProcessingAudio) scale else 1f
                            ),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }

                // Mic icon với animation
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = "Mic",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(
                            rotationZ = if (isRecording) rotation else 0f,
                            scaleX = if (isProcessingAudio) scale else 1f,
                            scaleY = if (isProcessingAudio) scale else 1f
                        ),
                    tint = when {
                        isRecording -> Color.Red
                        isProcessingAudio -> Color.Green
                        else -> Color.Black
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Create Character Dialog
        if (showCreateCharacterDialog) {
            CreateCharacterDialog(
                onDismiss = { showCreateCharacterDialog = false },
                onCharacterCreated = { newCharacter: Character ->
                    availableCharacters = availableCharacters + newCharacter
                    selectedCharacter = newCharacter
                    showCreateCharacterDialog = false
                    onCharacterSelected?.invoke(newCharacter)
                }
            )
        }
    }
}

@Composable
fun CharacterSelectionRow(
    characters: List<Character>,
    selectedCharacter: Character?,
    onCharacterSelected: (Character) -> Unit,
    onCreateNew: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(characters) { character ->
            CharacterCard(
                character = character,
                isSelected = selectedCharacter?.name == character.name,
                onClick = { onCharacterSelected(character) }
            )
        }
        
        // Add new character button
        item {
            Card(
                modifier = Modifier
                    .size(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ButtonPrimary.copy(alpha = 0.1f)
                ),
                onClick = onCreateNew
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Character",
                        tint = ButtonPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tạo mới",
                        fontSize = 12.sp,
                        color = ButtonPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: Character,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ButtonPrimary else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Character avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isSelected) Color.White else ButtonPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = character.name,
                    tint = if (isSelected) ButtonPrimary else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Character name
            Text(
                text = character.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else Color.Black,
                maxLines = 1
            )
            
            // Voice indicator
            Text(
                text = "🎤",
                fontSize = 10.sp
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewChatSmartAiWelcomeScreen() {
    ChatSmartAiWelcomeScreen()
}