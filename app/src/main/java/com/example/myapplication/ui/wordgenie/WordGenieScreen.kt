package com.example.myapplication.ui.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import androidx.compose.foundation.Image

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.R


import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview


import com.example.myapplication.ui.components.BottomNavBar
@Composable
fun WordGenieScreen(
    onBack: () -> Unit,
    onNavItemSelected: (String) -> Unit,
    onSearchComplete: (String) -> Unit // Thêm callback này
) {
    var searchWord by remember { mutableStateOf("Beautiful") } // Thêm state để lưu từ nhập vào

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3CFE2))
    ) {
        // Nút back
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 40.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                modifier = Modifier.size(32.dp)

                )
        }

        // Nội dung chính
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = "Word Genie",
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Word Genie",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nhập vào từ tiếng anh bạn muốn học",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchWord,
                onValueChange = { searchWord = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9D9D9), RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSearchComplete(searchWord) }, // Gọi callback khi nhấn nút
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD17878),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Tạo", fontWeight = FontWeight.Bold)
            }
        }

        // Navbar dưới cùng
        BottomNavBar(
            currentRoute = "word_genie",
            onNavItemSelected = onNavItemSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WordGenieScreenPreview() {
//    WordGenieScreen(
//        onBack = {},
//        onNavItemSelected = {}
//    )
//}