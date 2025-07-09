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

/**
 * Màn hình học flashcard - Giao diện chính để người dùng ôn tập flashcard
 * 
 * @param setId ID của bộ flashcard cần học
 * @param onBack Callback khi người dùng nhấn nút quay lại
 * @param onPlayAudio Callback để phát âm thanh (hiện tại chưa được sử dụng)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardStudyScreen(
    setId: String,
    onBack: () -> Unit,
    onPlayAudio: (String) -> Unit = {}
) {
    // Lấy context và khởi tạo ViewModel để quản lý dữ liệu flashcard
    val context = LocalContext.current
    val viewModel: FlashcardViewModel = remember { FlashcardViewModel(context) }
    
    // === RESPONSIVE DESIGN CONFIGURATION ===
    // Lấy thông tin kích thước màn hình để tối ưu giao diện cho các thiết bị khác nhau
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Phân loại kích thước màn hình để áp dụng layout phù hợp
    val isSmallScreen = screenWidth < 360.dp      // Màn hình nhỏ (< 360dp)
    val isVerySmallScreen = screenWidth < 320.dp  // Màn hình rất nhỏ (< 320dp)
    
    // === RESPONSIVE VALUES ===
    // Padding ngang cho toàn bộ màn hình, điều chỉnh theo kích thước màn hình
    val horizontalPadding = when {
        isVerySmallScreen -> 8.dp
        isSmallScreen -> 12.dp
        else -> 16.dp
    }
    
    // Padding ngang cho thẻ flashcard
    val cardHorizontalPadding = when {
        isVerySmallScreen -> 8.dp
        isSmallScreen -> 12.dp
        else -> 16.dp
    }
    
    // Tỷ lệ khung hình của thẻ flashcard, điều chỉnh theo chiều cao màn hình
    val cardAspectRatio = when {
        screenHeight < 600.dp -> 1.6f  // Landscape hoặc màn hình thấp
        isSmallScreen -> 1.3f
        else -> 1.4f
    }
    
    // Kích thước chữ cho các nút, điều chỉnh theo kích thước màn hình
    val buttonTextSize = when {
        isVerySmallScreen -> 12.sp
        isSmallScreen -> 13.sp
        else -> 14.sp
    }

    // Lấy dữ liệu bộ flashcard hiện tại từ ViewModel
    val currentSet by viewModel.currentSet.collectAsState()

    // === STUDY STATE MANAGEMENT ===
    // Chỉ số thẻ hiện tại (bắt đầu từ 0)
    var currentIndex by remember { mutableStateOf(0) }
    
    // Trạng thái lật thẻ (true = đã lật, false = chưa lật)
    var isFlipped by remember { mutableStateOf(false) }
    
    // Chế độ học (từ tiếng Anh sang tiếng Việt, ngược lại, hoặc trộn lẫn)
    var studyMode by remember { mutableStateOf(StudyMode.FRONT_TO_BACK) }
    
    // Trạng thái hiển thị dialog chọn chế độ học
    var showModeDialog by remember { mutableStateOf(false) }
    
    // Trạng thái hiển thị dialog hoàn thành học
    var showCompleteDialog by remember { mutableStateOf(false) }

    // === ANIMATION ===
    // Animation cho hiệu ứng lật thẻ (quay 180 độ theo trục Y)
    val flipAnimation = remember { Animatable(0f) }

    // Khởi tạo dữ liệu khi setId thay đổi
    LaunchedEffect(setId) {
        viewModel.setCurrentSet(setId)
    }

    // Kiểm tra và hiển thị nội dung khi có dữ liệu
    currentSet?.let { set ->
        val flashcards = set.flashcards

        // === EMPTY STATE ===
        // Hiển thị thông báo khi không có thẻ nào để học
        if (flashcards.isEmpty()) {
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

        // Kiểm tra hoàn thành - khi đã học hết tất cả thẻ
        if (currentIndex >= flashcards.size) {
            LaunchedEffect(Unit) {
                showCompleteDialog = true
            }
        }

        // === MAIN CONTENT ===
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainColor)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header chứa nút back, thông tin tiến độ và nút settings
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
                    // Nút quay lại
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(if (isSmallScreen) 28.dp else 32.dp)
                        )
                    }

                    // Hiển thị tiến độ học (thẻ hiện tại / tổng số thẻ)
                    Text(
                        text = "${currentIndex + 1}/${flashcards.size}",
                        fontSize = if (isSmallScreen) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Nút mở dialog cài đặt chế độ học
                    IconButton(onClick = { showModeDialog = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Cài đặt",
                            modifier = Modifier.size(if (isSmallScreen) 28.dp else 32.dp)
                        )
                    }
                }

                // === PROGRESS BAR ===
                // Thanh tiến độ hiển thị % hoàn thành
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

                // === FLASHCARD VIEW ===
                // Hiển thị thẻ flashcard hiện tại với hiệu ứng lật
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

                // === RESPONSIVE CONTROL BUTTONS ===
                // Layout nút điều khiển thay đổi theo kích thước màn hình
                if (isVerySmallScreen) {
                    // Layout dọc cho màn hình rất nhỏ để tránh chen chúc
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Hàng đầu: nút Trước và Tiếp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Nút chuyển về thẻ trước
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

                            // Nút chuyển sang thẻ tiếp theo
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
                        
                        // Hàng thứ hai: nút Lật thẻ (chiếm toàn bộ chiều rộng)
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
                    // Layout ngang cho màn hình bình thường (3 nút trên 1 hàng)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isSmallScreen) 16.dp else 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 6.dp else 8.dp)
                    ) {
                        // Nút Trước
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
                            // Chỉ hiển thị text trên màn hình lớn để tránh chen chúc
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

                        // Nút Lật thẻ
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

                        // Nút Tiếp theo
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

                // === ACTION BUTTONS WHEN FLIPPED ===
                // Hiển thị nút "Biết rồi" và "Chưa biết" khi đã lật thẻ
                if (isFlipped) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isSmallScreen) 16.dp else 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 12.dp)
                    ) {
                        // Nút "Biết rồi" - đánh dấu thẻ đã học
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

                        // Nút "Chưa biết" - đánh dấu thẻ chưa học
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

    // === DIALOGS ===
    // Dialog chọn chế độ học
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

    // Dialog hoàn thành học
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

/**
 * Component hiển thị thẻ flashcard với hiệu ứng lật và responsive design
 * 
 * @param flashcard Dữ liệu thẻ flashcard
 * @param isFlipped Trạng thái lật thẻ
 * @param studyMode Chế độ học hiện tại
 * @param flipAnimation Animation cho hiệu ứng lật
 * @param onFlip Callback khi người dùng chạm để lật thẻ
 * @param onPlayAudio Callback phát âm thanh
 * @param cardAspectRatio Tỷ lệ khung hình của thẻ
 * @param cardHorizontalPadding Padding ngang của thẻ
 * @param isSmallScreen Flag để xác định màn hình nhỏ
 * @param modifier Modifier cho component
 */
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
    // === ANIMATION LOGIC ===
    // Tự động chạy animation khi trạng thái lật thẻ thay đổi
    LaunchedEffect(isFlipped) {
        flipAnimation.animateTo(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    // Xác định mặt nào đang hiển thị
    val showFront = !isFlipped
    
    // Tính toán độ trong suốt cho hiệu ứng lật mượt mà
    val cardAlpha = if (abs(flipAnimation.value % 360 - 90f) < 45f || abs(flipAnimation.value % 360 - 270f) < 45f) {
        0.1f  // Làm mờ khi đang ở giữa quá trình lật
    } else {
        1f    // Hiển thị rõ khi ở mặt trước hoặc sau
    }

    // === RESPONSIVE TEXT SIZES ===
    // Kích thước chữ tự động điều chỉnh theo kích thước màn hình
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

    // === FLASHCARD UI ===
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = cardHorizontalPadding)
            .aspectRatio(cardAspectRatio)
            .graphicsLayer {
                rotationY = flipAnimation.value  // Áp dụng rotation animation
                alpha = cardAlpha                // Áp dụng alpha animation
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onFlip() }  // Phát hiện tap để lật thẻ
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
                // === FRONT SIDE ===
                // Mặt trước của thẻ
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Xác định nội dung hiển thị dựa trên chế độ học
                    val frontText = when (studyMode) {
                        StudyMode.FRONT_TO_BACK -> flashcard.front  // Tiếng Anh -> Tiếng Việt
                        StudyMode.BACK_TO_FRONT -> flashcard.back   // Tiếng Việt -> Tiếng Anh
                        StudyMode.MIXED -> flashcard.front          // Trộn lẫn
                    }

                    // Hiển thị từ chính
                    Text(
                        text = frontText,
                        fontSize = headlineSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isSmallScreen) 3 else 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Hiển thị phiên âm IPA (chỉ khi học từ tiếng Anh sang tiếng Việt)
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

                // Hướng dẫn cho người dùng
                Text(
                    text = "Chạm để lật thẻ",
                    fontSize = bodySize,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else {
                // === BACK SIDE ===
                // Mặt sau của thẻ
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f  // Xoay 180 độ để text không bị ngược
                    }
                ) {
                    // Xác định nội dung hiển thị cho mặt sau
                    val backText = when (studyMode) {
                        StudyMode.FRONT_TO_BACK -> flashcard.back   // Hiển thị nghĩa tiếng Việt
                        StudyMode.BACK_TO_FRONT -> flashcard.front  // Hiển thị từ tiếng Anh
                        StudyMode.MIXED -> flashcard.back           // Trộn lẫn
                    }

                    // Hiển thị nội dung mặt sau
                    Text(
                        text = backText,
                        fontSize = if (isSmallScreen) MaterialTheme.typography.titleLarge.fontSize else MaterialTheme.typography.headlineSmall.fontSize,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isSmallScreen) 4 else 5,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Hiển thị phiên âm IPA (chỉ khi học từ tiếng Việt sang tiếng Anh)
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

                // Hướng dẫn cho người dùng
                Text(
                    text = "Chạm để lật lại",
                    fontSize = bodySize,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .graphicsLayer { rotationY = 180f }  // Xoay 180 độ để text không bị ngược
                )
            }
        }
    }
}

/**
 * Dialog cho phép người dùng chọn chế độ học
 * 
 * @param currentMode Chế độ học hiện tại
 * @param onModeSelected Callback khi chọn chế độ mới
 * @param onDismiss Callback khi đóng dialog
 */
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
                // Chế độ học từ tiếng Anh sang tiếng Việt
                StudyModeOption(
                    mode = StudyMode.FRONT_TO_BACK,
                    label = "Tiếng Anh → Tiếng Việt",
                    description = "Mặt trước: từ tiếng Anh, mặt sau: nghĩa tiếng Việt",
                    isSelected = currentMode == StudyMode.FRONT_TO_BACK,
                    onSelect = { onModeSelected(StudyMode.FRONT_TO_BACK) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Chế độ học từ tiếng Việt sang tiếng Anh
                StudyModeOption(
                    mode = StudyMode.BACK_TO_FRONT,
                    label = "Tiếng Việt → Tiếng Anh",
                    description = "Mặt trước: nghĩa tiếng Việt, mặt sau: từ tiếng Anh",
                    isSelected = currentMode == StudyMode.BACK_TO_FRONT,
                    onSelect = { onModeSelected(StudyMode.BACK_TO_FRONT) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Chế độ học trộn lẫn
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

/**
 * Component hiển thị một lựa chọn chế độ học trong dialog
 * 
 * @param mode Enum chế độ học
 * @param label Nhãn hiển thị cho chế độ
 * @param description Mô tả chi tiết chế độ
 * @param isSelected Trạng thái được chọn
 * @param onSelect Callback khi chọn option này
 */
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
            // Highlight khi được chọn
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radio button để hiển thị trạng thái chọn
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                // Nhãn chế độ học
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Mô tả chi tiết
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 40.dp)  // Indent để thẳng hàng với text ở trên
            )
        }
    }
}

/**
 * Dialog hiển thị khi hoàn thành việc học tất cả flashcard
 * 
 * @param onRestart Callback để học lại từ đầu
 * @param onBack Callback để quay lại màn hình trước
 */
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
                // Icon thành công
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF4CAF50)  // Màu xanh lá
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Thông báo hoàn thành
                Text("Bạn đã hoàn thành việc ôn tập bộ flashcard này!")
            }
        },
        confirmButton = {
            Row {
                // Nút học lại
                TextButton(onClick = onRestart) {
                    Text("Học lại")
                }
                Spacer(modifier = Modifier.width(8.dp))
                
                // Nút hoàn thành và quay lại
                Button(onClick = onBack) {
                    Text("Hoàn thành")
                }
            }
        }
    )
}




