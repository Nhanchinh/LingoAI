package com.example.myapplication.ui.components




import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R


@Composable
fun VisionaryResultScreen(image: Bitmap?, onRetake: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8CFEA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Visionary Words",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        image?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            // TODO: Vẽ bounding box và label lên ảnh
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetake,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE48ED4)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Retake photo")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(painterResource(id = R.drawable.ic_camera), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Chọn 1 vật trong hình nếu bạn muốn học nhiều hơn",
            fontSize = 16.sp
        )
    }
}