
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.models.Character
import com.example.myapplication.models.Conversation
import com.example.myapplication.models.VoiceOption
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.MainColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

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
    onNavigate: (String, Character) -> Unit = { _, _ -> },
    onNavigateWithConversation: (Character, String) -> Unit = { _, _ -> }, // New callback for conversation navigation
    onCharacterSelected: ((Character) -> Unit)? = null // New callback for direct character selection
) {
    // Tự động lấy userId từ ApiService
    val userId = ApiService.getUserId()
    val isGuestUser = userId.startsWith("guest_")
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isProcessingAudio by remember { mutableStateOf(false) }
    var pressStartTime by remember { mutableStateOf(0L) }
    
    // Character management
    var availableCharacters by remember { mutableStateOf<List<Character>>(Character.DEFAULT_CHARACTERS) }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    var showCreateCharacterDialog by remember { mutableStateOf(false) }
    var showManageCharactersDialog by remember { mutableStateOf(false) }
    var showConversationsDialog by remember { mutableStateOf<Character?>(null) }
    var isLoadingCharacters by remember { mutableStateOf(false) }

    // Load characters from API when screen initializes
    LaunchedEffect(Unit) {
        isLoadingCharacters = true
        ApiService.getCharacters { statusCode, response ->
            isLoadingCharacters = false
            if (statusCode == 200 && response != null) {
                try {
                    val jsonResponse = JSONObject(response)
                    val charactersArray = jsonResponse.getJSONArray("data")
                    val apiCharacters = Character.fromApiResponseList(charactersArray)
                    
                    // Merge API characters with default characters, avoiding duplicates
                    val allCharacters = mutableListOf<Character>()
                    allCharacters.addAll(Character.DEFAULT_CHARACTERS)
                    
                    // Add API characters that don't have the same name as default characters
                    apiCharacters.forEach { apiChar ->
                        val exists = allCharacters.any { it.name.equals(apiChar.name, ignoreCase = true) }
                        if (!exists) {
                            allCharacters.add(apiChar)
                        }
                    }
                    
                    availableCharacters = allCharacters
                    
                    // Auto-select the first character if none is selected
                    if (selectedCharacter == null && allCharacters.isNotEmpty()) {
                        selectedCharacter = allCharacters[0]
                        onCharacterSelected?.invoke(allCharacters[0])
                    }
                    
                    Log.d("ChatWelcome", "Loaded ${apiCharacters.size} characters from API, total: ${allCharacters.size}")
                } catch (e: Exception) {
                    Log.e("ChatWelcome", "Error parsing characters: ${e.message}")
                    // Auto-select first default character if API fails
                    if (selectedCharacter == null && availableCharacters.isNotEmpty()) {
                        selectedCharacter = availableCharacters[0]
                        onCharacterSelected?.invoke(availableCharacters[0])
                    }
                }
            } else {
                Log.e("ChatWelcome", "Failed to load characters: $statusCode - $response")
                // Auto-select first default character if API fails
                if (selectedCharacter == null && availableCharacters.isNotEmpty()) {
                    selectedCharacter = availableCharacters[0]
                    onCharacterSelected?.invoke(availableCharacters[0])
                }
            }
        }
    }

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                }
                
                // Nút quản lý Character
                IconButton(
                    onClick = { showManageCharactersDialog = true },
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(24.dp),
                        color = ButtonPrimary.copy(alpha = 0.1f),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Edit, 
                                contentDescription = "Manage Characters",
                                modifier = Modifier.size(28.dp),
                                tint = ButtonPrimary
                            )
                        }
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
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Character Selection Section
            Text(
                "Chọn nhân vật AI",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Horizontal Character List with Loading
            if (isLoadingCharacters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = ButtonPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Đang tải nhân vật...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                CharacterSelectionRow(
                    characters = availableCharacters,
                    selectedCharacter = selectedCharacter,
                    onCharacterSelected = { character ->
                        selectedCharacter = character
                        Log.d("ChatWelcome", "Selected character: ${character.name}")
                        onCharacterSelected?.invoke(character)
                    },
                    onCreateNew = {
                        if (isGuestUser) {
                            // Show message for guest users
                            Toast.makeText(
                                context,
                                "Tính năng này chỉ dành cho người dùng có tài khoản. Vui lòng đăng nhập!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            showCreateCharacterDialog = true
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Nhấn giữ để ghi âm",
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))

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

                                tryAwaitRelease()

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
                                            val character = selectedCharacter ?: Character.DEFAULT_CHARACTERS[0]
                                            onNavigate(transcription, character)
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
        
        // Manage Characters Dialog
        if (showManageCharactersDialog) {
            ManageCharactersDialog(
                characters = availableCharacters,
                onDismiss = { showManageCharactersDialog = false },
                onCharacterUpdated = { updatedCharacter ->
                    // Reload characters from API
                    isLoadingCharacters = true
                    ApiService.getCharacters { statusCode, response ->
                        isLoadingCharacters = false
                        if (statusCode == 200 && response != null) {
                            try {
                                val jsonResponse = JSONObject(response)
                                val charactersArray = jsonResponse.getJSONArray("data")
                                val apiCharacters = Character.fromApiResponseList(charactersArray)
                                val allCharacters = mutableListOf<Character>()
                                allCharacters.addAll(Character.DEFAULT_CHARACTERS)
                                apiCharacters.forEach { apiChar ->
                                    val exists = allCharacters.any { it.name.equals(apiChar.name, ignoreCase = true) }
                                    if (!exists) {
                                        allCharacters.add(apiChar)
                                    }
                                }
                                availableCharacters = allCharacters
                            } catch (e: Exception) {
                                Log.e("ChatWelcome", "Error reloading characters: ${e.message}")
                            }
                        }
                    }
                },
                onCharacterDeleted = { deletedCharacter ->
                    availableCharacters = availableCharacters.filter { it.id != deletedCharacter.id }
                    if (selectedCharacter?.id == deletedCharacter.id) {
                        selectedCharacter = availableCharacters.firstOrNull()
                        selectedCharacter?.let { onCharacterSelected?.invoke(it) }
                    }
                },
                onViewConversations = { character ->
                    showManageCharactersDialog = false
                    showConversationsDialog = character
                }
            )
        }
        
        // Conversations List Dialog
        showConversationsDialog?.let { character ->
            ConversationsListDialog(
                character = character,
                onDismiss = { showConversationsDialog = null },
                onConversationSelected = { conversation ->
                    showConversationsDialog = null
                    // Navigate to ChatScreen with conversation history
                    onNavigateWithConversation(character, conversation.id)
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
        
        // Add new character button - always show, but check permission on click
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
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCharactersDialog(
    characters: List<Character>,
    onDismiss: () -> Unit,
    onCharacterUpdated: (Character) -> Unit,
    onCharacterDeleted: (Character) -> Unit,
    onViewConversations: (Character) -> Unit
) {
    val context = LocalContext.current
    var selectedCharacterToEdit by remember { mutableStateOf<Character?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Character?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Quản lý nhân vật",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                if (characters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Chưa có nhân vật nào",
                            color = Color.Gray
                        )
                    }
                } else {
                    // Filter out default characters for editing
                    val editableCharacters = characters.filter { 
                        !Character.DEFAULT_CHARACTERS.any { defaultChar -> defaultChar.name == it.name }
                    }
                    
                    if (editableCharacters.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Chỉ có nhân vật mặc định\n(không thể chỉnh sửa)",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        editableCharacters.forEach { character ->
                            CharacterManageItem(
                                character = character,
                                onEdit = {
                                    selectedCharacterToEdit = character
                                    showEditDialog = true
                                },
                                onDelete = {
                                    showDeleteConfirmDialog = character
                                },
                                onViewConversations = {
                                    onViewConversations(character)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = ButtonPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )

    // Edit Dialog
    if (showEditDialog && selectedCharacterToEdit != null) {
        EditCharacterDialog(
            character = selectedCharacterToEdit!!,
            onDismiss = { 
                showEditDialog = false
                selectedCharacterToEdit = null
            },
            onCharacterUpdated = { updatedCharacter ->
                showEditDialog = false
                selectedCharacterToEdit = null
                onCharacterUpdated(updatedCharacter)
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteConfirmDialog?.let { characterToDelete ->
        AlertDialog(
            onDismissRequest = { 
                if (!isDeleting) showDeleteConfirmDialog = null 
            },
            title = { Text("Xác nhận xóa") },
            text = { 
                Text("Bạn có chắc muốn xóa nhân vật \"${characterToDelete.name}\"?\nMọi hội thoại với nhân vật này cũng sẽ bị xóa.") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        characterToDelete.id?.let { id ->
                            ApiService.deleteCharacter(id) { statusCode, response ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    isDeleting = false
                                    if (statusCode == 200) {
                                        onCharacterDeleted(characterToDelete)
                                        showDeleteConfirmDialog = null
                                        Toast.makeText(context, "Đã xóa nhân vật", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Không thể xóa nhân vật: $response", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = null },
                    enabled = !isDeleting
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun CharacterManageItem(
    character: Character,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewConversations: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = ButtonPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = character.name,
                    tint = ButtonPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Character Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = character.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            
            // Action Buttons
            Row {
                // View Conversations Button
                IconButton(
                    onClick = onViewConversations,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "View Conversations",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Edit Button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = ButtonPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCharacterDialog(
    character: Character,
    onDismiss: () -> Unit,
    onCharacterUpdated: (Character) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(character.name) }
    var personality by remember { mutableStateOf(character.personality) }
    var description by remember { mutableStateOf(character.description) }
    var selectedVoice by remember { mutableStateOf(
        VoiceOption.ALL_VOICES.find { it.id == character.voiceId } ?: VoiceOption.DEFAULT_VOICE
    ) }
    var showVoiceDropdown by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    // Validation states
    var nameError by remember { mutableStateOf(false) }
    var personalityError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Chỉnh sửa nhân vật",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Tên nhân vật (English)") },
                    placeholder = { Text("e.g., Emma, Alex, Teacher...") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Vui lòng nhập tên nhân vật bằng tiếng Anh") }
                    } else {
                        { Text("💡 Nhập bằng tiếng Anh", color = Color.Gray, fontSize = 11.sp) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = false
                    },
                    label = { Text("Mô tả ngắn (English)") },
                    placeholder = { Text("e.g., A friendly English teacher...") },
                    isError = descriptionError,
                    supportingText = if (descriptionError) {
                        { Text("Vui lòng nhập mô tả bằng tiếng Anh") }
                    } else {
                        { Text("💡 Nhập bằng tiếng Anh", color = Color.Gray, fontSize = 11.sp) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Voice selection
                ExposedDropdownMenuBox(
                    expanded = showVoiceDropdown,
                    onExpandedChange = { showVoiceDropdown = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedVoice.name} (${selectedVoice.language})",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Giọng nói") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showVoiceDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonPrimary,
                            unfocusedBorderColor = ButtonSecondary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showVoiceDropdown,
                        onDismissRequest = { showVoiceDropdown = false },
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        VoiceOption.ALL_VOICES.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text("${voice.name} (${voice.language})") },
                                onClick = {
                                    selectedVoice = voice
                                    showVoiceDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Personality field (bigger)
                OutlinedTextField(
                    value = personality,
                    onValueChange = {
                        personality = it
                        personalityError = false
                    },
                    label = { Text("Tính cách & Cách trò chuyện") },
                    placeholder = { Text("Ví dụ: Bạn là một trợ lý thân thiện và hữu ích...") },
                    isError = personalityError,
                    supportingText = if (personalityError) {
                        { Text("Vui lòng nhập tính cách nhân vật") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> nameError = true
                        description.isBlank() -> descriptionError = true
                        personality.isBlank() -> personalityError = true
                        else -> {
                            isUpdating = true
                            character.id?.let { id ->
                                ApiService.updateCharacter(
                                    characterId = id,
                                    name = name.trim(),
                                    personality = personality.trim(),
                                    description = description.trim(),
                                    voice = selectedVoice.id
                                ) { statusCode, response ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        isUpdating = false
                                        if (statusCode == 200) {
                                            val updatedCharacter = character.copy(
                                                name = name.trim(),
                                                personality = personality.trim(),
                                                description = description.trim(),
                                                voiceId = selectedVoice.id
                                            )
                                            onCharacterUpdated(updatedCharacter)
                                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Không thể cập nhật: $response", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = !isUpdating,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Cập nhật", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCharacterCreated: (Character) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var personality by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedVoice by remember { mutableStateOf(VoiceOption.DEFAULT_VOICE) }
    var showVoiceDropdown by remember { mutableStateOf(false) }
    var isCreating by remember { mutableStateOf(false) }

    // Validation states
    var nameError by remember { mutableStateOf(false) }
    var personalityError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Tạo nhân vật mới",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Tên nhân vật (English)") },
                    placeholder = { Text("e.g., Emma, Alex, Teacher...") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Vui lòng nhập tên nhân vật bằng tiếng Anh") }
                    } else {
                        { Text("💡 Nhập bằng tiếng Anh", color = Color.Gray, fontSize = 11.sp) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = false
                    },
                    label = { Text("Mô tả ngắn (English)") },
                    placeholder = { Text("e.g., A friendly English teacher...") },
                    isError = descriptionError,
                    supportingText = if (descriptionError) {
                        { Text("Vui lòng nhập mô tả bằng tiếng Anh") }
                    } else {
                        { Text("💡 Nhập bằng tiếng Anh", color = Color.Gray, fontSize = 11.sp) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Voice selection
                ExposedDropdownMenuBox(
                    expanded = showVoiceDropdown,
                    onExpandedChange = { showVoiceDropdown = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedVoice.name} (${selectedVoice.language})",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Giọng nói") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showVoiceDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonPrimary,
                            unfocusedBorderColor = ButtonSecondary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showVoiceDropdown,
                        onDismissRequest = { showVoiceDropdown = false },
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        VoiceOption.ALL_VOICES.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text("${voice.name} (${voice.language})") },
                                onClick = {
                                    selectedVoice = voice
                                    showVoiceDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Personality field (bigger)
                OutlinedTextField(
                    value = personality,
                    onValueChange = {
                        personality = it
                        personalityError = false
                    },
                    label = { Text("Tính cách & Cách trò chuyện (English)") },
                    placeholder = { Text("You are a friendly and helpful assistant. You speak clearly and explain things well...") },
                    isError = personalityError,
                    supportingText = if (personalityError) {
                        { Text("Vui lòng nhập tính cách bằng tiếng Anh") }
                    } else {
                        { Text("💡 Mô tả tính cách bằng tiếng Anh để AI hiểu rõ hơn", color = Color.Gray, fontSize = 11.sp) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        unfocusedBorderColor = ButtonSecondary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> nameError = true
                        description.isBlank() -> descriptionError = true
                        personality.isBlank() -> personalityError = true
                        else -> {
                            isCreating = true
                            ApiService.createCharacter(
                                name = name.trim(),
                                personality = personality.trim(),
                                description = description.trim(),
                                voice = selectedVoice.id
                            ) { statusCode, response ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    isCreating = false
                                    if (statusCode == 200 || statusCode == 201) {
                                        try {
                                            val jsonResponse = JSONObject(response ?: "{}")
                                            val character = Character.fromApiResponse(jsonResponse)
                                            onCharacterCreated(character)
                                            Toast.makeText(context, "Tạo nhân vật thành công!", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Log.e("CreateCharacter", "Error parsing response: ${e.message}")
                                            Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Không thể tạo nhân vật: $response", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = !isCreating,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Tạo nhân vật", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsListDialog(
    character: Character,
    onDismiss: () -> Unit,
    onConversationSelected: (Conversation) -> Unit
) {
    val context = LocalContext.current
    var conversations by remember { mutableStateOf<List<Conversation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load conversations when dialog opens
    LaunchedEffect(character.id) {
        isLoading = true
        errorMessage = null
        ApiService.getCharacterConversations(character.id) { statusCode, response ->
            isLoading = false
            if (statusCode == 200 && response != null) {
                try {
                    val jsonResponse = JSONObject(response)
                    val conversationsArray = jsonResponse.getJSONArray("data")
                    conversations = Conversation.fromApiResponseList(conversationsArray)
                    Log.d("ConversationsDialog", "Loaded ${conversations.size} conversations for ${character.name}")
                } catch (e: Exception) {
                    Log.e("ConversationsDialog", "Error parsing conversations: ${e.message}")
                    errorMessage = "Lỗi khi tải hội thoại"
                }
            } else {
                errorMessage = "Không thể tải hội thoại"
                Log.e("ConversationsDialog", "Failed to load conversations: $statusCode - $response")
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    "Hội thoại với ${character.name}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${conversations.size} cuộc hội thoại",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                when {
                    isLoading -> {
                        // Loading state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = ButtonPrimary,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Đang tải hội thoại...",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    errorMessage != null -> {
                        // Error state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "❌",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    errorMessage!!,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    conversations.isEmpty() -> {
                        // Empty state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "💬",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Chưa có hội thoại nào",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Hãy bắt đầu trò chuyện với ${character.name}!",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        // Conversations list
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(conversations) { conversation ->
                                ConversationItem(
                                    conversation = conversation,
                                    onClick = { onConversationSelected(conversation) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = ButtonPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Conversation Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = ButtonPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "💬",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Conversation Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = conversation.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Show message count
                Text(
                    text = "${conversation.messages.size} tin nhắn",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            
            // Arrow icon
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Open conversation",
                tint = ButtonPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatSmartAiWelcomeScreen() {
    ChatSmartAiWelcomeScreen()
}