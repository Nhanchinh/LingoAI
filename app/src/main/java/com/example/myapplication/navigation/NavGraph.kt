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
import com.example.myapplication.models.Character
import com.example.myapplication.ui.flashcard.FlashcardDetailScreen
import com.example.myapplication.ui.flashcard.FlashcardScreen
import com.example.myapplication.ui.flashcard.FlashcardStudyScreen
import com.example.myapplication.ui.flashcard.MatchingStudyScreen
import com.example.myapplication.ui.flashcard.MultipleChoiceStudyScreen
import com.example.myapplication.ui.flashcard.VideoStudyScreen
import com.example.myapplication.ui.flashcard.VideoPlayerScreen
import com.example.myapplication.ui.notification.NotificationSettingsScreen
import com.example.myapplication.ui.ai.AISettingsScreen

object Routes {
    // Auth routes
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main features
    const val HOME = "home"
    const val PROFILE = "profile"  // Thêm route này
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val AI_SETTINGS = "ai_settings"
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
    const val CHAT_SMART_AI_CHAT_WITH_CONVERSATION = "chat_smart_ai_chat_with_conversation"

    const val FLASHCARD_DETAIL = "flashcard_detail" // THÊM DÒNG NÀY
    const val FLASHCARD_STUDY = "flashcard_study"   // THÊM DÒNG NÀY
    const val VIDEO_STUDY = "video_study"
    const val VIDEO_PLAYER = "video_player"
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
                    },
                    onNotificationSettings = {
                        navController.navigate(Routes.NOTIFICATION_SETTINGS)
                    },
                    onAISettings = {
                        navController.navigate(Routes.AI_SETTINGS)
                    }
                )
            }

            // Notification Settings Screen
            composable(
                route = Routes.NOTIFICATION_SETTINGS,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                NotificationSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // AI Settings Screen
            composable(
                route = Routes.AI_SETTINGS,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                AISettingsScreen(
                    onBack = { navController.popBackStack() }
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
                    onNavigate = { sentence, character ->
                        val characterJson = "{\"name\":\"${character.name}\",\"personality\":\"${character.personality}\",\"description\":\"${character.description}\",\"voice\":\"${character.voiceId}\"}"
                        val encodedCharacterJson = java.net.URLEncoder.encode(characterJson, "UTF-8")
                        navController.navigate("${Routes.CHAT_SMART_AI_CHAT}/$sentence?characterData=$encodedCharacterJson")
                    },
                    onNavigateWithConversation = { character, conversationId ->
                        val characterJson = "{\"name\":\"${character.name}\",\"personality\":\"${character.personality}\",\"description\":\"${character.description}\",\"voice\":\"${character.voiceId}\"}"
                        val encodedCharacterJson = java.net.URLEncoder.encode(characterJson, "UTF-8")
                        navController.navigate("${Routes.CHAT_SMART_AI_CHAT_WITH_CONVERSATION}/$conversationId?characterData=$encodedCharacterJson")
                    }
                )
            }

            composable(
                route = "${Routes.CHAT_SMART_AI_CHAT}/{sentence}?characterData={characterData}",
                arguments = listOf(
                    navArgument("sentence") { type = NavType.StringType },
                    navArgument("characterData") { 
                        type = NavType.StringType
                        defaultValue = ""
                    }
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
                val characterData = backStackEntry.arguments?.getString("characterData") ?: ""
                
                // Parse character from JSON data
                val selectedCharacter = if (characterData.isNotEmpty()) {
                    try {
                        val decodedJson = java.net.URLDecoder.decode(characterData, "UTF-8")
                        Log.e("NavGraph", "decodedJson: ${decodedJson}")
                        val jsonObject = org.json.JSONObject(decodedJson)
                        val character = Character(
                            name = jsonObject.getString("name"),
                            personality = jsonObject.getString("personality"),
                            description = jsonObject.getString("description"),
                            voiceId = jsonObject.getString("voice")
                        )
                        character
                    } catch (e: Exception) {
                        Log.e("NavGraph", "Error parsing character data: ${e.message}")
                        Log.e("NavGraph", "characterData was: $characterData")
                        Character.DEFAULT_CHARACTERS.firstOrNull()
                    }
                } else Character.DEFAULT_CHARACTERS.firstOrNull()
                
                ChatSmartAiChatScreen(
                    sentence = sentence,
                    selectedCharacter = selectedCharacter,
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

            // New route for chat with existing conversation
            composable(
                route = "${Routes.CHAT_SMART_AI_CHAT_WITH_CONVERSATION}/{conversationId}?characterData={characterData}",
                arguments = listOf(
                    navArgument("conversationId") { type = NavType.StringType },
                    navArgument("characterData") { 
                        type = NavType.StringType
                        defaultValue = ""
                    }
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
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                val characterData = backStackEntry.arguments?.getString("characterData") ?: ""
                
                // Parse character from JSON data
                val selectedCharacter = if (characterData.isNotEmpty()) {
                    try {
                        val json = org.json.JSONObject(characterData)
                        val character = Character(
                            id = "", // Will be loaded from conversation
                            name = json.optString("name", "Lingoo"),
                            personality = json.optString("personality", ""),
                            description = json.optString("description", ""),
                            voiceId = json.optString("voice", "alloy"),
                            isActive = true
                        )
                        character
                    } catch (e: Exception) {
                        Log.e("NavGraph", "Error parsing character data: ${e.message}")
                        Character.DEFAULT_CHARACTERS.firstOrNull()
                    }
                } else Character.DEFAULT_CHARACTERS.firstOrNull()
                
                ChatSmartAiChatScreen(
                    sentence = "", // Empty because we're loading from conversation
                    selectedCharacter = selectedCharacter,
                    existingConversationId = conversationId,
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
                    },
                    onNavigateToVideoStudy = {
                        navController.navigate(Routes.VIDEO_STUDY)
                    }
                )
            }

            // Video Study Screen
            composable(
                route = Routes.VIDEO_STUDY,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                VideoStudyScreen(
                    onBack = { navController.popBackStack() },
                    onVideoClick = { videoId, title, description, subtitleFileName ->
                        val subtitleParam = subtitleFileName ?: "none"
                        navController.navigate("${Routes.VIDEO_PLAYER}/$videoId/$title/$description/$subtitleParam")
                    }
                )
            }

            // Video Player Screen
            composable(
                route = "${Routes.VIDEO_PLAYER}/{videoId}/{title}/{description}/{subtitleFile}",
                arguments = listOf(
                    navArgument("videoId") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType },
                    navArgument("description") { type = NavType.StringType },
                    navArgument("subtitleFile") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                val title = backStackEntry.arguments?.getString("title") ?: "Video"
                val description = backStackEntry.arguments?.getString("description") ?: ""
                val subtitleFile = backStackEntry.arguments?.getString("subtitleFile")
                val subtitleFileName = if (subtitleFile == "none") null else subtitleFile
                
                VideoPlayerScreen(
                    videoId = videoId,
                    title = title,
                    description = description,
                    subtitleFileName = subtitleFileName,
                    onBack = { navController.popBackStack() }
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