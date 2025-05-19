//
//
//package com.example.myapplication.ui.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.myapplication.R
//import com.example.myapplication.ui.components.BottomNavBar
//
//@Composable
//fun HistoryScreen(
//    onBack: () -> Unit = {},
//    historyList: List<HistoryItem> = listOf(
//        HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút"),
//        HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút"),
//        HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút")
//    ),
//    onBottomNavClicked: (String) -> Unit = {}  // Đã sửa kiểu dữ liệu từ Function<Unit> sang (String) -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8CFEA))
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(bottom = 80.dp), // chừa chỗ cho bottom bar
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Back icon
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp, start = 8.dp),
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
//            // Ảnh cừu
//            Image(
//                painter = painterResource(id = R.drawable.sheep_reading),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(140.dp)
//                    .padding(top = 0.dp, bottom = 8.dp)
//            )
//
//            // Tiêu đề
//            Text(
//                "History",
//                fontSize = 36.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier.padding(bottom = 24.dp)
//            )
//
//            // Danh sách lịch sử
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(historyList.size) { idx ->
//                    HistoryItemCard(historyList[idx])
//                }
//            }
//        }
//    }
//}
//
//data class HistoryItem(val word: String, val phonetic: String, val meaning: String)
//
//@Composable
//fun HistoryItemCard(item: HistoryItem,
//                    onNavItemSelected: (String) -> Unit = {}
//) {
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8CFEA))
//    ){
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(20.dp))
//                .padding(16.dp)
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
//        // Navbar ở cuối màn hình
//        BottomNavBar(
//            currentRoute = "visionary_words",
//            onNavItemSelected = onNavItemSelected,
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )
//
//    }
//
//}
//
//@Preview(showBackground = true, widthDp = 360, heightDp = 800)
//@Composable
//fun PreviewHistoryScreen() {
//    HistoryScreen(
//        historyList = listOf(
//            HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút"),
//            HistoryItem("Beautiful", "/ˈbjuː.tɪ.fəl/", "Đẹp"),
//            HistoryItem("Gorgeous", "/ˈɡɔː.dʒəs/", "Lộng lẫy")
//        )
//    )
//}
//
//
//
//
//
//
//
//
//










package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.components.BottomNavBar

@Composable
fun HistoryScreen(
    onBack: () -> Unit = {},
    historyList: List<HistoryItem> = listOf(
        HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút"),
        HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút"),
        HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút")
    ),
    onBottomNavClicked: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8CFEA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // chừa chỗ cho bottom bar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp),
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

            // Ảnh cừu
            Image(
                painter = painterResource(id = R.drawable.sheep_reading),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .padding(top = 0.dp, bottom = 8.dp)
            )

            // Tiêu đề
            Text(
                "History",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Danh sách lịch sử
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(historyList.size) { idx ->
                    HistoryItemCard(historyList[idx])
                }
            }
        }

        // Đặt BottomNavBar ở đây, bên trong Box nhưng bên ngoài Column
        BottomNavBar(
            currentRoute = "history",
            onNavItemSelected = onBottomNavClicked,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

data class HistoryItem(val word: String, val phonetic: String, val meaning: String)

@Composable
fun HistoryItemCard(item: HistoryItem) {
    // Đơn giản hóa thành chỉ một Column
    Column(
        modifier = Modifier
            .fillMaxWidth() // Chỉ chiếm toàn bộ chiều rộng
            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
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
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewHistoryScreen() {
    HistoryScreen(
        historyList = listOf(
            HistoryItem("Attractive", "/əˈtræktɪv/", "Thu hút"),
            HistoryItem("Beautiful", "/ˈbjuː.tɪ.fəl/", "Đẹp"),
            HistoryItem("Gorgeous", "/ˈɡɔː.dʒəs/", "Lộng lẫy")
        )
    )
}