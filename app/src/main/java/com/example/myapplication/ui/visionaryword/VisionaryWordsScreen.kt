//package com.example.myapplication.ui.components
package com.example.myapplication.ui.visionaryword

// app/src/main/java/com/example/myapplication/ui/screens/VisionaryWordsScreen.kt


import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R


import androidx.compose.ui.Alignment


import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.components.BottomNavBar
import com.example.myapplication.ui.components.VisionaryCameraScreen
import com.example.myapplication.ui.components.VisionaryResultScreen


enum class VisionaryStep { Welcome, Camera, Result }





@Composable
fun VisionaryWordsScreen(
    onNavItemSelected: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onOpenCamera: () -> Unit
) {
    var step by remember { mutableStateOf(VisionaryStep.Welcome) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    when (step) {
        VisionaryStep.Welcome -> VisionaryWelcomeScreen(
            onOpenCamera = { step = VisionaryStep.Camera },
            onNavItemSelected = onNavItemSelected,
            onBack = onBack
        )
        VisionaryStep.Camera -> VisionaryCameraScreen(
            onPhotoTaken = { bitmap ->
                capturedImage = bitmap
                step = VisionaryStep.Result
            }
        )
        VisionaryStep.Result -> VisionaryResultScreen(
            image = capturedImage,
            onRetake = { step = VisionaryStep.Camera },
            onNavItemSelected = onNavItemSelected
        )
    }
}
@Composable
fun VisionaryWelcomeScreen(
    onOpenCamera: () -> Unit,
    onNavItemSelected: (String) -> Unit = {},
    onBack: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8CFEA))
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp,top = 40.dp )

        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
            }
        }


        // Nội dung chính ở trong Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // Thêm padding để tránh bị navbar che
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Image(
                painter = painterResource(id = R.drawable.sheep_camera),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Visionary Words",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onOpenCamera,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE48ED4)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Open camera")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(painterResource(id = R.drawable.ic_camera), contentDescription = null)
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



@Preview(showBackground = true)
@Composable
fun PreviewVisionaryWelcomeScreen() {
    VisionaryWelcomeScreen(onOpenCamera = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewVisionaryCameraScreen() {
    VisionaryCameraScreen(onPhotoTaken = {})
}


@Preview(showBackground = true)
@Composable
fun PreviewVisionaryResultScreen() {
    VisionaryResultScreen(image = null, onRetake = {})
}