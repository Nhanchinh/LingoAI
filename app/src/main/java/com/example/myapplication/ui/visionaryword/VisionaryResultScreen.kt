package com.example.myapplication.ui.visionaryword

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import com.example.myapplication.ui.common.BottomNavBar
import org.json.JSONObject
import java.io.File

// Thêm imports cho scroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.myapplication.ui.theme.ButtonPrimary
import com.example.myapplication.ui.theme.MainColor

fun Context.bitmapToFile(bitmap: Bitmap): File {
    val file = File(cacheDir, "temp_image.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file
}

@Composable
fun VisionaryResultScreen(
    context: Context,
    image: Bitmap?,
    onRetake: () -> Unit,
    onNavItemSelected: (String) -> Unit = {},
    onPlayAudio: (String) -> Unit = {},
    onSaveWord: (String) -> Unit = {}
) {
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var objects by remember { mutableStateOf<List<DetectedObject>>(emptyList()) }

    // Khi image thay đổi, gọi API
    LaunchedEffect(image) {
        if (image != null) {
            loading = true
            error = null

            // Chuyển Bitmap thành File
            val imageFile = context.bitmapToFile(image) // chuyển Bitmap thành File

            ApiService.detectObject(imageFile) { responseJson ->
                loading = false
                if (responseJson == null) {
                    error = "Lỗi kết nối API"
                    return@detectObject
                }

                try {
                    val json = JSONObject(responseJson)
                    imageUrl = json.optString("image_url")
                    val jsonObjects = json.getJSONArray("objects")
                    val tempList = mutableListOf<DetectedObject>()
                    for (i in 0 until jsonObjects.length()) {
                        val item = jsonObjects.getJSONObject(i)
                        tempList.add(
                            DetectedObject(
                                ipa = item.optString("ipa"),
                                meaning = item.optString("meaning"),
                                word = item.optString("word")
                            )
                        )
                    }
                    objects = tempList
                    error = null
                } catch (e: Exception) {
                    error = "Lỗi xử lý dữ liệu"
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
    ) {
        // Thêm verticalScroll để có thể cuộn
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Photo Result",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(4 / 3f)
                    .background(Color.LightGray, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator()
                } else if (error != null) {
                    Text(error ?: "", color = Color.Red)
                } else {
                    if (imageUrl != null) {
                        // Load ảnh từ url bằng Coil
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Detected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text("No image available", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            objects.forEach { obj ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detected Word", fontWeight = FontWeight.Bold)
                        Text(obj.word, fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
                        Text("Pronunciation: /${obj.ipa}/")
                        Text(obj.meaning)

                        // Thêm các nút play và save
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onPlayAudio(obj.word) }
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Play audio",
                                    tint = ButtonPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onSaveWord(obj.word) }
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Save word",
                                    tint = ButtonPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetake,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary),
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

//        BottomNavBar(
//            currentRoute = "visionary_words",
//            onNavItemSelected = onNavItemSelected,
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )
    }
}

data class DetectedObject(
    val ipa: String,
    val meaning: String,
    val word: String
)