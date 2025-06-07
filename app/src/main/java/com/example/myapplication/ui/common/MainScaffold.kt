package com.example.myapplication.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.Routes

@Composable
fun MainScaffold(
    navController: NavHostController,
    currentRoute: String,

    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,  // Thêm dòng này để tránh background color của Scaffold

        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                Column {

                    BottomNavBar(
                        currentRoute = currentRoute.split("/")[0],
                        onNavItemSelected = { route ->
                            handleBottomNavigation(navController, route)
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

private fun shouldShowBottomBar(currentRoute: String): Boolean {
    val baseRoute = currentRoute.split("/")[0]
    return when (baseRoute) {
        Routes.WORD_GENIE,
        Routes.CHAT_SMART_AI,
        Routes.VISIONARY_WORDS,
        Routes.FLASHCARD, // THÊM DÒNG NÀY
        Routes.HISTORY -> true
        else -> false
    }
}

private fun handleBottomNavigation(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}