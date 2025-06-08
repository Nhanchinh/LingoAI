package com.example.myapplication.ui.common

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import com.example.myapplication.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

// Data class cho navigation item với Material Icons
data class NavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavBar(currentRoute: String, onNavItemSelected: (String) -> Unit, modifier: Modifier) {
    val navItems = listOf(
        NavItem("Word", "word_genie", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
        NavItem("Chat", "chat_smart_ai", Icons.Filled.Chat, Icons.Outlined.Chat),
        NavItem("Vision", "visionary_words", Icons.Filled.Visibility, Icons.Outlined.Visibility),
        NavItem("History", "history", Icons.Filled.Schedule, Icons.Outlined.Schedule),
        NavItem("Cards", "flashcard", Icons.Filled.Style, Icons.Outlined.Style)
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Viền mỏng phía trên
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color.Gray.copy(alpha = 0.3f))
        )
        
        // Navbar content - GIẢM PADDING
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp, horizontal = 4.dp), // Giảm horizontal padding
            horizontalArrangement = Arrangement.spacedBy(0.dp), // Không có space giữa các item
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                NavBarItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onNavItemSelected(item.route) },
                    modifier = Modifier.weight(1f) // CHIA ĐỀU KHÔNG GIAN
                )
            }
        }
    }
}

@Composable
fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Thêm modifier parameter
) {
    // Animation cho scale effect
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Animation cho background
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(300),
        label = "background"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier // Sử dụng modifier được truyền vào
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(
                            ButtonPrimary.copy(alpha = backgroundAlpha * 0.2f),
                            ButtonPrimary.copy(alpha = backgroundAlpha * 0.1f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                }
            )
            .clickable { onClick() }
            .padding(vertical = 6.dp) // Chỉ padding vertical, horizontal sẽ tự động
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = if (isSelected) ButtonPrimary else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ButtonPrimary else TextSecondary,
            maxLines = 1
        )
    }
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