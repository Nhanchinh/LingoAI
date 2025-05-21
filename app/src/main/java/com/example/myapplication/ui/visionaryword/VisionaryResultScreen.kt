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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun VisionaryResultScreen(
    image: Bitmap?,
    onRetake: () -> Unit,
    onNavItemSelected: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8CFEA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Photo Result",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Hiển thị ảnh đã chụp
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(4/3f)
                    .background(Color.LightGray, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (image != null) {
                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = "Captured photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("No image captured", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kết quả phân tích (mô phỏng)
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detected Word", fontWeight = FontWeight.Bold)
                    Text("Book (Sách)", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
                    Text("Pronunciation: /bʊk/")
                    Text("A written or printed work consisting of pages.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút chụp lại
            Button(
                onClick = onRetake,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE48ED4)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take another photo", fontSize = 18.sp)
            }
        }

        // Navbar ở cuối màn hình
        BottomNavBar(
            currentRoute = "visionary_words",
            onNavItemSelected = onNavItemSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}