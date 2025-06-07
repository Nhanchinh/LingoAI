package com.example.myapplication.ui.flashcard

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R

@Composable
fun FlashcardDetailScreen(
    setId: String,
    onBack: () -> Unit,
    onStartStudy: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: FlashcardViewModel = remember { FlashcardViewModel(context) }
    
    val currentSet: FlashcardSet? by viewModel.currentSet.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(setId) {
        viewModel.setCurrentSet(setId)
    }

    currentSet?.let { set: FlashcardSet ->
        // PATTERN GIỐNG CÁC TRANG KHÁC
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3CFE2))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HEADER như các trang khác
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Thêm thẻ",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // TITLE như các trang khác
                Text(
                    text = set.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // THỐNG KÊ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        title = "Tổng số thẻ",
                        value = set.flashcards.size.toString(),
                        color = Color(0xFF2196F3)
                    )
                    StatCard(
                        title = "Đã học",
                        value = set.flashcards.count { it.isLearned }.toString(),
                        color = Color(0xFF4CAF50)
                    )
                    StatCard(
                        title = "Chưa học",
                        value = set.flashcards.count { !it.isLearned }.toString(),
                        color = Color(0xFFFF9800)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (set.flashcards.isEmpty()) {
                    // EMPTY STATE
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Bộ flashcard trống",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE48ED4)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thêm thẻ đầu tiên")
                        }
                    }
                } else {
                    // LIST FLASHCARDS
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(set.flashcards) { flashcard: Flashcard ->
                            FlashcardItemCard(
                                flashcard = flashcard,
                                onDelete = { viewModel.deleteFlashcard(setId, flashcard.id) },
                                onToggleLearned = {
                                    viewModel.updateFlashcardLearnedStatus(
                                        setId,
                                        flashcard.id,
                                        !flashcard.isLearned
                                    )
                                }

                            )

                        }
                        // Thêm item cuối cùng là Spacer
                        item {
                            Spacer(modifier = Modifier.height(80.dp))  // Điều chỉnh độ cao phù hợp
                        }
                    }
                }
            }

            // FLOATING ACTION BUTTON để bắt đầu học
            if (set.flashcards.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onStartStudy(setId) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = Color(0xFFE48ED4)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Bắt đầu học")
                }
            }
        }

        if (showAddDialog) {
            AddFlashcardDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { front, back, ipa ->
                    viewModel.addFlashcard(setId, front, back, ipa)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun FlashcardItemCard(
    flashcard: Flashcard,
    onDelete: () -> Unit,
    onToggleLearned: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD9D9D9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = flashcard.front,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                if (flashcard.ipa.isNotEmpty()) {
                    Text(
                        text = "/${flashcard.ipa}/",
                        fontSize = 14.sp,
                        color = Color(0xFF2196F3)
                    )
                }
                Text(
                    text = flashcard.back,
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row {
                IconButton(onClick = onToggleLearned) {
                    Icon(
                        if (flashcard.isLearned) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                        contentDescription = if (flashcard.isLearned) "Đã học" else "Chưa học",
                        tint = if (flashcard.isLearned) Color(0xFF4CAF50) else Color.Black.copy(alpha = 0.5f)
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = Color.Black
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa thẻ") },
            text = { Text("Bạn có chắc muốn xóa thẻ này?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun AddFlashcardDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    var ipa by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm thẻ mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = front,
                    onValueChange = { front = it },
                    label = { Text("Từ tiếng Anh") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ipa,
                    onValueChange = { ipa = it },
                    label = { Text("Phiên âm IPA (tùy chọn)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = back,
                    onValueChange = { back = it },
                    label = { Text("Nghĩa tiếng Việt") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(front, back, ipa) },
                enabled = front.isNotBlank() && back.isNotBlank()
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}