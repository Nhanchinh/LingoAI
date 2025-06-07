package com.example.myapplication.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Person


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import androidx.compose.foundation.Image

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.R


import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MainColor
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.HomeButtonBackground
import com.example.myapplication.ui.theme.HomeButtonText
import com.example.myapplication.ui.theme.HomeSubtitleText


//
//@Composable
//fun HomeScreen() {
//    var selectedIndex by remember { mutableStateOf(0) }
//
//    val screens = listOf(
//        BottomNavItem("Từ vựng", Icons.Default.Star),
//        BottomNavItem("Hội thoại", Icons.Default.MailOutline),
//        BottomNavItem("Word Cam", Icons.Default.Search),
//        BottomNavItem("Tiny Lesson", Icons.Default.CheckCircle), // cần thêm icon mới
//        BottomNavItem("User", Icons.Default.Person)
//    )
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar {
//                screens.forEachIndexed { index, item ->
//                    NavigationBarItem(
//                        selected = selectedIndex == index,
//                        onClick = { selectedIndex = index },
//                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
//                        label = { Text(item.label) }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(innerPadding)) {
//            when (selectedIndex) {
//                0 -> VocabularyScreen()
//                1 -> ConversationScreen()
//                2 -> WordCamScreen()
//                3 -> TinyLessonScreen()
//                4 -> UserScreen()
//            }
//        }
//    }
//}
//
//data class BottomNavItem(val label: String, val icon: ImageVector)
//
//@Composable
//fun VocabularyScreen() {
//    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text("Từ vựng theo chủ đề", style = MaterialTheme.typography.headlineSmall)
//    }
//}
//
//@Composable
//fun ConversationScreen() {
//    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text("Các cuộc hội thoại thực tế", style = MaterialTheme.typography.headlineSmall)
//    }
//}
//
//
//
//
//
//@Composable
//fun TinyLessonScreen() {
//    val lessonData = mapOf(
//        "Hẹn hò lần đầu" to listOf("Xin chào", "Bạn có khỏe không?", "Rất vui được gặp bạn"),
//        "Bắt taxi" to listOf("Tôi muốn đến...", "Giá bao nhiêu?", "Dừng ở đây nhé"),
//        "Mua sắm" to listOf("Cái này bao nhiêu tiền?", "Tôi muốn mua cái này", "Có giảm giá không?"),
//        "Nhà hàng" to listOf("Cho tôi thực đơn", "Tôi muốn gọi món", "Tính tiền giúp tôi"),
//        "Sân bay" to listOf("Cổng ra máy bay ở đâu?", "Tôi muốn check-in", "Hành lý của tôi đâu?"),
//        "Khách sạn" to listOf("Tôi muốn đặt phòng", "Phòng có bao gồm bữa sáng không?", "Khi nào trả phòng?")
//    )
//
//    var selectedTopic by remember { mutableStateOf(lessonData.keys.first()) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            "🎓 Tiny Lessons",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        TopicDropdown(
//            topics = lessonData.keys.toList(),
//            selectedTopic = selectedTopic,
//            onTopicSelected = { selectedTopic = it }
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        lessonData[selectedTopic]?.let {
//            VocabularyList(vocabulary = it)
//        }
//    }
//}
//
//
//
//@Composable
//fun UserScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(bottom = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Thông tin người dùng",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier
//                .padding(top = 16.dp, bottom = 8.dp)
//                .align(Alignment.Start)
//                .padding(start = 16.dp)
//        )
//
//        // Header ảnh + tên + email
//        Box {
//            UserHeader(
//                name = "Nguyễn Văn A",
//                email = "nguyenvana@example.com"
//            )
//        }
//
//        Spacer(modifier = Modifier.height(64.dp)) // vì avatar bị đẩy xuống nên cần thêm khoảng cách
//
//        // Thành tích hoặc tiến độ học tập
//        LearningProgressSection(progress = 0.75f)
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Logout
//        LogoutButton(
//            onLogout = { /* TODO: xử lý đăng xuất */ },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 32.dp)
//                .height(48.dp)
//        )
//    }
//}

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onWordGenieClick: () -> Unit = {},
    onChatSmartAIClick: () -> Unit = {},
    onVisionaryWordsClick: () -> Unit = {},
    onFlashcardClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        // Icon user góc trên phải
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User",
                tint = TextPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        // Nội dung chính căn giữa
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "LingoAI",
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hãy bắt đầu học nào!",
                color = HomeSubtitleText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            HomeButton("Word Genie", onClick = onWordGenieClick)
            Spacer(modifier = Modifier.height(16.dp))
            HomeButton("ChatSmart AI", onClick = onChatSmartAIClick)
            Spacer(modifier = Modifier.height(16.dp))
            HomeButton("Visionary Words", onClick = onVisionaryWordsClick)
            Spacer(modifier = Modifier.height(16.dp))
            HomeButton("Flashcard", onClick = onFlashcardClick)
            Spacer(modifier = Modifier.height(16.dp))
            HomeButton("History", onClick = onHistoryClick)
        }
    }
}

@Composable
fun HomeButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HomeButtonBackground,
            contentColor = HomeButtonText
        ),
        modifier = Modifier
            .width(280.dp)
            .height(56.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}