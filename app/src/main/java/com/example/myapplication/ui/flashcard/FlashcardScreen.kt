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

// Enum cho UI mode selection
enum class UIMode {
    FLASHCARD, VIDEO
}

@Composable
fun FlashcardScreen(
    onBack: () -> Unit,
    onNavItemSelected: (String) -> Unit,
    onCreateSet: () -> Unit,
    onOpenSet: (String) -> Unit,
    onNavigateToVideoStudy: () -> Unit = {}
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
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Chọn phương thức ôn tập",
                fontSize = 18.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // STUDY MODE SELECTION CARDS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card Flashcard - Hiển thị danh sách flashcard
                StudyModeCard(
                    title = "Danh sách từ vựng",
                    icon = Icons.Default.Style,
                    isSelected = false,
                    onClick = { /* Hiển thị danh sách flashcard bên dưới */ },
                    modifier = Modifier.weight(1f)
                )
                
                // Card Video - Navigate đến screen video
                StudyModeCard(
                    title = "Học qua video",
                    icon = Icons.Default.PlayCircle,
                    isSelected = false,
                    onClick = { onNavigateToVideoStudy() },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FLASHCARD CONTENT - Luôn hiển thị danh sách flashcard
            FlashcardContent(
                flashcardSets = flashcardSets,
                isLoading = isLoading,
                onOpenSet = onOpenSet,
                onDeleteSet = { viewModel.deleteFlashcardSet(it) }
            )
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

// STUDY MODE CARD - Card để chọn phương thức học
@Composable
fun StudyModeCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ButtonPrimary else Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) Color.White else ButtonPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// FLASHCARD CONTENT - Nội dung khi chọn danh sách từ vựng
@Composable
fun FlashcardContent(
    flashcardSets: List<FlashcardSet>,
    isLoading: Boolean,
    onOpenSet: (String) -> Unit,
    onDeleteSet: (String) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (flashcardSets.isEmpty()) {
        // EMPTY STATE
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Style,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = ButtonPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Chưa có bộ flashcard nào",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tạo bộ flashcard đầu tiên để bắt đầu học từ vựng",
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    } else {
        // LIST các flashcard sets
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
                    onDelete = { onDeleteSet(set.id) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
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