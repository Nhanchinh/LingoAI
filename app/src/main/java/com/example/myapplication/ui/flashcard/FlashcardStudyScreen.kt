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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.math.abs
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
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
    
    // Responsive configuration
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenWidth < 360.dp
    val isVerySmallScreen = screenWidth < 320.dp
    
    // Responsive values
    val horizontalPadding = when {
        isVerySmallScreen -> 8.dp
        isSmallScreen -> 12.dp
        else -> 16.dp
    }
    
    val cardHorizontalPadding = when {
        isVerySmallScreen -> 8.dp
        isSmallScreen -> 12.dp
        else -> 16.dp
    }
    
    val cardAspectRatio = when {
        screenHeight < 600.dp -> 1.6f  // Landscape hoặc màn hình thấp
        isSmallScreen -> 1.3f
        else -> 1.4f
    }
    
    val buttonTextSize = when {
        isVerySmallScreen -> 12.sp
        isSmallScreen -> 13.sp
        else -> 14.sp
    }

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
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(if (isSmallScreen) 48.dp else 64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Không có thẻ nào để học",
                        fontSize = if (isSmallScreen) 16.sp else 18.sp
                    )
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
                // RESPONSIVE HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (isSmallScreen) 12.dp else 16.dp,
                            start = horizontalPadding,
                            end = horizontalPadding
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(if (isSmallScreen) 28.dp else 32.dp)
                        )
                    }

                    Text(
                        text = "${currentIndex + 1}/${flashcards.size}",
                        fontSize = if (isSmallScreen) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    IconButton(onClick = { showModeDialog = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Cài đặt",
                            modifier = Modifier.size(if (isSmallScreen) 28.dp else 32.dp)
                        )
                    }
                }

                // Responsive Progress bar
                LinearProgressIndicator(
                    progress = (currentIndex + 1).toFloat() / flashcards.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .height(if (isSmallScreen) 6.dp else 8.dp),
                    color = Color(0xFFE48ED4),
                    trackColor = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 32.dp))

                // Responsive Flashcard view
                if (currentIndex < flashcards.size) {
                    ResponsiveFlashcardView(
                        flashcard = flashcards[currentIndex],
                        isFlipped = isFlipped,
                        studyMode = studyMode,
                        flipAnimation = flipAnimation,
                        onFlip = { isFlipped = !isFlipped },
                        onPlayAudio = onPlayAudio,
                        cardAspectRatio = cardAspectRatio,
                        cardHorizontalPadding = cardHorizontalPadding,
                        isSmallScreen = isSmallScreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 32.dp))

                // RESPONSIVE Control buttons
                if (isVerySmallScreen) {
                    // Layout dọc cho màn hình rất nhỏ
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (currentIndex > 0) {
                                        currentIndex--
                                        isFlipped = false
                                    }
                                },
                                enabled = currentIndex > 0,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD9D9D9)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack, 
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Trước", 
                                    color = Color.Black,
                                    fontSize = buttonTextSize,
                                    maxLines = 1
                                )
                            }

                            Button(
                                onClick = {
                                    if (currentIndex < flashcards.size - 1) {
                                        currentIndex++
                                        isFlipped = false
                                    } else {
                                        showCompleteDialog = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE48ED4)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    "Tiếp",
                                    fontSize = buttonTextSize,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.ArrowForward, 
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        Button(
                            onClick = { isFlipped = !isFlipped },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE48ED4)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Info, 
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Lật thẻ",
                                fontSize = buttonTextSize
                            )
                        }
                    }
                } else {
                    // Layout ngang cho màn hình bình thường
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isSmallScreen) 16.dp else 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 6.dp else 8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (currentIndex > 0) {
                                    currentIndex--
                                    isFlipped = false
                                }
                            },
                            enabled = currentIndex > 0,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD9D9D9)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(
                                vertical = if (isSmallScreen) 8.dp else 12.dp,
                                horizontal = if (isSmallScreen) 4.dp else 8.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.ArrowBack, 
                                contentDescription = null,
                                modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                            )
                            if (!isSmallScreen) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Trước", 
                                    color = Color.Black,
                                    fontSize = buttonTextSize,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = { isFlipped = !isFlipped },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE48ED4)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(
                                vertical = if (isSmallScreen) 8.dp else 12.dp,
                                horizontal = if (isSmallScreen) 4.dp else 8.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.Info, 
                                contentDescription = null,
                                modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                            )
                            if (!isSmallScreen) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                "Lật thẻ",
                                fontSize = buttonTextSize,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                if (currentIndex < flashcards.size - 1) {
                                    currentIndex++
                                    isFlipped = false
                                } else {
                                    showCompleteDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE48ED4)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(
                                vertical = if (isSmallScreen) 8.dp else 12.dp,
                                horizontal = if (isSmallScreen) 4.dp else 8.dp
                            )
                        ) {
                            Text(
                                "Tiếp",
                                fontSize = buttonTextSize,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!isSmallScreen) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Icon(
                                Icons.Default.ArrowForward, 
                                contentDescription = null,
                                modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                            )
                        }
                    }
                }

                // Responsive Action buttons khi đã lật thẻ
                if (isFlipped) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isSmallScreen) 16.dp else 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 12.dp)
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
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(
                                vertical = if (isSmallScreen) 8.dp else 12.dp,
                                horizontal = if (isSmallScreen) 4.dp else 8.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.CheckCircle, 
                                contentDescription = null,
                                modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                            )
                            if (!isSmallScreen) {
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                "Biết rồi",
                                fontSize = buttonTextSize,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
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
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(
                                vertical = if (isSmallScreen) 8.dp else 12.dp,
                                horizontal = if (isSmallScreen) 4.dp else 8.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.Close, 
                                contentDescription = null,
                                modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                            )
                            if (!isSmallScreen) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                "Chưa biết",
                                fontSize = buttonTextSize,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 32.dp))
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
fun ResponsiveFlashcardView(
    flashcard: Flashcard,
    isFlipped: Boolean,
    studyMode: StudyMode,
    flipAnimation: Animatable<Float, AnimationVector1D>,
    onFlip: () -> Unit,
    onPlayAudio: (String) -> Unit,
    cardAspectRatio: Float,
    cardHorizontalPadding: Dp,
    isSmallScreen: Boolean,
    modifier: Modifier = Modifier
) {
    // Animation logic
    LaunchedEffect(isFlipped) {
        flipAnimation.animateTo(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    val showFront = !isFlipped
    val cardAlpha = if (abs(flipAnimation.value % 360 - 90f) < 45f || abs(flipAnimation.value % 360 - 270f) < 45f) {
        0.1f
    } else {
        1f
    }

    // Responsive text sizes
    val headlineSize = when {
        isSmallScreen -> MaterialTheme.typography.headlineSmall.fontSize
        else -> MaterialTheme.typography.headlineMedium.fontSize
    }
    
    val titleSize = when {
        isSmallScreen -> MaterialTheme.typography.titleSmall.fontSize
        else -> MaterialTheme.typography.titleMedium.fontSize
    }
    
    val bodySize = when {
        isSmallScreen -> MaterialTheme.typography.bodySmall.fontSize
        else -> MaterialTheme.typography.bodyMedium.fontSize
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = cardHorizontalPadding)
            .aspectRatio(cardAspectRatio)
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
                .padding(if (isSmallScreen) 16.dp else 24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showFront) {
                // FRONT SIDE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val frontText = when (studyMode) {
                        StudyMode.FRONT_TO_BACK -> flashcard.front
                        StudyMode.BACK_TO_FRONT -> flashcard.back
                        StudyMode.MIXED -> flashcard.front
                    }

                    Text(
                        text = frontText,
                        fontSize = headlineSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isSmallScreen) 3 else 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (studyMode == StudyMode.FRONT_TO_BACK && flashcard.ipa.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 16.dp))
                        Text(
                            text = "/${flashcard.ipa}/",
                            fontSize = titleSize,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = "Chạm để lật thẻ",
                    fontSize = bodySize,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else {
                // BACK SIDE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f
                    }
                ) {
                    val backText = when (studyMode) {
                        StudyMode.FRONT_TO_BACK -> flashcard.back
                        StudyMode.BACK_TO_FRONT -> flashcard.front
                        StudyMode.MIXED -> flashcard.back
                    }

                    Text(
                        text = backText,
                        fontSize = if (isSmallScreen) MaterialTheme.typography.titleLarge.fontSize else MaterialTheme.typography.headlineSmall.fontSize,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isSmallScreen) 4 else 5,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (studyMode == StudyMode.BACK_TO_FRONT && flashcard.ipa.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 16.dp))
                        Text(
                            text = "/${flashcard.ipa}/",
                            fontSize = titleSize,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = "Chạm để lật lại",
                    fontSize = bodySize,
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




