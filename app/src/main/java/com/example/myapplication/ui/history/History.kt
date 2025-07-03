

package com.example.myapplication.ui.history
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.compose.animation.core.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import androidx.compose.material.icons.filled.Add
import com.example.myapplication.ui.flashcard.FlashcardRepository
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.Refresh

@Composable
fun HistoryScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var historyList by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // THÊM: Biến cho lazy loading với pageSize tăng dần
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPageSize by remember { mutableStateOf(10) } // Bắt đầu với 10
    var hasMoreData by remember { mutableStateOf(true) }

    // Thêm FlashcardRepository
    val flashcardRepository = remember { FlashcardRepository.getInstance(context) }
    val flashcardSets by flashcardRepository.flashcardSets.collectAsState()

    // FUNCTION ĐỂ REFRESH DATA (giữ nguyên + reset pageSize)
    fun refreshHistoryList() {
        isLoading = true
        currentPageSize = 10 // Reset về 10
        hasMoreData = true

        ApiService.getVocabularyList(
            pageSize = currentPageSize,
            pageIndex = 0
        ) { code, body ->
            isLoading = false
            if (code == 200 && body != null) {
                try {
                    val json = JSONObject(body)
                    val dataArray = json.getJSONArray("data")
                    val list = mutableListOf<HistoryItem>()
                    for (i in 0 until dataArray.length()) {
                        val item = dataArray.getJSONObject(i)
                        list.add(
                            HistoryItem(
                                word = item.optString("word"),
                                phonetic = "/" + item.optString("ipa") + "/",
                                meaning = item.optString("meaning")
                            )
                        )
                    }
                    historyList = list
                } catch (e: Exception) {
                    e.printStackTrace()
                    coroutineScope.launch {
                        Toast.makeText(context, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                coroutineScope.launch {
                    Toast.makeText(context, "Không thể tải danh sách từ vựng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // THÊM: Function để load thêm data với pageSize tăng dần
    fun loadMoreData() {
        if (isLoadingMore || !hasMoreData) return

        isLoadingMore = true
        val newPageSize = currentPageSize + 5 // Tăng thêm 5

        ApiService.getVocabularyList(
            pageSize = newPageSize,
            pageIndex = 0
        ) { code, body ->
            isLoadingMore = false
            if (code == 200 && body != null) {
                try {
                    val json = JSONObject(body)
                    val dataArray = json.getJSONArray("data")
                    val newList = mutableListOf<HistoryItem>()
                    for (i in 0 until dataArray.length()) {
                        val item = dataArray.getJSONObject(i)
                        newList.add(
                            HistoryItem(
                                word = item.optString("word"),
                                phonetic = "/" + item.optString("ipa") + "/",
                                meaning = item.optString("meaning")
                            )
                        )
                    }

                    // Check duplicate và chỉ lấy từ mới
                    val currentWords = historyList.map { it.word }.toSet()
                    val newUniqueItems = newList.filter { it.word !in currentWords }

                    if (newUniqueItems.isEmpty()) {
                        // Không có từ mới nào thì dừng
                        hasMoreData = false
                        coroutineScope.launch {
                            Toast.makeText(context, "Không có từ vựng mới", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Thêm từ mới vào danh sách
                        historyList = historyList + newUniqueItems
                        currentPageSize = newPageSize

                        coroutineScope.launch {
                            Toast.makeText(context, "Đã tải thêm ${newUniqueItems.size} từ vựng", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    coroutineScope.launch {
                        Toast.makeText(context, "Lỗi tải thêm dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                coroutineScope.launch {
                    Toast.makeText(context, "Không thể tải thêm từ vựng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Gọi API khi refreshTrigger thay đổi
    LaunchedEffect(refreshTrigger) {
        refreshHistoryList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Nút refresh manual
                IconButton(
                    onClick = {
                        refreshTrigger++
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.sheep_reading),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .padding(top = 0.dp, bottom = 8.dp)
            )

            Text(
                "History",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            } else if (historyList.isEmpty()) {
                // Hiển thị message khi không có từ vựng
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Chưa có từ vựng nào được lưu",
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { refreshTrigger++ }
                        ) {
                            Text("Nhấn để tải lại", color = ButtonPrimary)
                        }
                    }
                }
            } else {
                // THÊM: LazyColumn với lazy loading
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(historyList.size) { idx ->
                        val item = historyList[idx]

                        // THÊM: Kiểm tra scroll đến cuối để load more
                        if (idx == historyList.size - 1 && hasMoreData && !isLoadingMore) {
                            LaunchedEffect(key1 = idx) {
                                loadMoreData()
                            }
                        }

                        HistoryItemCard(
                            item = item,
                            flashcardSets = flashcardSets,
                            onDelete = { wordToDelete ->
                                ApiService.deleteVocabulary(wordToDelete) { code, _ ->
                                    if (code == 200) {
                                        historyList = historyList.filter { it.word != wordToDelete }
                                        coroutineScope.launch {
                                            Toast.makeText(context, "Đã xóa từ vựng", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onAddToFlashcard = { word, phonetic, meaning, setId ->
                                flashcardRepository.addFlashcard(
                                    setId = setId,
                                    front = word,
                                    back = meaning,
                                    ipa = phonetic.replace("/", "").trim()
                                )
                                coroutineScope.launch {
                                    Toast.makeText(context, "Đã thêm '$word' vào flashcard", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCreateFlashcardSet = { name, description ->
                                val newSetId = flashcardRepository.createFlashcardSet(name, description)
                                flashcardRepository.addFlashcard(
                                    setId = newSetId,
                                    front = item.word,
                                    back = item.meaning,
                                    ipa = item.phonetic.replace("/", "").trim()
                                )
                                coroutineScope.launch {
                                    Toast.makeText(context, "Đã tạo bộ flashcard mới và thêm từ", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    // THÊM: Loading indicator khi đang load more
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = ButtonPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Đang tải thêm...",
                                        color = TextSecondary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // THÊM: Message khi không còn data để load
                    if (!hasMoreData && historyList.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Đã hiển thị tất cả từ vựng (${historyList.size} từ)",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation(
    circleColor: Color = ButtonPrimary,
    animationDelay: Int = 300
) {
    // Tạo animation cho 3 dots
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(timeMillis = (animationDelay * index).toLong())
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
        modifier = Modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hiển thị text "Đang tải" cùng với 3 dots
        Text(
            text = "Đang tải",
            color = TextSecondary,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        circles.forEachIndexed { index, animatable ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = circleColor.copy(alpha = animatable.value),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

data class HistoryItem(val word: String, val phonetic: String, val meaning: String)

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    flashcardSets: List<com.example.myapplication.ui.flashcard.FlashcardSet>,
    onDelete: (String) -> Unit,
    onAddToFlashcard: (String, String, String, String) -> Unit,
    onCreateFlashcardSet: (String, String) -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ButtonSecondary, shape = RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Từ tiếng Anh - in đậm, nổi bật
            Text(
                item.word,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,  // In đậm để nổi bật
                color = TextPrimary
            )
            // Phiên âm - in thường, riêng dòng
            Text(
                item.phonetic,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,  // In thường
                color = TextPrimary.copy(alpha = 0.3f)  // Nhạt hơn một chút
            )
            // Nghĩa tiếng Việt
            Text(
                item.meaning,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }

        // Nút thêm vào flashcard
        Box {
            IconButton(
                onClick = { showDropdown = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to Flashcard",
                    tint = ButtonPrimary
                )
            }

            // Dropdown menu với chiều cao cố định và thanh cuộn
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier
                    .heightIn(max = 200.dp) // Giới hạn chiều cao tối đa là 200dp
                    .width(250.dp) // Đặt chiều rộng cố định
            ) {
                // Nội dung dropdown với scroll
                Column(
                    modifier = Modifier
                        .heightIn(max = 180.dp) // Chiều cao nội dung nhỏ hơn 1 chút so với dropdown
                        .verticalScroll(rememberScrollState()) // Thêm scroll
                ) {
                    if (flashcardSets.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Tạo bộ flashcard mới",
                                    fontSize = 14.sp
                                )
                            },
                            onClick = {
                                showDropdown = false
                                showCreateDialog = true
                            }
                        )
                    } else {
                        // Hiển thị danh sách các bộ flashcard hiện có
                        flashcardSets.forEach { set ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = set.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1
                                        )
                                        if (set.description.isNotEmpty()) {
                                            Text(
                                                text = set.description,
                                                fontSize = 12.sp,
                                                color = TextSecondary,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    onAddToFlashcard(item.word, item.phonetic, item.meaning, set.id)
                                    showDropdown = false
                                }
                            )
                        }

                        // Thêm divider
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.5.dp,
                            color = TextSecondary.copy(alpha = 0.3f)
                        )

                        // Tùy chọn tạo bộ mới
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = ButtonPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Tạo bộ mới",
                                        fontSize = 14.sp,
                                        color = ButtonPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            },
                            onClick = {
                                showDropdown = false
                                showCreateDialog = true
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { onDelete(item.word) }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }

    // Dialog tạo flashcard set mới
    if (showCreateDialog) {
        CreateFlashcardSetDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description ->
                onCreateFlashcardSet(name, description)
                showCreateDialog = false
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewHistoryScreen() {
    HistoryScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashcardSetDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Tạo bộ flashcard mới",
                color = TextPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Tên bộ flashcard") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Vui lòng nhập tên bộ flashcard") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả (không bắt buộc)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        name.isBlank() -> nameError = true
                        else -> {
                            onCreate(name.trim(), description.trim())
                        }
                    }
                }
            ) {
                Text(
                    "Tạo",
                    color = ButtonPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Hủy",
                    color = TextPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

