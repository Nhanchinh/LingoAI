package com.example.myapplication.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.api.ApiService
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.chat.ChatSmartAiWelcomeScreen
import com.example.myapplication.ui.chat.RecordingManager
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.history.HistoryScreen
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.visionaryword.VisionaryWordsScreen
import com.example.myapplication.ui.wordgenie.WordDetailScreen
import com.example.myapplication.ui.wordgenie.WordGenieScreen

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
    val context = LocalContext.current
    val recordingManager = remember { RecordingManager(context) }

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
                onBackToLogin = { navController.navigate(Routes.LOGIN) }
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
                word = word,
                onBack = {
                    navController.popBackStack()
                },
                onPlayAudio =  { word -> recordingManager.playAudioFromText(word) },
                onSave = { wordToSave ->
                    ApiService.addVocabulary(wordToSave, null) { code, _ ->
                        if (code == 200) {
                            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                        } else {
                            // Xử lý lỗi, ví dụ:
                            Toast.makeText(context, "Lưu từ thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // Chat Smart AI flow - chính là route "chat_smart_ai"
        composable(Routes.CHAT_SMART_AI) {
            ChatSmartAiWelcomeScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRecordStart = {
                    recordingManager.startRecording()
                },
                onRecordStop = { onResult ->
                    recordingManager.stopRecording { transcription ->
                        transcription?.let { onResult(it) }
                    }
                },
                onNavigate = {
                        sentence ->
                    navController.navigate("${Routes.CHAT_SMART_AI_CHAT}/$sentence")
                },
                onNavItemSelected = { route -> handleBottomNavigation(navController, route) }
            )
        }

        // Sub-routes của Chat Smart AI
        composable(route = "${Routes.CHAT_SMART_AI_CHAT}/{sentence}"
        ) { backStackEntry ->

            val sentence = backStackEntry.arguments?.getString("sentence") ?: ""
            ChatSmartAiChatScreen(
                sentence = sentence,
                onRecordStart = {
                    recordingManager.startRecording()
                },
                onRecordStop = { onResult ->
                    recordingManager.stopRecording { transcription ->
                        transcription?.let { onResult(it) }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                onPlayAudio =  { word -> recordingManager.playAudioFromText(word) }
            )
        }

        // Visionary Words flow
        composable(Routes.VISIONARY_WORDS) {
            VisionaryWordsScreen(
                onBack = {navController.popBackStack()},
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

