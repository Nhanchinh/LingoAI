package com.example.myapplication.ui.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MainColor
import kotlin.random.Random

@Composable
fun MultipleChoiceStudyScreen(
    setId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: FlashcardViewModel = remember { FlashcardViewModel(context) }

    val currentSet by viewModel.currentSet.collectAsState()

    LaunchedEffect(setId) {
        viewModel.setCurrentSet(setId)
    }

    currentSet?.let { set ->
        val cards = set.flashcards
        if (cards.isEmpty()) {
            Box(Modifier.fillMaxSize().background(MainColor), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bộ thẻ trống", fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onBack) { Text("Quay lại") }
                }
            }
            return
        }

        var index by remember { mutableStateOf(0) }
        var selected by remember { mutableStateOf<Int?>(null) }
        var correct by remember { mutableStateOf<Int?>(null) }

        val question = cards[index]

        // Build 4 options: 1 correct + 3 random distractors
        val options = remember(index, cards) {
            val others = cards.filter { it.id != question.id }.shuffled(Random(index)).take(3)
            val raw = (others + question).shuffled(Random(index * 31 + 7))
            raw.map { it.back }
        }
        val answerIndex = remember(index) { options.indexOf(question.back) }

        fun nextQuestion() {
            selected = null
            correct = null
            if (index < cards.lastIndex) index++ else onBack()
        }

        Box(
            modifier = Modifier.fillMaxSize().background(MainColor)
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                    Spacer(Modifier.width(8.dp))
                    Text("${index + 1}/${cards.size}", style = MaterialTheme.typography.titleMedium)
                }

                LinearProgressIndicator(
                    progress = (index + 1f) / cards.size,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(6.dp),
                    color = Color(0xFFE48ED4),
                    trackColor = Color.White.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(16.dp))

                // Question card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text("Thuật ngữ", fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = question.front,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Start
                        )

                        Spacer(Modifier.height(24.dp))
                        Text("Chọn đáp án đúng", fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Spacer(Modifier.height(12.dp))

                        options.forEachIndexed { i, opt ->
                            val isChosen = selected == i
                            val isCorrect = correct == i
                            val bg = when {
                                isCorrect -> Color(0xFFC8E6C9)
                                isChosen && correct != null -> Color(0xFFFFCDD2)
                                else -> Color(0xFFF7F7F7)
                            }
                            Button(
                                onClick = {
                                    if (selected == null) {
                                        selected = i
                                        correct = answerIndex
                                        if (i == answerIndex) {
                                            viewModel.updateFlashcardLearnedStatus(setId, question.id, true)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = bg)
                            ) {
                                Text(opt, color = Color.Black, textAlign = TextAlign.Start)
                            }
                        }
                    }
                }

                // Bottom controls
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { nextQuestion() },
                        enabled = selected != null,
                        shape = RoundedCornerShape(16.dp)
                    ) { Text(if (index < cards.lastIndex) "Câu tiếp" else "Kết thúc") }
                }
            }
        }
    }
}


