package com.example.myapplication.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.game.common.Card
import com.example.myapplication.game.common.MemoryGameState
import com.example.myapplication.ui.common.DreamyCardBackground
import com.example.myapplication.ui.common.PauseOverlay
import com.example.myapplication.ui.common.GameHeader
import com.example.myapplication.ui.common.GameControlButtons
import com.example.myapplication.util.ScoreManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.example.myapplication.ui.common.HighScoreButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val GRID_SIZE = 4
private const val ANIMATION_DURATION = 300
private const val MATCH_DELAY = 500L
private val THEME_COLOR = Color(0xFF7B1FA2) // 紫色主题

@Composable
fun MemoryGameScreen(paused: Boolean = false, onBack: (() -> Unit)? = null) {
    val koinHelper = remember { object : KoinComponent {} }
    val scoreManager: ScoreManager by koinHelper.inject()
    

    
    var gameState by remember { mutableStateOf(createInitialState(scoreManager)) }
    var isPaused by remember { mutableStateOf(paused) }
    val scope = rememberCoroutineScope()

    // 更新最高分
    LaunchedEffect(gameState.score) {
        if (gameState.score > gameState.highScore) {
            scoreManager.updateHighScore(ScoreManager.GAME_MEMORY, gameState.score)
            gameState = gameState.copy(highScore = gameState.score)
        }
    }

    DreamyCardBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            // 分数显示和最高分按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "分数: ${gameState.score}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "步数: ${gameState.moves}",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                HighScoreButton(
                    score = gameState.highScore,
                    themeColor = Color(0xFF4CAF50),
                    showAbove = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 游戏板
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // 响应式计算卡片大小
                val availableSize = minOf(maxWidth, maxHeight)
                val cardSpacing = 8.dp
                val totalSpacing = cardSpacing * (GRID_SIZE - 1)
                val cardSize = (availableSize - totalSpacing) / GRID_SIZE
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(cardSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(GRID_SIZE) { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(cardSpacing),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(GRID_SIZE) { col ->
                                val index = row * GRID_SIZE + col
                                if (index < gameState.cards.size) {
                                    MemoryCard(
                                        card = gameState.cards[index],
                                        size = cardSize,
                                        onClick = {
                                            if (!isPaused && !gameState.isGameOver &&
                                                !gameState.cards[index].isMatched &&
                                                !gameState.cards[index].isFlipped &&
                                                gameState.flippedCards.size < 2) {
                                                scope.launch {
                                                    val newCards = gameState.cards.toMutableList()
                                                    newCards[index] = newCards[index].copy(isFlipped = true)
                                                    
                                                    val newFlipped = gameState.flippedCards + newCards[index]
                                                    gameState = gameState.copy(
                                                        cards = newCards,
                                                        flippedCards = newFlipped,
                                                        moves = gameState.moves + 1
                                                    )

                                                    if (newFlipped.size == 2) {
                                                        delay(MATCH_DELAY)
                                                        if (newFlipped[0].value == newFlipped[1].value) {
                                                            // 匹配成功
                                                            val updatedCards = newCards.map { card ->
                                                                if (card.isFlipped) {
                                                                    card.copy(isMatched = true)
                                                                } else card
                                                            }
                                                            gameState = gameState.copy(
                                                                cards = updatedCards,
                                                                flippedCards = emptyList(),
                                                                score = gameState.score + 10
                                                            )
                                                            
                                                            // 检查游戏是否结束
                                                            if (updatedCards.all { it.isMatched }) {
                                                                gameState = gameState.copy(isGameOver = true)
                                                            }
                                                        } else {
                                                            // 匹配失败
                                                            val updatedCards = newCards.map { card ->
                                                                if (card.isFlipped && !card.isMatched) {
                                                                    card.copy(isFlipped = false)
                                                                } else card
                                                            }
                                                            gameState = gameState.copy(
                                                                cards = updatedCards,
                                                                flippedCards = emptyList()
                                                            )
                                                        }
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

            // 控制按钮
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { isPaused = !isPaused },
                    enabled = !gameState.isGameOver,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(if (isPaused) "继续" else "暂停")
                }
                Button(
                    onClick = { 
                        gameState = createInitialState(scoreManager)
                        isPaused = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("重开")
                }
                Button(
                    onClick = { onBack?.invoke() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("返回")
                }
            }
        }

        // 游戏结束遮罩
        if (gameState.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "游戏完成！",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "最终得分: ${gameState.score}",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    Text(
                        "总步数: ${gameState.moves}",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Button(
                        onClick = {
                            gameState = createInitialState(scoreManager)
                            isPaused = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("重新开始")
                    }
                }
            }
        }

        // 暂停遮罩
        if (isPaused && !gameState.isGameOver) {
            PauseOverlay(
                onResume = { isPaused = false },
                onRestart = {
                    gameState = createInitialState(scoreManager)
                    isPaused = false
                },
                onBack = onBack,
                themeColor = Color(0xFF4CAF50)
            )
            }
        }
    }
}

@Composable
fun MemoryCard(
    card: Card,
    onClick: () -> Unit,
    size: Dp = 80.dp
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped) 180f else 0f,
        animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(
                rotationY = rotation,
                cameraDistance = 12f * size.value
            )
            .clickable(
                enabled = !card.isMatched && !card.isFlipped,
                onClick = onClick
            )
            .background(
                color = when {
                    card.isMatched -> Color(0xFF4CAF50)
                    rotation > 90f -> Color(0xFFE1BEE7)
                    else -> Color(0xFF7B1FA2)
                },
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (rotation > 90f) {
            Text(
                text = card.value.toString(),
                color = Color(0xFF7B1FA2),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                }
            )
        }
    }
}

private fun createInitialState(scoreManager: ScoreManager): MemoryGameState {
    val values = (List(8) { it } + List(8) { it }).shuffled()
    val cards = values.mapIndexed { index, value ->
        Card(id = index, value = value)
    }
    return MemoryGameState(
        cards = cards,
        highScore = scoreManager.getHighScore(ScoreManager.GAME_MEMORY)
    )
} 