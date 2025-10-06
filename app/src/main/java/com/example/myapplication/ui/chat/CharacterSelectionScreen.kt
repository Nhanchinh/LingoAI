package com.example.myapplication.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.api.ApiService
import com.example.myapplication.models.Character
import com.example.myapplication.models.VoiceOption
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.MainColor
import org.json.JSONObject
import org.json.JSONArray

@Composable
fun CharacterSelectionScreen(
    onBack: () -> Unit,
    onCharacterSelected: (Character) -> Unit
) {
    var characters by remember { mutableStateOf<List<Character>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Load characters from API
    LaunchedEffect(Unit) {
        ApiService.getCharacters { code, response ->
            if (code == 200 && response != null) {
                try {
                    val json = JSONObject(response)
                    val charactersArray = json.getJSONArray("characters")
                    characters = Character.fromApiResponseList(charactersArray)
                } catch (e: Exception) {
                    // If no characters exist or error, show default characters
                    characters = Character.DEFAULT_CHARACTERS
                }
            } else {
                // Show default characters if API fails
                characters = Character.DEFAULT_CHARACTERS
            }
            isLoading = false
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
                    .padding(top = 40.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Choose Your AI Character",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { showCreateDialog = true }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Create Character",
                        modifier = Modifier.size(28.dp),
                        tint = ButtonPrimary
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(characters) { character ->
                        CharacterCard(
                            character = character,
                            onClick = { onCharacterSelected(character) }
                        )
                    }
                }
            }
        }

        // Create Character Dialog
        if (showCreateDialog) {
            CreateCharacterDialog(
                onDismiss = { showCreateDialog = false },
                onCharacterCreated = { newCharacter: Character ->
                    characters = characters + newCharacter
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit
) {
    val voiceOption = VoiceOption.getVoiceById(character.voiceId)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = ButtonPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = ButtonPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Character Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                
                Text(
                    text = character.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Voice info
                voiceOption?.let { voice ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Voice: ${voice.name} (${voice.language})",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        
                        // Voice gender indicator
                        val genderEmoji = if (voice.gender == VoiceOption.Gender.FEMALE) "👩" else "👨"
                        Text(
                            text = genderEmoji,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // Play button for voice preview
            IconButton(
                onClick = {
                    // TODO: Add voice preview functionality
                }
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Preview Voice",
                    tint = ButtonPrimary
                )
            }
        }
    }
}

@Composable
fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCharacterCreated: (Character) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var personality by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedVoice by remember { mutableStateOf(VoiceOption.DEFAULT_VOICE) }
    var showVoiceSelector by remember { mutableStateOf(false) }
    var isCreating by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Character")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = personality,
                    onValueChange = { personality = it },
                    label = { Text("Personality") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    placeholder = { Text("Describe how this character should behave and respond...") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Voice Selector
                OutlinedTextField(
                    value = "${selectedVoice.name} (${selectedVoice.language})",
                    onValueChange = { },
                    label = { Text("Voice") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showVoiceSelector = true },
                    readOnly = true,
                    trailingIcon = {
                        Text(
                            text = if (selectedVoice.gender == VoiceOption.Gender.FEMALE) "👩" else "👨",
                            fontSize = 16.sp
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && personality.isNotBlank() && description.isNotBlank()) {
                        isCreating = true
                        ApiService.createCharacter(
                            name = name,
                            personality = personality,
                            description = description,
                            voice = selectedVoice.id
                        ) { code, response ->
                            isCreating = false
                            if (code == 200 || code == 201) {
                                // Create character object (even if API fails, we can use local version)
                                val newCharacter = Character(
                                    name = name,
                                    personality = personality,
                                    description = description,
                                    voiceId = selectedVoice.id
                                )
                                onCharacterCreated(newCharacter)
                            } else {
                                // Still create local character if API fails
                                val newCharacter = Character(
                                    name = name,
                                    personality = personality,
                                    description = description,
                                    voiceId = selectedVoice.id
                                )
                                onCharacterCreated(newCharacter)
                            }
                        }
                    }
                },
                enabled = !isCreating && name.isNotBlank() && personality.isNotBlank() && description.isNotBlank()
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Create")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Voice Selection Dialog
    if (showVoiceSelector) {
        VoiceSelectionDialog(
            currentVoice = selectedVoice,
            onVoiceSelected = { voice ->
                selectedVoice = voice
                showVoiceSelector = false
            },
            onDismiss = { showVoiceSelector = false }
        )
    }
}

@Composable
fun VoiceSelectionDialog(
    currentVoice: VoiceOption,
    onVoiceSelected: (VoiceOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Voice") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Group voices by language
                val voicesByLanguage = VoiceOption.ALL_VOICES.groupBy { it.language }
                
                voicesByLanguage.forEach { (language, voices) ->
                    item {
                        Text(
                            text = language,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(voices) { voice ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onVoiceSelected(voice) }
                                .padding(8.dp)
                                .let { 
                                    if (voice.id == currentVoice.id) {
                                        it.border(2.dp, ButtonPrimary, RoundedCornerShape(8.dp))
                                    } else it
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (voice.gender == VoiceOption.Gender.FEMALE) "👩" else "👨",
                                fontSize = 16.sp,
                                modifier = Modifier.width(32.dp)
                            )
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = voice.name,
                                    fontWeight = FontWeight.Medium
                                )
                                if (voice.overallGrade.isNotEmpty()) {
                                    Text(
                                        text = "Quality: ${voice.overallGrade}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            
                            IconButton(
                                onClick = {
                                    // TODO: Add voice preview
                                }
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Preview",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}