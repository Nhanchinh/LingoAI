package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person



import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.LearningProgressSection
import com.example.myapplication.ui.components.LogoutButton
import com.example.myapplication.ui.components.UserHeader
import com.example.myapplication.ui.components.UserInfoSection

@Composable
fun HomeScreen() {
    var selectedIndex by remember { mutableStateOf(0) }

    val screens = listOf(
        BottomNavItem("Từ vựng", Icons.Default.Star),
        BottomNavItem("Hội thoại", Icons.Default.MailOutline),
        BottomNavItem("Word Cam", Icons.Default.Search),
        BottomNavItem("User", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> VocabularyScreen()
                1 -> ConversationScreen()
                2 -> WordCamScreen()
                3 -> UserScreen()
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)

@Composable
fun VocabularyScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Từ vựng theo chủ đề", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ConversationScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Các cuộc hội thoại thực tế", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun WordCamScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Word Cam: Nhận diện vật và hiện từ", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun UserScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thông tin người dùng",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )

        // Header ảnh + tên + email
        Box {
            UserHeader(
                name = "Nguyễn Văn A",
                email = "nguyenvana@example.com"
            )
        }

        Spacer(modifier = Modifier.height(64.dp)) // vì avatar bị đẩy xuống nên cần thêm khoảng cách

        // Thành tích hoặc tiến độ học tập
        LearningProgressSection(progress = 0.75f)

        Spacer(modifier = Modifier.height(24.dp))

        // Logout
        LogoutButton(
            onLogout = { /* TODO: xử lý đăng xuất */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(48.dp)
        )
    }
}


