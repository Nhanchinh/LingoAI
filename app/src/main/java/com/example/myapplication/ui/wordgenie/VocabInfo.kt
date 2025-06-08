package com.example.myapplication.ui.wordgenie

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.api.ApiService
import org.json.JSONObject
import com.example.myapplication.ui.chat.AudioManager
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.TabSelected
import com.example.myapplication.ui.theme.TabUnselected
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.WordItemBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R

data class WordDetail(
    val word: String,
    val phonetic: String,
    val ipa: String,
    val meaning: String,
    val synonyms: List<WordItem>,
    val antonyms: List<WordItem>,
    val phrases: List<WordItem>,
    val onBackPressed: () -> Boolean
)

data class WordItem(
    val word: String,
    val phonetic: String,
    val ipa: String,
    val meaning: String
)

@Composable
fun WordDetailScreen(
    word: String,
    onBack: () -> Unit = {},
    onPlayAudio: (String) -> Unit = {},
    onSave: (String) -> Unit = {}
) {
    val screenId = remember { "vocabinfo_$word" }
    var wordDetail by remember { mutableStateOf<WordDetail?>(null) }

    DisposableEffect(screenId) {
        onDispose {
            AudioManager.onScreenExit(screenId)
        }
    }

    LaunchedEffect(word) {
        ApiService.getRelatedWords(word) { code, body ->
            if (code == 200 && body != null) {
                val detail = parseWordDetailFromJson(body, word, onBackPressed = { false })
                wordDetail = detail
            }
        }
    }

    if (wordDetail == null) {
        LoadingScreenWithAppDesign(word = word, onBack = onBack)
    } else {
        WordDetailContent(
            wordDetail = wordDetail!!,
            screenId = screenId,
            onBack = {
                AudioManager.onScreenExit(screenId)
                onBack()
            },
            onPlayAudio = onPlayAudio,
            onSave = onSave
        )
    }
}

@Composable
fun WordDetailContent(
    wordDetail: WordDetail,
    screenId: String,
    onBack: () -> Unit,
    onPlayAudio: (String) -> Unit,
    onSave: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Synonyms", "Antonyms", "Phrases")
    val tabData = listOf(wordDetail.synonyms, wordDetail.antonyms, wordDetail.phrases)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var isMainWordLoading by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "refresh_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Back",
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* TODO: Bookmark */ }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Bookmark")
                }
            }

            // Word, phonetic/IPA, meaning, audio/save
            Text(
                text = wordDetail.word,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = if (wordDetail.ipa.isNotEmpty()) "/${wordDetail.ipa}/" else wordDetail.phonetic,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = wordDetail.meaning,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = {
                        if (!isMainWordLoading) {
                            AudioManager.stopCurrentAudio()
                            isMainWordLoading = true

                            AudioManager.playAudioFromText(
                                context = context,
                                text = wordDetail.word,
                                screenId = screenId,
                                onStateChange = { isPlaying ->
                                    if (isPlaying) {
                                        kotlinx.coroutines.GlobalScope.launch {
                                            delay(2500)
                                            isMainWordLoading = false
                                        }
                                    } else {
                                        isMainWordLoading = false
                                    }
                                }
                            )
                        }
                    }
                ) {
                    if (isMainWordLoading) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Loading audio",
                            modifier = Modifier.rotate(rotation)
                        )
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play audio")
                    }
                }

                IconButton(onClick = { onSave(wordDetail.word) }) {
                    Icon(Icons.Default.Add, contentDescription = "Save")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Button(
                        onClick = { selectedTab = index },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == index) ButtonSecondary else ButtonPrimary,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(title)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Danh sách từ
            Text(
                text = tabTitles[selectedTab],
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                tabData[selectedTab].forEach { item ->
                    WordItemRowWithSimpleAnimation(item, screenId, onPlayAudio, onSave)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun WordItemRowWithSimpleAnimation(
    item: WordItem,
    screenId: String,
    onPlayAudio: (String) -> Unit,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "item_refresh_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "item_rotation"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ButtonSecondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.word} ${if (item.ipa.isNotEmpty()) "/${item.ipa}/" else item.phonetic}",
                    fontWeight = FontWeight.Bold
                )
                Text(item.meaning)
            }

            IconButton(
                onClick = {
                    if (!isLoading) {
                        AudioManager.stopCurrentAudio()
                        isLoading = true

                        AudioManager.playAudioFromText(
                            context = context,
                            text = item.word,
                            screenId = screenId,
                            onStateChange = { isPlaying ->
                                if (isPlaying) {
                                    kotlinx.coroutines.GlobalScope.launch {
                                        delay(2500)
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
                if (isLoading) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Loading audio",
                        modifier = Modifier.rotate(rotation)
                    )
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play audio")
                }
            }

            IconButton(onClick = { onSave(item.word) }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Save")
            }
        }
    }
}

fun parseWordDetailFromJson(
    jsonStr: String,
    word: String,
    onBackPressed: () -> Boolean = { true }
) : WordDetail {
    val json = JSONObject(jsonStr)

    val phonetic = json.optString("phonetic", "")
    val ipa = json.optString("ipa", "")
    val meaning = json.optString("meaning", "")

    fun parseList(key: String): List<WordItem> {
        val array = json.optJSONArray(key) ?: return emptyList()
        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            WordItem(
                word = obj.optString("word", ""),
                phonetic = obj.optString("phonetic", ""),
                ipa = obj.optString("ipa", ""),
                meaning = obj.optString("meaning", "")
            )
        }
    }

    return WordDetail(
        word = word,
        phonetic = phonetic,
        ipa = ipa,
        meaning = meaning,
        synonyms = parseList("synonyms"),
        antonyms = parseList("antonyms"),
        phrases = parseList("phrases"),
        onBackPressed = onBackPressed
    )
}

@Composable
fun LoadingScreenWithAppDesign(
    word: String,
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_animation")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 8.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack, 
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(120.dp)
                        .rotate(rotation),
                    color = ButtonPrimary,
                    strokeWidth = 3.dp
                )
                
                Image(
                    painter = painterResource(id = R.drawable.sheep_genie),
                    contentDescription = "Loading",
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Đang tìm kiếm từ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "\"$word\"",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = ButtonPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoadingDots()
        }
    }
}

@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots_animation")
    
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 200L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1200 with LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEachIndexed { index, animatable ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = ButtonPrimary.copy(alpha = animatable.value),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
fun SimpleLoadingScreenWithAppDesign(
    word: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 8.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack, 
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.sheep_genie),
                contentDescription = "Loading",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = ButtonPrimary,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Đang tìm kiếm từ \"$word\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Vui lòng đợi...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.7f)
            )
        }
    }
}