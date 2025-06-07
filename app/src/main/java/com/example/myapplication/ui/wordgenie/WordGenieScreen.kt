package com.example.myapplication.ui.wordgenie

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.R
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack

// Thêm imports
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.zIndex
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
// ✅ THÊM: Imports cho smooth animation
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateContentSize

// WordDictionary object (giữ nguyên)
object WordDictionary {
    private var words: List<String> = emptyList()
    private var isLoaded = false

    suspend fun loadWords(context: android.content.Context) {
        if (isLoaded) return

        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("english_words.txt")
                val reader = BufferedReader(InputStreamReader(inputStream))
                words = reader.readLines().map { it.trim().lowercase() }
                reader.close()
                isLoaded = true
            } catch (e: Exception) {
                words = listOf(
                    "hello", "world", "beautiful", "wonderful", "amazing", "fantastic", "excellent",
                    "perfect", "great", "good", "bad", "terrible", "awesome", "incredible", "outstanding",
                    "happy", "sad", "angry", "excited", "nervous", "calm", "peaceful", "stressful",
                    "love", "hate", "like", "dislike", "enjoy", "prefer", "choose", "select",
                    "house", "home", "family", "friend", "teacher", "student", "work", "job",
                    "computer", "phone", "internet", "website", "application", "software", "hardware",
                    "book", "read", "write", "study", "learn", "education", "school", "university",
                    "food", "water", "coffee", "tea", "breakfast", "lunch", "dinner", "restaurant",
                    "travel", "vacation", "holiday", "journey", "adventure", "experience", "memory",
                    "something", "someone", "somewhere", "sometimes", "somewhat", "some", "somebody"
                )
                isLoaded = true
            }
        }
    }

    fun getSuggestion(input: String): String {
        if (input.isBlank() || !isLoaded) return ""
        val lowercaseInput = input.lowercase()
        return words.find {
            it.startsWith(lowercaseInput) && it != lowercaseInput
        } ?: ""
    }

    fun getSuggestions(input: String, limit: Int = 5): List<String> {
        if (input.isBlank() || !isLoaded) return emptyList()
        val lowercaseInput = input.lowercase()
        return words.filter {
            it.startsWith(lowercaseInput) && it != lowercaseInput
        }.take(limit)
    }
}

// ✅ FIX: WordGenieScreen với smooth animated spacing
@Composable
fun WordGenieScreen(
    onBack: () -> Unit,
    onSearchComplete: (String) -> Unit
) {
    var searchWord by remember { mutableStateOf("") }
    val context = LocalContext.current

    var suggestion by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }

    // ✅ THÊM: Animated spacing
    val spacingHeight by animateDpAsState(
        targetValue = if (showDropdown && suggestions.isNotEmpty()) 100.dp else 16.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "spacing_animation"
    )

    LaunchedEffect(Unit) {
        WordDictionary.loadWords(context)
    }

    LaunchedEffect(searchWord) {
        if (searchWord.isNotBlank()) {
            suggestion = WordDictionary.getSuggestion(searchWord)
            suggestions = WordDictionary.getSuggestions(searchWord, 5)
            showDropdown = suggestions.isNotEmpty()
        } else {
            suggestion = ""
            suggestions = emptyList()
            showDropdown = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3CFE2))
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize( // ✅ THÊM: Smooth content size animation
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = "Word Genie",
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Word Genie",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nhập vào từ tiếng anh bạn muốn học",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // TextField với dropdown
            AutoSuggestTextFieldWithDropdown(
                value = searchWord,
                onValueChange = { searchWord = it },
                suggestion = suggestion,
                suggestions = suggestions,
                showDropdown = showDropdown,
                onSuggestionClick = { selectedWord ->
                    searchWord = selectedWord
                    showDropdown = false
                },
                onDismissDropdown = { showDropdown = false },
                modifier = Modifier.fillMaxWidth()
            )

            // ✅ FIX: Animated spacing thay vì fixed
            Spacer(modifier = Modifier.height(spacingHeight))

            Button(
                onClick = {
                    if (searchWord.isBlank()) {
                        Toast.makeText(context, "Bạn phải nhập từ", Toast.LENGTH_SHORT).show()
                    } else {
                        onSearchComplete(searchWord)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD17878),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Tạo", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ✅ AutoSuggestTextField (giữ nguyên)
@Composable
fun AutoSuggestTextFieldWithDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    suggestion: String,
    suggestions: List<String>,
    showDropdown: Boolean,
    onSuggestionClick: (String) -> Unit,
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // TextField container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFD9D9D9), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Background suggestion text
            if (suggestion.isNotEmpty()) {
                Text(
                    text = suggestion,
                    color = Color.Gray.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(suggestion) }
                )
            }

            // Main input field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            "Nhập từ tiếng Anh...",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            )
        }

        // Dropdown với smooth animation
        AnimatedVisibility(
            visible = showDropdown && suggestions.isNotEmpty(),
            enter = expandVertically(
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 65.dp)
                .zIndex(10f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(suggestions) { index, suggestionItem ->
                        Text(
                            text = suggestionItem,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSuggestionClick(suggestionItem)
                                }
                                .padding(16.dp),
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (index < suggestions.size - 1) {
                            HorizontalDivider(
                                color = Color.Gray.copy(alpha = 0.2f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}