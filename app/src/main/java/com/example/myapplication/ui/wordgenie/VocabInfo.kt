//package com.example.myapplication.ui.wordgenie
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.AddCircle
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.PlayArrow
//// ✅ ANIMATION: Imports cho animation
//import androidx.compose.animation.core.*
//import androidx.compose.material.icons.filled.Refresh
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.platform.LocalContext
//import com.example.myapplication.api.ApiService
//import org.json.JSONObject
//// ✅ AUDIO: Import AudioManager
//import com.example.myapplication.ui.chat.AudioManager
//import com.example.myapplication.ui.common.AudioLifecycleWrapper
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//data class WordDetail(
//    val word: String,
//    val phonetic: String,
//    val ipa: String, // Thêm field IPA
//    val meaning: String,
//    val synonyms: List<WordItem>,
//    val antonyms: List<WordItem>,
//    val phrases: List<WordItem>,
//    val onBackPressed: () -> Boolean
//)
//
//data class WordItem(
//    val word: String,
//    val phonetic: String,
//    val ipa: String, // Thêm field IPA
//    val meaning: String
//)
//
//@Composable
//fun WordDetailScreen(
//    word: String,
//    onBack: () -> Unit = {},
//    onPlayAudio: (String) -> Unit = {},
//    onSave: (String) -> Unit = {}
//) {
//    AudioLifecycleWrapper {
//
//
//        var wordDetail by remember { mutableStateOf<WordDetail?>(null) }
//
//        LaunchedEffect(word) {
//            ApiService.getRelatedWords(word) { code, body ->
//                if (code == 200 && body != null) {
//                    val detail = parseWordDetailFromJson(body, word, onBackPressed = { false })
//                    wordDetail = detail
//                }
//            }
//        }
//
//        if (wordDetail == null) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        } else {
//            WordDetailContent(
//                wordDetail = wordDetail!!,
//                onBack = onBack,
//                onPlayAudio = onPlayAudio,
//                onSave = onSave
//            )
//        }
//    }
//}
//
//// ✅ SIMPLE ANIMATION: WordDetailContent với animation đơn giản
//@Composable
//fun WordDetailContent(
//    wordDetail: WordDetail,
//    onBack: () -> Unit,
//    onPlayAudio: (String) -> Unit,
//    onSave: (String) -> Unit
//) {
//
//
//
//        var selectedTab by remember { mutableStateOf(0) }
//        val tabTitles = listOf("Synonyms", "Antonyms", "Phrases")
//        val tabData = listOf(wordDetail.synonyms, wordDetail.antonyms, wordDetail.phrases)
//        val scrollState = rememberScrollState()
//        val context = LocalContext.current
//
//        // ✅ SIMPLE STATE: Chỉ cần biết có đang loading không
//        var isMainWordLoading by remember { mutableStateOf(false) }
//
//        // ✅ ANIMATION: Rotation animation cho Refresh icon
//        val infiniteTransition = rememberInfiniteTransition(label = "refresh_animation")
//        val rotation by infiniteTransition.animateFloat(
//            initialValue = 0f,
//            targetValue = 360f,
//            animationSpec = infiniteRepeatable(
//                animation = tween(1000, easing = LinearEasing),
//                repeatMode = RepeatMode.Restart
//            ), label = "rotation"
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFFF3CFE2))
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 16.dp)
//                    .verticalScroll(scrollState),
//                verticalArrangement = Arrangement.Center
//            ) {
//                // Header
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 36.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    IconButton(onClick = onBack) {
//                        Icon(
//                            Icons.Default.ArrowBack, contentDescription = "Back",
//                            modifier = Modifier.size(40.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.weight(1f))
//                    IconButton(onClick = { /* TODO: Bookmark */ }) {
//                        Icon(Icons.Default.DateRange, contentDescription = "Bookmark")
//                    }
//                }
//
//                // Word, phonetic/IPA, meaning, audio/save
//                Text(
//                    text = wordDetail.word,
//                    style = MaterialTheme.typography.headlineLarge,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//
//                // Hiển thị IPA nếu có, không thì fallback về phonetic
//                Text(
//                    text = if (wordDetail.ipa.isNotEmpty()) "/${wordDetail.ipa}/" else wordDetail.phonetic,
//                    style = MaterialTheme.typography.bodyLarge,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//
//                Text(
//                    text = wordDetail.meaning,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//                Row(
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                ) {
//                    // ✅ SIMPLE ANIMATION: Play button với animation đơn giản
//                    IconButton(
//                        onClick = {
//                            if (!isMainWordLoading) {
//                                // Stop current audio
//                                AudioManager.stopCurrentAudio()
//
//                                isMainWordLoading = true
//
//                                AudioManager.playAudioFromText(
//                                    context = context,
//                                    text = wordDetail.word,
//                                    onStateChange = { isPlaying ->
//                                        // Sau 2.5 giây trở về icon Play (bất kể audio có đang play hay không)
//                                        if (isPlaying) {
//                                            // Audio bắt đầu phát, sau 2.5s sẽ về Play icon
//                                            kotlinx.coroutines.GlobalScope.launch {
//                                                delay(2500) // 2.5 giây
//                                                isMainWordLoading = false
//                                            }
//                                        } else {
//                                            // Audio lỗi hoặc không load được
//                                            isMainWordLoading = false
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                    ) {
//                        // ✅ SIMPLE ANIMATION: Chỉ Play và Refresh
//                        if (isMainWordLoading) {
//                            Icon(
//                                Icons.Default.Refresh,
//                                contentDescription = "Loading audio",
//                                modifier = Modifier.rotate(rotation)
//                            )
//                        } else {
//                            Icon(Icons.Default.PlayArrow, contentDescription = "Play audio")
//                        }
//                    }
//
//                    IconButton(onClick = { onSave(wordDetail.word) }) {
//                        Icon(Icons.Default.Add, contentDescription = "Save")
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Tabs
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    tabTitles.forEachIndexed { index, title ->
//                        Button(
//                            onClick = { selectedTab = index },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = if (selectedTab == index) Color.White else Color(
//                                    0xFFE0BFD6
//                                ),
//                                contentColor = Color.Black
//                            ),
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(horizontal = 4.dp)
//                        ) {
//                            Text(title)
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Danh sách từ
//                Text(
//                    text = tabTitles[selectedTab],
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Column {
//                    tabData[selectedTab].forEach { item ->
//                        // ✅ SIMPLE ANIMATION: WordItemRow với animation đơn giản
//                        WordItemRowWithSimpleAnimation(item, onPlayAudio, onSave)
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//                }
//
//                Spacer(modifier = Modifier.weight(1f))
//            }
//
//    }
//}
//
//// ✅ SIMPLE ANIMATION: WordItemRow với animation đơn giản
//@Composable
//fun WordItemRowWithSimpleAnimation(
//    item: WordItem,
//    onPlayAudio: (String) -> Unit,
//    onSave: (String) -> Unit
//) {
//
//
//
//
//        val context = LocalContext.current
//        // ✅ SIMPLE STATE: Chỉ loading state
//        var isLoading by remember { mutableStateOf(false) }
//
//        // ✅ ANIMATION: Rotation cho từng item
//        val infiniteTransition = rememberInfiniteTransition(label = "item_refresh_animation")
//        val rotation by infiniteTransition.animateFloat(
//            initialValue = 0f,
//            targetValue = 360f,
//            animationSpec = infiniteRepeatable(
//                animation = tween(1000, easing = LinearEasing),
//                repeatMode = RepeatMode.Restart
//            ), label = "item_rotation"
//        )
//
//        Surface(
//            shape = RoundedCornerShape(16.dp),
//            color = Color(0xFFD9D9D9),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Row(
//                modifier = Modifier
//                    .padding(12.dp)
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    // Hiển thị IPA nếu có, không thì fallback về phonetic
//                    Text(
//                        text = "${item.word} ${if (item.ipa.isNotEmpty()) "/${item.ipa}/" else item.phonetic}",
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(item.meaning)
//                }
//
//                // ✅ SIMPLE ANIMATION: Play button đơn giản
//                IconButton(
//                    onClick = {
//                        if (!isLoading) {
//                            // Stop current audio
//                            AudioManager.stopCurrentAudio()
//
//                            isLoading = true
//
//                            AudioManager.playAudioFromText(
//                                context = context,
//                                text = item.word,
//                                onStateChange = { isPlaying ->
//                                    // Sau 2.5 giây trở về icon Play
//                                    if (isPlaying) {
//                                        kotlinx.coroutines.GlobalScope.launch {
//                                            delay(2500) // 2.5 giây
//                                            isLoading = false
//                                        }
//                                    } else {
//                                        isLoading = false
//                                    }
//                                }
//                            )
//                        }
//                    }
//                ) {
//                    // ✅ SIMPLE ANIMATION: Chỉ Play và Refresh
//                    if (isLoading) {
//                        Icon(
//                            Icons.Default.Refresh,
//                            contentDescription = "Loading audio",
//                            modifier = Modifier.rotate(rotation)
//                        )
//                    } else {
//                        Icon(Icons.Default.PlayArrow, contentDescription = "Play audio")
//                    }
//                }
//
//                IconButton(onClick = { onSave(item.word) }) {
//                    Icon(Icons.Default.AddCircle, contentDescription = "Save")
//                }
//            }
//        }
//
//}
//
//// ✅ CẬP NHẬT: parseWordDetailFromJson để hỗ trợ IPA
//fun parseWordDetailFromJson(
//    jsonStr: String,
//    word: String,
//    onBackPressed: () -> Boolean = { true }
//) : WordDetail {
//    val json = JSONObject(jsonStr)
//
//    val phonetic = json.optString("phonetic", "")
//    val ipa = json.optString("ipa", "") // Thêm parse IPA
//    val meaning = json.optString("meaning", "")
//
//    fun parseList(key: String): List<WordItem> {
//        val array = json.optJSONArray(key) ?: return emptyList()
//        return List(array.length()) { i ->
//            val obj = array.getJSONObject(i)
//            WordItem(
//                word = obj.optString("word", ""),
//                phonetic = obj.optString("phonetic", ""),
//                ipa = obj.optString("ipa", ""), // Thêm parse IPA cho từng item
//                meaning = obj.optString("meaning", "")
//            )
//        }
//    }
//
//    return WordDetail(
//        word = word,
//        phonetic = phonetic,
//        ipa = ipa, // Thêm IPA vào WordDetail
//        meaning = meaning,
//        synonyms = parseList("synonyms"),
//        antonyms = parseList("antonyms"),
//        phrases = parseList("phrases"),
//        onBackPressed = onBackPressed
//    )
//}














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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
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
            .background(Color(0xFFF3CFE2))
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
                            containerColor = if (selectedTab == index) Color.White else Color(0xFFE0BFD6),
                            contentColor = Color.Black
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
        color = Color(0xFFD9D9D9),
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