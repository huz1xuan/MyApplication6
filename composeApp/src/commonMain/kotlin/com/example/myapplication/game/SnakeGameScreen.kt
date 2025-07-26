package com.example.myapplication.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.myapplication.game.common.Direction
import com.example.myapplication.game.common.Point
import com.example.myapplication.game.common.SnakeGameState
import kotlinx.coroutines.delay
import kotlin.math.abs
import com.example.myapplication.ui.common.NatureForestBackground
import com.example.myapplication.ui.common.PauseOverlay
import com.example.myapplication.ui.common.HighScoreButton
import com.example.myapplication.ui.common.GameHeader
import com.example.myapplication.ui.common.GameControlButtons
import com.example.myapplication.util.ScoreManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun SnakeGameScreen(paused: Boolean = false, onBack: (() -> Unit)? = null) {
    val koinHelper = remember { object : KoinComponent {} }
    val scoreManager: ScoreManager by koinHelper.inject()
    

    
    var gameState by remember { mutableStateOf(createInitialState(scoreManager)) }
    var isPaused by remember { mutableStateOf(paused) }
    val scope = rememberCoroutineScope()

    // 更新最高分
    LaunchedEffect(gameState.score) {
        if (gameState.score > gameState.highScore) {
            scoreManager.updateHighScore(ScoreManager.GAME_SNAKE, gameState.score)
            gameState = gameState.copy(highScore = gameState.score)
        }
    }

    NatureForestBackground {
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
                // 使用共享头部组件
                GameHeader(
                    score = gameState.score,
                    highScore = gameState.highScore,
                    themeColor = Color(0xFF00B4D8)
                )

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
                    var dragStartX by remember { mutableStateOf(0f) }
                    var dragStartY by remember { mutableStateOf(0f) }
                    val MIN_SWIPE_DISTANCE = 50f

                    // 响应式计算单元格大小
                    val availableSize = minOf(maxWidth, maxHeight) - 32.dp
                    val cellSize = (availableSize - (BOARD_SIZE - 1) * 2.dp) / BOARD_SIZE
                    val spacing = 2.dp

                    Box(
                        modifier = Modifier
                            .size(availableSize)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        dragStartX = offset.x
                                        dragStartY = offset.y
                                    },
                                    onDragEnd = {
                                        // 不做任何操作
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val dragX = change.position.x - dragStartX
                                        val dragY = change.position.y - dragStartY
                                        
                                        if (maxOf(abs(dragX), abs(dragY)) >= MIN_SWIPE_DISTANCE) {
                                            val direction = when {
                                                abs(dragX) > abs(dragY) -> {
                                                    if (dragX > 0) Direction.RIGHT else Direction.LEFT
                                                }
                                                else -> {
                                                    if (dragY > 0) Direction.DOWN else Direction.UP
                                                }
                                            }
                                            if (!isPaused && !gameState.isGameOver && direction != gameState.direction.opposite()) {
                                                gameState = gameState.copy(direction = direction)
                                            }
                                            // 重置拖动起始位置
                                            dragStartX = change.position.x
                                            dragStartY = change.position.y
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // 游戏网格
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            repeat(BOARD_SIZE) { row ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(spacing),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(BOARD_SIZE) { col ->
                                        val point = Point(col, row)
                                        val isSnake = gameState.snake.contains(point)
                                        val isFood = gameState.food == point
                                        val isHead = gameState.snake.firstOrNull() == point

                                        Box(
                                            modifier = Modifier
                                                .size(cellSize)
                                                .background(
                                                    when {
                                                        isHead -> Color(0xFF00B4D8)  // 蛇头
                                                        isSnake -> Color(0xFF90E0EF) // 蛇身
                                                        isFood -> Color(0xFFFF0054)  // 食物
                                                        else -> Color(0x33FFFFFF)    // 空格子
                                                    },
                                                    if (isFood) CircleShape else RoundedCornerShape(4.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 使用共享控制按钮组件
                GameControlButtons(
                    isPaused = isPaused,
                    isGameOver = gameState.isGameOver,
                    themeColor = Color(0xFF00B4D8),
                    onPauseResume = { isPaused = !isPaused },
                    onRestart = { 
                        gameState = createInitialState(scoreManager)
                        isPaused = false
                    },
                    onBack = { onBack?.invoke() }
                )
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
                            "游戏结束",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "最终得分: ${gameState.score}",
                            color = Color.White,
                            fontSize = 24.sp
                        )
                        Button(
                            onClick = {
                                gameState = createInitialState(scoreManager)
                                isPaused = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00B4D8)
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
                    themeColor = Color(0xFF00B4D8)
                )
            }
        }
    }

    // 游戏逻辑
    LaunchedEffect(isPaused, gameState.isGameOver) {
        if (!isPaused && !gameState.isGameOver) {
            while (true) {
                delay(gameState.moveDelay)
                gameState = gameState.move()
            }
        }
    }
}

private fun Direction.opposite(): Direction = when (this) {
    Direction.UP -> Direction.DOWN
    Direction.DOWN -> Direction.UP
    Direction.LEFT -> Direction.RIGHT
    Direction.RIGHT -> Direction.LEFT
}

private fun createInitialState(scoreManager: ScoreManager): SnakeGameState {
    return SnakeGameState(
        snake = listOf(Point(BOARD_SIZE / 2, BOARD_SIZE / 2)),
        direction = Direction.RIGHT,
        food = generateFood(listOf(Point(BOARD_SIZE / 2, BOARD_SIZE / 2))),
        highScore = scoreManager.getHighScore(ScoreManager.GAME_SNAKE)
    )
}

private fun generateFood(snake: List<Point>): Point {
    val allPositions = (0 until BOARD_SIZE).flatMap { row ->
        (0 until BOARD_SIZE).map { col -> Point(col, row) }
    }
    val availablePositions = allPositions.filter { it !in snake }
    return availablePositions.random()
}

private fun SnakeGameState.move(): SnakeGameState {
    if (isGameOver) return this

    val newHead = when (direction) {
        Direction.UP -> Point(snake.first().x, (snake.first().y - 1))
        Direction.DOWN -> Point(snake.first().x, (snake.first().y + 1))
        Direction.LEFT -> Point((snake.first().x - 1), snake.first().y)
        Direction.RIGHT -> Point((snake.first().x + 1), snake.first().y)
    }

    // 检查是否撞墙
    if (newHead.x < 0 || newHead.x >= BOARD_SIZE || newHead.y < 0 || newHead.y >= BOARD_SIZE) {
        return copy(isGameOver = true)
    }

    // 检查是否撞到自己
    if (newHead in snake) {
        return copy(isGameOver = true)
    }

    val newSnake = if (newHead == food) {
        listOf(newHead) + snake
    } else {
        listOf(newHead) + snake.dropLast(1)
    }

    return if (newHead == food) {
        copy(
            snake = newSnake,
            food = generateFood(newSnake),
            score = score + 10,
            moveDelay = maxOf(INITIAL_DELAY - score, MIN_DELAY)
        )
    } else {
        copy(snake = newSnake)
    }
}

private const val BOARD_SIZE = 15
private const val INITIAL_DELAY = 150L
private const val MIN_DELAY = 50L 