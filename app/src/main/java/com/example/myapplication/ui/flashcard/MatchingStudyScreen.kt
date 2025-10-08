package com.example.myapplication.ui.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.ButtonPrimary
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale

@Composable
fun MatchingStudyScreen(
    setId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: FlashcardViewModel = remember { FlashcardViewModel(context) }
    val soundManager = rememberSoundManager()

    val currentSet by viewModel.currentSet.collectAsState()
    
    // Cleanup SoundManager khi component unmount
    SoundManagerEffect(soundManager)

    LaunchedEffect(setId) {
        viewModel.setCurrentSet(setId)
    }

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 360

    currentSet?.let { set ->
        val allCards = set.flashcards
        if (allCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bộ thẻ trống", fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onBack) { Text("Quay lại") }
                }
            }
            return
        }

        // Pagination size per page (ensure even number, at least 4, at most 12)
        val pageSize = when {
            allCards.size < 4 -> 4
            allCards.size < 8 -> 6
            else -> 8
        }

        var pageIndex by remember { mutableStateOf(0) }
        val numPages = ((allCards.size + pageSize - 1) / pageSize).coerceAtLeast(1)

        // Tập thẻ của trang hiện tại (tính lại khi đổi trang; không phụ thuộc trạng thái đã học)
        val pageCards = run {
            val start = pageIndex * pageSize
            val end = (start + pageSize).coerceAtMost(allCards.size)
            allCards.subList(start, end)
        }

        // Build pairs: left column fronts, right column backs, shuffled
        data class Cell(val id: String, val text: String, val isLeft: Boolean)

        // Giữ nguyên thứ tự ô từ lần khởi tạo trang (không xáo lại khi dữ liệu set thay đổi)
        var cells by remember(pageIndex) {
            val start = pageIndex * pageSize
            val end = (start + pageSize).coerceAtMost(allCards.size)
            val slice = allCards.subList(start, end)
            val left = slice.map { Cell("L-${it.id}", it.front, true) }
            val right = slice.map { Cell("R-${it.id}", it.back, false) }
            val seed = setId.hashCode() xor pageIndex
            val combined = (left + right).shuffled(Random(seed))
            mutableStateOf(combined)
        }

        var selectedIndex by remember { mutableStateOf<Int?>(null) }
        var matchedIds by remember { mutableStateOf(setOf<String>()) }

        val totalPairs = pageCards.size
        val progress by remember(matchedIds, totalPairs) {
            mutableStateOf(
                if (totalPairs == 0) 0f else (matchedIds.size.toFloat() / (totalPairs * 2f)).coerceIn(0f, 1f)
            )
        }

        fun isPairMatch(a: Cell, b: Cell): Boolean {
            val leftId = if (a.isLeft) a.id.removePrefix("L-") else a.id.removePrefix("R-")
            val rightId = if (b.isLeft) b.id.removePrefix("L-") else b.id.removePrefix("R-")
            return leftId == rightId && a.isLeft != b.isLeft
        }

        fun baseFlashcardId(wrappedId: String): String =
            wrappedId.removePrefix("L-").removePrefix("R-")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainColor)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Ghép cặp",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    AssistChip(
                        onClick = {},
                        label = {
                            Text("Trang ${pageIndex + 1}/$numPages")
                        }
                    )
                }

                // Progress
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = ButtonPrimary,
                        trackColor = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Đã ghép ${matchedIds.size / 2}/${totalPairs}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }

                // Grid: adaptive to width; ensure even columns
                val columns = if (isCompact) 2 else 3
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cells, key = { it.id }) { cell ->
                        val isMatched = matchedIds.contains(cell.id)
                        val isSelected = cells.indexOf(cell) == selectedIndex
                        val scale by animateFloatAsState(targetValue = if (isSelected) 1.04f else 1f, label = "select-scale")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(92.dp)
                                .scale(scale)
                                .clickable(enabled = !isMatched) {
                                    val current = cells.indexOf(cell)
                                    if (selectedIndex == null) {
                                        selectedIndex = current
                                    } else if (selectedIndex == current) {
                                        selectedIndex = null
                                    } else {
                                        val first = cells[selectedIndex!!]
                                        if (isPairMatch(first, cell)) {
                                            // Phát âm thanh đúng
                                            soundManager.playCorrectSound()
                                            matchedIds = matchedIds + first.id + cell.id
                                            // Mark the underlying flashcard as learned
                                            val learnedId = baseFlashcardId(first.id)
                                            viewModel.updateFlashcardLearnedStatus(setId, learnedId, true)
                                            selectedIndex = null
                                        } else {
                                            // Phát âm thanh sai
                                            soundManager.playWrongSound()
                                            selectedIndex = null
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMatched -> Color(0xFFC8E6C9)
                                    isSelected -> Color(0xFFFFF0F6)
                                    else -> Color.White
                                }
                            ),
                            border = if (isSelected) BorderStroke(2.dp, ButtonPrimary) else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cell.text,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    // No bottom spacer needed; using contentPadding instead
                }

                // Gỡ auto-advance: người dùng tự bấm nút Trang tiếp/Hoàn thành

                // Sticky footer controls
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (pageIndex > 0) {
                                    pageIndex--
                                    selectedIndex = null
                                    matchedIds = emptySet()
                                }
                            },
                            enabled = pageIndex > 0,
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Trang trước") }

                        Button(
                            onClick = {
                                if (matchedIds.size >= pageCards.size * 2) {
                                    if (pageIndex < numPages - 1) {
                                        pageIndex++
                                        selectedIndex = null
                                        matchedIds = emptySet()
                                    } else {
                                        onBack()
                                    }
                                }
                            },
                            enabled = matchedIds.size >= pageCards.size * 2,
                            shape = RoundedCornerShape(16.dp)
                        ) { Text(if (pageIndex < numPages - 1) "Trang tiếp" else "Hoàn thành") }
                    }
                }
            }
        }
    }
}


