//
//package com.example.myapplication.ui.history
//
//import android.widget.Toast
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.myapplication.R
//import com.example.myapplication.api.ApiService
//import com.example.myapplication.ui.common.BottomNavBar
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//
//
//@Composable
//fun HistoryScreen(
//    onBack: () -> Unit = {}
//) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    var historyList by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
//
//    LaunchedEffect(Unit) {
//        ApiService.getVocabularyList { code, body ->
//            if (code == 200 && body != null) {
//                try {
//                    val json = JSONObject(body)
//                    val dataArray = json.getJSONArray("data")
//                    val list = mutableListOf<HistoryItem>()
//                    for (i in 0 until dataArray.length()) {
//                        val item = dataArray.getJSONObject(i)
//                        list.add(
//                            HistoryItem(
//                                word = item.optString("word"),
//                                phonetic = "/" + item.optString("ipa") + "/",
//                                meaning = item.optString("meaning")
//                            )
//                        )
//                    }
//                    historyList = list
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } else {
//                coroutineScope.launch {
//                    Toast.makeText(context, "Không thể tải danh sách từ vựng", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8CFEA))
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),  // Bỏ padding bottom
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = Color.Black,
//                        modifier = Modifier.size(32.dp)
//                    )
//                }
//            }
//
//            Image(
//                painter = painterResource(id = R.drawable.sheep_reading),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(140.dp)
//                    .padding(top = 0.dp, bottom = 8.dp)
//            )
//
//            Text(
//                "History",
//                fontSize = 36.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier.padding(bottom = 24.dp)
//            )
//
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(historyList.size) { idx ->
//                    val item = historyList[idx]
//                    HistoryItemCard(
//                        item = item,
//                        onDelete = { wordToDelete ->
//                            ApiService.deleteVocabulary(wordToDelete) { code, _ ->
//                                if (code == 200) {
//                                    historyList = historyList.filter { it.word != wordToDelete }
//                                } else {
//                                    coroutineScope.launch {
//                                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//data class HistoryItem(val word: String, val phonetic: String, val meaning: String)
//@Composable
//fun HistoryItemCard(item: HistoryItem, onDelete: (String) -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(20.dp))
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Column(
//            modifier = Modifier.weight(1f) // chiếm phần lớn chiều rộng
//        ) {
//            Text(
//                "${item.word} ${item.phonetic}",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color.Black
//            )
//            Text(
//                item.meaning,
//                fontSize = 16.sp,
//                color = Color.Black
//            )
//        }
//
//        IconButton(
//            onClick = { onDelete(item.word) }
//        ) {
//            Icon(
//                imageVector = Icons.Default.Delete,
//                contentDescription = "Delete"
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true, widthDp = 360, heightDp = 800)
//@Composable
//fun PreviewHistoryScreen() {
//    HistoryScreen(
//    )
//}



package com.example.myapplication.ui.history
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.api.ApiService
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.compose.animation.core.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HistoryScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var historyList by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) } // Thêm state cho loading

    // Gọi API khi Composable được tạo
    LaunchedEffect(Unit) {
        ApiService.getVocabularyList { code, body ->
            isLoading = false // Tắt loading khi có response
            if (code == 200 && body != null) {
                try {
                    val json = JSONObject(body)
                    val dataArray = json.getJSONArray("data")
                    val list = mutableListOf<HistoryItem>()
                    for (i in 0 until dataArray.length()) {
                        val item = dataArray.getJSONObject(i)
                        list.add(
                            HistoryItem(
                                word = item.optString("word"),
                                phonetic = "/" + item.optString("ipa") + "/",
                                meaning = item.optString("meaning")
                            )
                        )
                    }
                    historyList = list
                } catch (e: Exception) {
                    e.printStackTrace()
                    coroutineScope.launch {
                        Toast.makeText(context, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                coroutineScope.launch {
                    Toast.makeText(context, "Không thể tải danh sách từ vựng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8CFEA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.sheep_reading),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .padding(top = 0.dp, bottom = 8.dp)
            )

            Text(
                "History",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            } else if (historyList.isEmpty()) {
                // Hiển thị message khi không có từ vựng
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Chưa có từ vựng nào được lưu",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Hiển thị danh sách từ vựng
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(historyList.size) { idx ->
                        val item = historyList[idx]
                        HistoryItemCard(
                            item = item,
                            onDelete = { wordToDelete ->
                                ApiService.deleteVocabulary(wordToDelete) { code, _ ->
                                    if (code == 200) {
                                        historyList = historyList.filter { it.word != wordToDelete }
                                    } else {
                                        coroutineScope.launch {
                                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation(
    circleColor: Color = Color(0xFFD17878),
    animationDelay: Int = 300
) {
    // Tạo animation cho 3 dots
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(timeMillis = (animationDelay * index).toLong())
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1200 with LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Row(
        modifier = Modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hiển thị text "Đang tải" cùng với 3 dots
        Text(
            text = "Đang tải",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        circles.forEachIndexed { index, animatable ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = circleColor.copy(alpha = animatable.value),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}


data class HistoryItem(val word: String, val phonetic: String, val meaning: String)
@Composable
fun HistoryItemCard(item: HistoryItem, onDelete: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f) // chiếm phần lớn chiều rộng
        ) {
            Text(
                "${item.word} ${item.phonetic}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                item.meaning,
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        IconButton(
            onClick = { onDelete(item.word) }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete"
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewHistoryScreen() {
    HistoryScreen(
    )
}