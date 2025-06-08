package com.example.myapplication.ui.flashcard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.math.abs
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.theme.ButtonSecondary
import com.example.myapplication.ui.theme.MainColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardStudyScreen(
    setId: String,
    onBack: () -> Unit,
    onPlayAudio: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: FlashcardViewModel = remember { FlashcardViewModel(context) }

    val currentSet by viewModel.currentSet.collectAsState()

    // Study state
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var studyMode by remember { mutableStateOf(StudyMode.FRONT_TO_BACK) }
    var showModeDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }

    // Animation
    val flipAnimation = remember { Animatable(0f) }

    LaunchedEffect(setId) {
        viewModel.setCurrentSet(setId)
    }

    currentSet?.let { set ->
        val flashcards = set.flashcards

        if (flashcards.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize()

                ,
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Không có thẻ nào để học")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Quay lại")
                    }
                }
            }
            return@let
        }

        // Kiểm tra hoàn thành
        if (currentIndex >= flashcards.size) {
            LaunchedEffect(Unit) {
                showCompleteDialog = true
            }
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
                // HEADER giống các trang khác
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "${currentIndex + 1}/${flashcards.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    IconButton(onClick = { showModeDialog = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Cài đặt",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Progress bar
                LinearProgressIndicator(
                    progress = (currentIndex + 1).toFloat() / flashcards.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(8.dp),
                    color = Color(0xFFE48ED4),
                    trackColor = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Flashcard view
                if (currentIndex < flashcards.size) {
                    FlashcardView(
                        flashcard = flashcards[currentIndex],
                        isFlipped = isFlipped,
                        studyMode = studyMode,
                        flipAnimation = flipAnimation,
                        onFlip = { isFlipped = !isFlipped },
                        onPlayAudio = onPlayAudio,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Control buttons theo style của dự án
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                isFlipped = false
                            }
                        },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD9D9D9)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                       // Spacer(modifier = Modifier.width(4.dp))
                        Text("Trước", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(4.dp)) // Điều chỉnh giá trị này theo ý muốn
                    Button(
                        onClick = { isFlipped = !isFlipped },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE48ED4)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lật thẻ")
                    }
                    Spacer(modifier = Modifier.width(4.dp)) // Điều chỉnh giá trị này theo ý muốn
                    Button(
                        onClick = {
                            if (currentIndex < flashcards.size - 1) {
                                currentIndex++
                                isFlipped = false
                            } else {
                                showCompleteDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE48ED4)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Tiếp")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons khi đã lật thẻ
                if (isFlipped) {
                    Row(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateFlashcardLearnedStatus(setId, flashcards[currentIndex].id, true)
                                if (currentIndex < flashcards.size - 1) {
                                    currentIndex++
                                    isFlipped = false
                                } else {
                                    showCompleteDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Biết rồi")
                        }

                        Button(
                            onClick = {
                                viewModel.updateFlashcardLearnedStatus(setId, flashcards[currentIndex].id, false)
                                if (currentIndex < flashcards.size - 1) {
                                    currentIndex++
                                    isFlipped = false
                                } else {
                                    showCompleteDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5722)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chưa biết")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Study mode dialog
    if (showModeDialog) {
        StudyModeDialog(
            currentMode = studyMode,
            onModeSelected = { mode ->
                studyMode = mode
                showModeDialog = false
                isFlipped = false
            },
            onDismiss = { showModeDialog = false }
        )
    }

    // Complete dialog
    if (showCompleteDialog) {
        StudyCompleteDialog(
            onRestart = {
                currentIndex = 0
                isFlipped = false
                showCompleteDialog = false
            },
            onBack = onBack
        )
    }
}

@Composable
fun FlashcardView(
    flashcard: Flashcard,
    isFlipped: Boolean,
    studyMode: StudyMode,
    flipAnimation: Animatable<Float, AnimationVector1D>,
    onFlip: () -> Unit,
    onPlayAudio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation logic
    LaunchedEffect(isFlipped) {
        flipAnimation.animateTo(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    // SỬA LOGIC NÀY: Đơn giản hóa điều kiện hiển thị
    val showFront = !isFlipped
    val cardAlpha = if (abs(flipAnimation.value % 360 - 90f) < 45f || abs(flipAnimation.value % 360 - 270f) < 45f) {
        0.1f
    } else {
        1f
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .aspectRatio(1.4f)
            .graphicsLayer {
                rotationY = flipAnimation.value
                alpha = cardAlpha
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onFlip() }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ButtonSecondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // SỬA: Đơn giản hóa logic hiển thị front/back
            if (showFront) {
                // FRONT SIDE - Hiển thị mặt trước
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val frontText = when (studyMode) {
                        StudyMode.FRONT_TO_BACK -> flashcard.front  // Từ tiếng Anh
                        StudyMode.BACK_TO_FRONT -> flashcard.back   // Nghĩa tiếng Việt
                        StudyMode.MIXED -> flashcard.front
                    }

                    Text(
                        text = frontText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Chỉ hiển thị IPA và nút phát âm khi ở chế độ FRONT_TO_BACK (từ EN -> VN)
                    if (studyMode == StudyMode.FRONT_TO_BACK && flashcard.ipa.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "/${flashcard.ipa}/",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
//                        IconButton(
//                            onClick = { onPlayAudio(flashcard.front) }
//                        ) {
//                            Icon(
//                                Icons.Default.PlayArrow,
//                                contentDescription = "Phát âm",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
                    }
                }

                // Tap hint
                Text(
                    text = "Chạm để lật thẻ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else {
                // BACK SIDE - Hiển thị mặt sau
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f  // Lật ngược để text đọc được
                    }
                ) {
                    val backText = when (studyMode) {
                        StudyMode.FRONT_TO_BACK -> flashcard.back   // Nghĩa tiếng Việt
                        StudyMode.BACK_TO_FRONT -> flashcard.front  // Từ tiếng Anh
                        StudyMode.MIXED -> flashcard.back
                    }

                    Text(
                        text = backText,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Hiển thị IPA khi ở chế độ BACK_TO_FRONT và đang hiển thị từ tiếng Anh
                    if (studyMode == StudyMode.BACK_TO_FRONT && flashcard.ipa.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "/${flashcard.ipa}/",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Tap hint cho mặt sau
                Text(
                    text = "Chạm để lật lại",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Composable
fun StudyModeDialog(
    currentMode: StudyMode,
    onModeSelected: (StudyMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chế độ học") },
        text = {
            Column {
                StudyModeOption(
                    mode = StudyMode.FRONT_TO_BACK,
                    label = "Tiếng Anh → Tiếng Việt",
                    description = "Mặt trước: từ tiếng Anh, mặt sau: nghĩa tiếng Việt",
                    isSelected = currentMode == StudyMode.FRONT_TO_BACK,
                    onSelect = { onModeSelected(StudyMode.FRONT_TO_BACK) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                StudyModeOption(
                    mode = StudyMode.BACK_TO_FRONT,
                    label = "Tiếng Việt → Tiếng Anh",
                    description = "Mặt trước: nghĩa tiếng Việt, mặt sau: từ tiếng Anh",
                    isSelected = currentMode == StudyMode.BACK_TO_FRONT,
                    onSelect = { onModeSelected(StudyMode.BACK_TO_FRONT) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                StudyModeOption(
                    mode = StudyMode.MIXED,
                    label = "Trộn lẫn",
                    description = "Ngẫu nhiên giữa hai chế độ trên",
                    isSelected = currentMode == StudyMode.MIXED,
                    onSelect = { onModeSelected(StudyMode.MIXED) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyModeOption(
    mode: StudyMode,
    label: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 40.dp)
            )
        }
    }
}

@Composable
fun StudyCompleteDialog(
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onBack,
        title = { Text("Hoàn thành!") },
        text = {
            Column {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Bạn đã hoàn thành việc ôn tập bộ flashcard này!")
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = onRestart) {
                    Text("Học lại")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onBack) {
                    Text("Hoàn thành")
                }
            }
        }
    )
}




