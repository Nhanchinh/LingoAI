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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow


import com.example.myapplication.api.ApiService
import org.json.JSONObject


data class WordDetail(
    val word: String,
    val phonetic: String,
    val meaning: String,
    val synonyms: List<WordItem>,
    val antonyms: List<WordItem>,
    val phrases: List<WordItem>,
    val onBackPressed: () -> Boolean
)

data class WordItem(
    val word: String,
    val phonetic: String,
    val meaning: String
)

@Composable
fun WordDetailScreen(
    word: String,
    onBack: () -> Unit = {},
    onPlayAudio: (String) -> Unit = {},
    onSave: (String) -> Unit = {}
) {
    var wordDetail by remember { mutableStateOf<WordDetail?>(null) }

    LaunchedEffect(word) {
        ApiService.getRelatedWords(word) { code, body ->
            if (code == 200 && body != null) {
                val detail = parseWordDetailFromJson(body, word, onBackPressed = { false })
                wordDetail = detail
            }
        }
    }

    if (wordDetail == null) {
        // Show loading
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        WordDetailContent(
            wordDetail = wordDetail!!,
            onBack = onBack,
            onPlayAudio = onPlayAudio,
            onSave = onSave
        )
    }
}

@Composable
fun WordDetailContent(
    wordDetail: WordDetail,
    onBack: () -> Unit,
    onPlayAudio: (String) -> Unit,
    onSave: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Synonyms", "Antonyms", "Phrases")
    val tabData = listOf(wordDetail.synonyms, wordDetail.antonyms, wordDetail.phrases)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3CFE2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        modifier = Modifier.size(40.dp)

                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* TODO: Bookmark */ }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Bookmark")
                }
            }

            // Word, phonetic, meaning, audio/save
            Text(
                text = wordDetail.word,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = wordDetail.phonetic,
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
                IconButton(onClick = { onPlayAudio(wordDetail.word) }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play audio")
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
                    WordItemRow(item, onPlayAudio, onSave)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))


        }
    }
}

@Composable
fun WordItemRow(
    item: WordItem,
    onPlayAudio: (String) -> Unit,
    onSave: (String) -> Unit
) {
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
                Text(item.word + " " + item.phonetic, fontWeight = FontWeight.Bold)
                Text(item.meaning)
            }
            IconButton(onClick = { onPlayAudio(item.word) }) {
                Icon(Icons.Default.Face, contentDescription = "Play audio")
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
    onBackPressed: () -> Boolean = { true } // Mặc định trả về true hoặc do bạn định nghĩa
) : WordDetail {
    val json = JSONObject(jsonStr)

    val phonetic = json.optString("phonetic", "")
    val meaning = json.optString("meaning", "")

    fun parseList(key: String): List<WordItem> {
        val array = json.optJSONArray(key) ?: return emptyList()
        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            WordItem(
                word = obj.optString("word", ""),
                phonetic = obj.optString("phonetic", ""),
                meaning = obj.optString("meaning", "")
            )
        }
    }

    return WordDetail(
        word = word,
        phonetic = phonetic,
        meaning = meaning,
        synonyms = parseList("synonyms"),
        antonyms = parseList("antonyms"),
        phrases = parseList("phrases"),
        onBackPressed = onBackPressed
    )
}


//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewWordDetailScreen() {
//    val wordDetail = WordDetail(
//        word = "Beautiful",
//        phonetic = "/bjuː.tɪ.fəl/",
//        meaning = "Đẹp, hay, tốt đẹp",
//        synonyms = listOf(
//            WordItem("Attractive", "/əˈtræktɪv/", "Thu hút"),
//            WordItem("Stunning", "/ˈstʌnɪŋ/", "Tuyệt vời, lộng lẫy"),
//            WordItem("Gorgeous", "/ˈɡɔː.dʒəs/", "Rực rỡ, lộng lẫy")
//        ),
//        antonyms = listOf(
//            WordItem("Ugly", "/ˈʌɡli/", "Xấu xí")
//        ),
//        phrases = listOf(
//            WordItem("Beautiful day", "", "Một ngày đẹp trời")
//        )
//    ) { navController.popBackStack() }
//    WordDetailScreen(wordDetail)
//}