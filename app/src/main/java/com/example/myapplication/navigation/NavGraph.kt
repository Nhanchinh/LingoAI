//package com.example.myapplication.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.myapplication.ui.screens.*
//import com.example.myapplication.ui.components.*
//
//object Routes {
//    // Auth routes
//    const val SPLASH = "splash"
//    const val LOGIN = "login"
//    const val REGISTER = "register"
//
//    // Main features
//    const val HOME = "home"
//
//    // Word Genie feature
//    const val WORD_GENIE = "word_genie"
//    const val VOCAB_INFO = "vocab_info"
//
//    // Chat Smart AI feature
//    const val CHAT_SMART_AI_WELCOME = "chat_smart_ai_welcome"
//    const val CHAT_SMART_AI_CHAT = "chat_smart_ai_chat"
//
//    // Visionary Words feature
//    const val VISIONARY_WELCOME = "visionary_welcome"
//
//    // History feature
//    const val HISTORY = "history"
//}
//
//@Composable
//fun AppNavGraph(
//    navController: NavHostController = rememberNavController(),
//    startDestination: String = Routes.LOGIN
//) {
//    NavHost(navController = navController, startDestination = startDestination) {
//        // Auth flow
//        composable(Routes.LOGIN) {
//            LoginScreen(
//                onLoginSuccess = {
//                    navController.navigate(Routes.HOME) {
//                        popUpTo(Routes.LOGIN) { inclusive = true }
//                    }
//                },
//                onNavigateToRegister = {
//                    navController.navigate(Routes.REGISTER)
//                }
//            )
//        }
//
//        composable(Routes.REGISTER) {
//            RegisterScreen(
//                onRegisterSuccess = {
//                    navController.navigate(Routes.HOME) {
//                        popUpTo(Routes.REGISTER) { inclusive = true }
//                    }
//                },
//                onBackToLogin = { false } // Trả về false để không thực hiện điều hướng
//            )
//        }
//
//        // Main screen
//        composable(Routes.HOME) {
//            HomeScreen(
//                onWordGenieClick = { navController.navigate(Routes.WORD_GENIE) },
//                onChatSmartAIClick = { navController.navigate(Routes.CHAT_SMART_AI_WELCOME) },
//                onVisionaryWordsClick = { navController.navigate(Routes.VISIONARY_WELCOME) },
//                onHistoryClick = { navController.navigate(Routes.HISTORY) }
//            )
//        }
//
//        // Word Genie flow
//        composable(Routes.WORD_GENIE) {
//            WordGenieScreen(
//                onBack = {
//                    navController.popBackStack()
//                },
//                onNavItemSelected = { route -> handleBottomNavigation(navController, route) }
//            )
//        }
//
//        composable(
//            route = "${Routes.VOCAB_INFO}/{word}"
//        ) { backStackEntry ->
//            val word = backStackEntry.arguments?.getString("word") ?: ""
//            WordDetailScreen(
//                wordDetail = WordDetail(
//                    word = word,
//                    phonetic = "/word/", // Giả sử
//                    meaning = "Meaning", // Giả sử
//                    synonyms = listOf(),
//                    antonyms = listOf(),
//                    phrases = listOf(),
//                    onBackPressed = { false }
//                ),
//                onBack = {
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        // Chat Smart AI flow
//        composable(Routes.CHAT_SMART_AI_WELCOME) {
//            ChatSmartAiWelcomeScreen(
//                onBack = {
//                    navController.popBackStack()
//                },
//                onRecord = {
//                    navController.navigate(Routes.CHAT_SMART_AI_CHAT)
//                }
//            )
//        }
//
//        composable(Routes.CHAT_SMART_AI_CHAT) {
//            ChatSmartAiChatScreen(
//                messages = listOf(
//                    ChatMessage("Lingoo", "Hello, how can I help you?", false),
//                    ChatMessage("User", "I want to learn English", true)
//                ), // Sample messages
//                onBack = {
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        // Visionary Words flow
//        composable(Routes.VISIONARY_WELCOME) {
//            VisionaryWelcomeScreen(
//                onOpenCamera = {
//                    // Navigation to camera screen would go here
//                }
//            )
//        }
//
//        // History flow
//        composable(Routes.HISTORY) {
//            HistoryScreen(
//                onBack = {
//                    navController.popBackStack()
//                },
//                onBottomNavClicked = {  }
//            )
//        }
//    }
//}
//
//// Hàm xử lý bottom navigation
//private fun handleBottomNavigation(navController: NavHostController, route: String) {
//    navController.navigate(route) {
//        popUpTo(Routes.HOME)
//        launchSingleTop = true
//    }
//}
//
//
//
//
//
//





package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.components.*

object Routes {
    // Auth routes
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main features
    const val HOME = "home"

    // Feature routes (sử dụng cho bottom navigation)
    const val WORD_GENIE = "word_genie"
    const val CHAT_SMART_AI = "chat_smart_ai" // Đã sửa route này
    const val VISIONARY_WORDS = "visionary_words" // Đã sửa route này
    const val HISTORY = "history"

    // Sub-routes
    const val VOCAB_INFO = "vocab_info"
    const val CHAT_SMART_AI_WELCOME = "chat_smart_ai_welcome"
    const val CHAT_SMART_AI_CHAT = "chat_smart_ai_chat"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    NavHost(navController = navController, startDestination = startDestination) {
        // Auth flow
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onBackToLogin = { false }
            )
        }

        // Main screen
        composable(Routes.HOME) {
            HomeScreen(
                onWordGenieClick = { navController.navigate(Routes.WORD_GENIE) },
                onChatSmartAIClick = { navController.navigate(Routes.CHAT_SMART_AI) },
                onVisionaryWordsClick = { navController.navigate(Routes.VISIONARY_WORDS) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) }
            )
        }

        // Word Genie flow
        composable(Routes.WORD_GENIE) {
            WordGenieScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavItemSelected = { route -> handleBottomNavigation(navController, route) },
                onSearchComplete = {
                        word ->
                    // Điều hướng đến màn hình vocab_info và truyền từ
                    navController.navigate("${Routes.VOCAB_INFO}/$word")

                }
            )
        }

        composable(
            route = "${Routes.VOCAB_INFO}/{word}"
        ) { backStackEntry ->
            val word = backStackEntry.arguments?.getString("word") ?: ""
            WordDetailScreen(
                wordDetail = WordDetail(
                    word = word,
                    phonetic = "/word/",
                    meaning = "Meaning",
                    synonyms = listOf(),
                    antonyms = listOf(),
                    phrases = listOf(),
                    onBackPressed = { false }
                ),
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Chat Smart AI flow - chính là route "chat_smart_ai"
        composable(Routes.CHAT_SMART_AI) {
            ChatSmartAiWelcomeScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRecord = {
                    navController.navigate(Routes.CHAT_SMART_AI_CHAT)
                } ,
                onNavItemSelected = { route -> handleBottomNavigation(navController, route) }
            )
        }

        // Sub-routes của Chat Smart AI
        composable(Routes.CHAT_SMART_AI_CHAT) {
            ChatSmartAiChatScreen(
                messages = listOf(
                    ChatMessage("Lingoo", "Hello, how can I help you?", false),
                    ChatMessage("User", "I want to learn English", true)
                ),
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Visionary Words flow
        composable(Routes.VISIONARY_WORDS) {
            VisionaryWelcomeScreen(
                onOpenCamera = {
                    // Navigation to camera screen
                },
                onNavItemSelected = { route -> handleBottomNavigation(navController, route) }
            )
        }

        // History flow
        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = {
                    navController.popBackStack()
                },
                onBottomNavClicked = { route -> handleBottomNavigation(navController, route) }
            )
        }
    }
}

// Hàm xử lý bottom navigation
private fun handleBottomNavigation(navController: NavHostController, route: String) {
    // Điều quan trọng là dùng launchSingleTop và popUpTo
    navController.navigate(route) {
        // Xóa tất cả các màn hình khác trong back stack trừ HOME
        popUpTo(Routes.HOME) { saveState = true }
        // Đảm bảo không tạo nhiều instance của cùng một màn hình
        launchSingleTop = true
        // Lưu trạng thái khi chuyển đổi giữa các tab
        restoreState = true
    }
}