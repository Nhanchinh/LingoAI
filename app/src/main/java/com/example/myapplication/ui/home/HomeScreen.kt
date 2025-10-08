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
            HomeButton("Learning", onClick = onFlashcardClick)
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