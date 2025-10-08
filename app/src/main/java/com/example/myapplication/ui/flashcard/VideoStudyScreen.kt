package com.example.myapplication.ui.flashcard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun VideoStudyScreen(
    onBack: () -> Unit,
    onVideoClick: (String, String, String, String?, String, String) -> Unit = { _, _, _, _, _, _ -> }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER
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
            }

            // LOGO VÀ TITLE
            Image(
                painter = painterResource(id = R.drawable.sheep_logo),
                contentDescription = "Video Study",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 0.dp, bottom = 8.dp)
            )
            
            Text(
                text = "Video Study",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Học từ vựng qua video với phát âm chuẩn",
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // VIDEO CONTENT
            VideoStudyContent(onVideoClick = onVideoClick)
        }
    }
}
