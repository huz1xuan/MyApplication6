package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.di.allModules
import com.example.myapplication.game.Game2048Screen
import com.example.myapplication.game.SnakeGameScreen
import com.example.myapplication.game.MemoryGameScreen
import com.example.myapplication.ui.MainMenuScreen
import com.example.myapplication.ui.SettingsScreen
import com.example.myapplication.ui.components.SlideVerification
import com.example.myapplication.util.SettingsManager
import org.koin.core.context.startKoin
import org.koin.core.context.KoinContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class AppPage {
    object MainMenu : AppPage()
    data class Game(val index: Int) : AppPage()
    object Settings : AppPage()
}

@Composable
fun App(
    onBackPressed: () -> Unit = {}
) {
    // 确保Koin初始化状态
    var koinInitialized by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        try {
            startKoin {
                modules(allModules)
            }
            koinInitialized = true
        } catch (e: Exception) {
            // 如果已经初始化，直接标记为完成
            koinInitialized = true
        }
    }
    
    MaterialTheme {
        if (koinInitialized) {
            AppContent(onBackPressed = onBackPressed)
        } else {
            // 显示加载界面
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "初始化中...",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AppContent(
    onBackPressed: () -> Unit = {}
) {
    var currentPage by remember { mutableStateOf<AppPage>(AppPage.MainMenu) }
    var pageHistory by remember { mutableStateOf(listOf<AppPage>()) }
    var showSlideVerification by remember { mutableStateOf(false) }
    var pendingGameIndex by remember { mutableStateOf(-1) }
    
    // 各游戏的暂停状态
    var gamePaused by remember { mutableStateOf(listOf(false, false, false, false)) }
    
    // 创建Koin组件来获取依赖
    val koinHelper = remember { object : KoinComponent {} }
    val settingsManager: SettingsManager by koinHelper.inject()

    fun navigateToPage(page: AppPage) {
        // 保存当前页面到历史记录
        if (currentPage != page) {
            pageHistory = pageHistory + currentPage
            currentPage = page
        }
    }
    
    fun goBack() {
        if (pageHistory.isNotEmpty()) {
            val previousPage = pageHistory.last()
            pageHistory = pageHistory.dropLast(1)
            
            // 如果从游戏返回，暂停当前游戏
            if (currentPage is AppPage.Game) {
                val gameIndex = (currentPage as AppPage.Game).index
                gamePaused = gamePaused.mapIndexed { i, v -> 
                    if (i == gameIndex) true else v 
                }
            }
            
            currentPage = previousPage
        } else {
            // 如果没有历史记录，且当前在主菜单，则显示退出确认
            if (currentPage is AppPage.MainMenu) {
                onBackPressed()
            } else {
                // 如果不在主菜单但没有历史记录，回到主菜单
                currentPage = AppPage.MainMenu
            }
        }
    }
    
    fun enterGame(gameIndex: Int) {
        pendingGameIndex = gameIndex
        showSlideVerification = true
    }
    
    fun enterSettings() {
        navigateToPage(AppPage.Settings)
    }
    
    fun backToMenu() {
        goBack()
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        when (currentPage) {
            is AppPage.MainMenu -> {
                MainMenuScreen(
                    onGameSelected = { gameIndex -> 
                        enterGame(gameIndex)
                    },
                    onSettingsClick = { enterSettings() }
                )
            }
            is AppPage.Game -> {
                val gameIndex = (currentPage as AppPage.Game).index
                val isPaused = gamePaused.getOrNull(gameIndex) ?: false
                
                when (gameIndex) {
                    0 -> Game2048Screen(paused = isPaused, onBack = { backToMenu() })
                    1 -> SnakeGameScreen(paused = isPaused, onBack = { backToMenu() })
                    2 -> MemoryGameScreen(paused = isPaused, onBack = { backToMenu() })
                    else -> {
                        Text(
                            text = "游戏未找到",
                            color = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            is AppPage.Settings -> {
                SettingsScreen(
                    onBack = { backToMenu() },
                    settingsManager = settingsManager
                )
            }
        }
        
        // 滑动验证对话框
        if (showSlideVerification && pendingGameIndex >= 0) {
            val gameNames = listOf("2048", "贪吃蛇", "记忆游戏")
            val gameName = gameNames.getOrNull(pendingGameIndex) ?: "游戏"
            
            SlideVerification(
                onVerificationComplete = {
                    showSlideVerification = false
                    gamePaused = gamePaused.mapIndexed { i, v -> 
                        if (i == pendingGameIndex) false else v 
                    }
                    navigateToPage(AppPage.Game(pendingGameIndex))
                    pendingGameIndex = -1
                },
                onDismiss = {
                    showSlideVerification = false
                    pendingGameIndex = -1
                },
                gameTitle = gameName
            )
        }
    }
}