package com.example.myapplication.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun VisionaryCameraScreen(onPhotoTaken: (Bitmap) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8CFEA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFD6EAF8))
        ) {
            // TODO: Hiển thị preview camera ở đây
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                // TODO: Chụp ảnh thật, ở đây giả lập bằng ảnh mẫu
                // onPhotoTaken(bitmap)
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E86C1)),
            modifier = Modifier.size(64.dp)
        ) {}
    }
}