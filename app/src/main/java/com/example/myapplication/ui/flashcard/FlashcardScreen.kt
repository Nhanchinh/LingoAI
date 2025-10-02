package com.example.myapplication.ui.flashcard

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
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.TextPrimary

@Composable
fun FlashcardScreen(
    onBack: () -> Unit,
    onNavItemSelected: (String) -> Unit,
    onCreateSet: () -> Unit,
    onOpenSet: (String) -> Unit
) {
    val context = LocalContext.current
    // Quay về cách khởi tạo ViewModel đơn giản ban đầu
    val viewModel: FlashcardViewModel = remember { FlashcardViewModel(context) }
    
    // Specify explicit types
    val flashcardSets: List<FlashcardSet> by viewModel.flashcardSets.collectAsState()
    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val error: String? by viewModel.error.collectAsState()
    
    // State cho dialog tạo bộ mới
    var showCreateDialog by remember { mutableStateOf(false) }

    // PATTERN GIỐNG CÁC TRANG KHÁC: Box với background màu pastel
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)  // Thay Color(0xFFF3CFE2)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER với ArrowBack như các trang khác
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
                        modifier = Modifier.size(32.dp) // Size giống các trang khác
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // Add button ở góc phải
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(
                        Icons.Default.Add, 
                        contentDescription = "Tạo bộ mới",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // LOGO VÀ TITLE như các trang khác
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = "Flashcard",
                modifier = Modifier
                    .size(140.dp)
                    .padding(top = 0.dp, bottom = 8.dp)
            )
            
            Text(
                text = "Learning",
                fontSize = 36.sp, // Size giống History
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Tạo và học từ vựng với thẻ ghi nhớ",
                fontSize = 18.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (flashcardSets.isEmpty()) {
                // EMPTY STATE theo pattern của VisionaryWords
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Chưa có bộ flashcard nào",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,  // Thay Color.Black
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonPrimary  // Thay Color(0xFFE48ED4)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tạo bộ flashcard đầu tiên")
                    }
                }
            } else {
                // LIST các flashcard sets theo pattern History
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(flashcardSets) { set: FlashcardSet ->
                        FlashcardSetCard(
                            set = set,
                            onClick = { onOpenSet(set.id) },
                            onDelete = { viewModel.deleteFlashcardSet(set.id) }
                        )
                    }
                    // Thêm item cuối cùng là một Spacer
                    item {
                        Spacer(modifier = Modifier.height(10.dp)) // Điều chỉnh giá trị này theo ý muốn
                    }
                }
            }
        }
    }

    // Dialog tạo bộ mới
    if (showCreateDialog) {
        CreateFlashcardSetDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description ->
                viewModel.createFlashcardSet(name, description)
                showCreateDialog = false
            }
        )
    }

    // Hiển thị lỗi
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            viewModel.clearError()
        }
    }
}


// CARD theo pattern History
@Composable
fun FlashcardSetCard(
    set: FlashcardSet,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp), // Giống History
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD9D9D9) // Màu giống History
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
                    text = set.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (set.description.isNotEmpty()) {
                    Text(
                        text = set.description,
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Thông tin thống kê
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${set.flashcards.size} thẻ",
                        fontSize = 12.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                    
                    if (set.flashcards.isNotEmpty()) {
                        val learnedCount = set.flashcards.count { it.isLearned }
                        Text(
                            text = "$learnedCount/${set.flashcards.size} đã học",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = TextPrimary
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa bộ flashcard") },
            text = { Text("Bạn có chắc muốn xóa bộ flashcard \"${set.name}\"?") },
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
fun CreateFlashcardSetDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo bộ flashcard mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên bộ flashcard") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả (tùy chọn)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}