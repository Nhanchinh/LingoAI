package com.example.myapplication.ui.components

import androidx.compose.foundation.clickable




import androidx.compose.foundation.layout.*


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.ui.draw.clip


@Composable
fun BottomNavBar(currentRoute: String, onNavItemSelected: (String) -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavBarItem("W", "word_genie", currentRoute, onNavItemSelected)
        NavBarItem("C", "chat_smart_ai", currentRoute, onNavItemSelected)
        NavBarItem("V", "visionary_words", currentRoute, onNavItemSelected)
        NavBarItem("H", "history", currentRoute, onNavItemSelected)
    }
}

@Composable
fun NavBarItem(label: String, route: String, currentRoute: String, onClick: (String) -> Unit) {
    val isSelected = currentRoute == route
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFD17878) else Color.Transparent)
            .padding(12.dp)
            .clickable { onClick(route) },
        color = if (isSelected) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
    )
}

//
//@Preview(showBackground = true)
//@Composable
//fun BottomNavBarPreview() {
//    BottomNavBar(
//        currentRoute = "chat_smart_ai_welcome",
//        onNavItemSelected = {},
//        modifier = Modifier
//    )
//}