package com.example.myapplication.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.api.ApiService
// Thay đổi các imports này
import com.example.myapplication.ui.screens.LoginScreen

import com.example.myapplication.ui.auth.RegisterScreen

import com.example.myapplication.ui.chat.ChatSmartAiWelcomeScreen
import com.example.myapplication.ui.chat.RecordingManager
import com.example.myapplication.ui.common.MainScaffold
import com.example.myapplication.ui.history.HistoryScreen
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.visionaryword.VisionaryWordsScreen
import com.example.myapplication.ui.wordgenie.WordDetailScreen
import com.example.myapplication.ui.wordgenie.WordGenieScreen
import com.example.myapplication.ui.auth.ProfileScreen
import com.example.myapplication.ui.chat.ChatSmartAiChatScreen
import com.example.myapplication.ui.flashcard.FlashcardDetailScreen
import com.example.myapplication.ui.flashcard.FlashcardScreen
import com.example.myapplication.ui.flashcard.FlashcardStudyScreen
import com.example.myapplication.ui.flashcard.MatchingStudyScreen
import com.example.myapplication.ui.flashcard.MultipleChoiceStudyScreen

object Routes {
    // Auth routes
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main features
    const val HOME = "home"
    const val PROFILE = "profile"  // Thêm route này
    // Feature routes
    const val WORD_GENIE = "word_genie"
    const val CHAT_SMART_AI = "chat_smart_ai"
    const val VISIONARY_WORDS = "visionary_words"
    const val HISTORY = "history"
    const val FLASHCARD = "flashcard" // THÊM DÒNG NÀY




    // Sub-routes
    const val VOCAB_INFO = "vocab_info"
    const val CHAT_SMART_AI_WELCOME = "chat_smart_ai_welcome"
    const val CHAT_SMART_AI_CHAT = "chat_smart_ai_chat"

    const val FLASHCARD_DETAIL = "flashcard_detail" // THÊM DÒNG NÀY
    const val FLASHCARD_STUDY = "flashcard_study"   // THÊM DÒNG NÀY
    const val MATCHING_STUDY = "matching_study"
    const val MULTIPLE_CHOICE_STUDY = "multiple_choice_study"


}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN,
    isLoggedIn: Boolean = false
) {
    val context = LocalContext.current
    val recordingManager = remember { RecordingManager(context) }

    // Theo dõi route hiện tại
    val currentBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val currentRoute = currentBackStackEntry?.destination?.route ?: startDestination

    MainScaffold(
        navController = navController,
        currentRoute = currentRoute
    ) {
        NavHost(

            navController = navController,
            startDestination = if (isLoggedIn) Routes.HOME else startDestination
        ) {
            // Auth flow
            composable(
                route = Routes.LOGIN,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
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

            composable(
                route = Routes.REGISTER,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.navigate(Routes.LOGIN) }
                )
            }

            composable(
                route = Routes.HOME,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                HomeScreen(
                    onWordGenieClick = { navController.navigate(Routes.WORD_GENIE) },
                    onChatSmartAIClick = { navController.navigate(Routes.CHAT_SMART_AI) },
                    onVisionaryWordsClick = { navController.navigate(Routes.VISIONARY_WORDS) },
                    onHistoryClick = { navController.navigate(Routes.HISTORY) } ,
                    onProfileClick = { navController.navigate(Routes.PROFILE) },  // Thêm dòng này
                    onFlashcardClick = { navController.navigate(Routes.FLASHCARD) }, // THÊM DÒNG NÀY
                )
            }


            // Thêm route cho Profile
            composable(
                route = Routes.PROFILE,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }



            composable(
                route = Routes.WORD_GENIE,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                WordGenieScreen(
                    onBack = { navController.popBackStack() },
                    onSearchComplete = { word ->
                        navController.navigate("${Routes.VOCAB_INFO}/$word")
                    }
                )
            }

            composable(
                route = "${Routes.VOCAB_INFO}/{word}",
                arguments = listOf(
                    navArgument("word") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val word = backStackEntry.arguments?.getString("word") ?: ""
                WordDetailScreen(
                    word = word,
                    onBack = { navController.popBackStack() },
                    onPlayAudio = { text -> recordingManager.playAudioFromText(text) },
                    onSave = { wordToSave ->
                        ApiService.addVocabulary(wordToSave, null) { code, _ ->
                            if (code == 200) {
                                Toast.makeText(context, "Đã lưu từ vựng", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Lưu từ thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            composable(
                route = Routes.CHAT_SMART_AI,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                ChatSmartAiWelcomeScreen(
                    onBack = { navController.popBackStack() },
                    onRecordStart = { recordingManager.startRecording() },
                    onRecordStop = { callback ->
                        recordingManager.stopRecording { transcription ->
                            transcription?.let { callback(it) }
                        }
                    },
                    onNavigate = { sentence ->
                        navController.navigate("${Routes.CHAT_SMART_AI_CHAT}/$sentence")
                    }
                )
            }

            composable(
                route = "${Routes.CHAT_SMART_AI_CHAT}/{sentence}",
                arguments = listOf(
                    navArgument("sentence") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val sentence = backStackEntry.arguments?.getString("sentence") ?: ""
                ChatSmartAiChatScreen(
                    sentence = sentence,
                    onRecordStart = { recordingManager.startRecording() },
                    onRecordStop = { callback ->
                        recordingManager.stopRecording { transcription ->
                            transcription?.let { callback(it) }
                        }
                    },
                    onBack = { navController.popBackStack() },
                    onPlayAudio = { text -> recordingManager.playAudioFromText(text) }
                )
            }

            composable(
                route = Routes.VISIONARY_WORDS,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                VisionaryWordsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenCamera = { /* Navigation to camera screen */ },
                    onPlayAudio = { text -> recordingManager.playAudioFromText(text) },
                    onSaveWord = { wordToSave ->
                        ApiService.addVocabulary(wordToSave, null) { code, _ ->
                            if (code == 200) {
                                Toast.makeText(context, "Đã lưu từ vựng", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Lưu từ thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            composable(
                route = Routes.HISTORY,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                HistoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Flashcard flow
            // Flashcard flow
            composable(
                route = Routes.FLASHCARD,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                FlashcardScreen(
                    onBack = { navController.popBackStack() },
                    onNavItemSelected = { route ->
                        // Xóa handleBottomNavigation, thay bằng direct navigation
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onCreateSet = {
                        // Logic tạo set có thể handle trong screen
                    },
                    onOpenSet = { setId ->
                        navController.navigate("${Routes.FLASHCARD_DETAIL}/$setId")
                    }
                )
            }

            composable(
                route = "${Routes.FLASHCARD_DETAIL}/{setId}",
                arguments = listOf(
                    navArgument("setId") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getString("setId") ?: ""
                FlashcardDetailScreen(
                    setId = setId,
                    onBack = { navController.popBackStack() },
                    onStartStudy = { studySetId ->
                        navController.navigate("${Routes.FLASHCARD_STUDY}/$studySetId")
                    },
                    onStartMatching = { matchingSetId ->
                        navController.navigate("${Routes.MATCHING_STUDY}/$matchingSetId")
                    },
                    onStartMultipleChoice = { mcSetId ->
                        navController.navigate("${Routes.MULTIPLE_CHOICE_STUDY}/$mcSetId")
                    }
                )
            }

            composable(
                route = "${Routes.FLASHCARD_STUDY}/{setId}",
                arguments = listOf(
                    navArgument("setId") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getString("setId") ?: ""
                FlashcardStudyScreen(
                    setId = setId,
                    onBack = { navController.popBackStack() },
                    onPlayAudio = { word -> recordingManager.playAudioFromText(word) }
                )
            }

            composable(
                route = "${Routes.MATCHING_STUDY}/{setId}",
                arguments = listOf(
                    navArgument("setId") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getString("setId") ?: ""
                MatchingStudyScreen(
                    setId = setId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "${Routes.MULTIPLE_CHOICE_STUDY}/{setId}",
                arguments = listOf(
                    navArgument("setId") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getString("setId") ?: ""
                MultipleChoiceStudyScreen(
                    setId = setId,
                    onBack = { navController.popBackStack() }
                )
            }


        }
    }
}