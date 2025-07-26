package com.example.myapplication.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.myapplication.game.common.Direction
import com.example.myapplication.game.common.Game2048State
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random
import com.example.myapplication.ui.common.PauseOverlay
import com.example.myapplication.ui.common.GeometricGameBackground
import com.example.myapplication.util.ScoreManager
import com.example.myapplication.ui.common.HighScoreButton
import com.example.myapplication.ui.common.GameHeader
import com.example.myapplication.ui.common.GameControlButtons
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val GRID_SIZE = 6 // 改为6x6
private const val WIN_VALUE = 128 // 改为128
private const val ANIMATION_DURATION = 150 // 减少动画时间
private const val MIN_SWIPE_DISTANCE = 30f // 减小滑动阈值，提高响应度
private const val MERGE_SCALE = 1.2f
private const val NEW_TILE_SCALE = 0f
private val THEME_COLOR = Color(0xFFE65100) // 暖色调主题

@Composable
fun Game2048Screen(paused: Boolean = false, onBack: (() -> Unit)? = null) {
    val koinHelper = remember { object : KoinComponent {} }
    val scoreManager: ScoreManager by koinHelper.inject()
    
    var gameState by remember { mutableStateOf(createInitialState(scoreManager)) }
    var isPaused by remember { mutableStateOf(paused) }
    val scope = rememberCoroutineScope()
    var canMove by remember { mutableStateOf(true) }

    // 更新最高分
    LaunchedEffect(gameState.score) {
        if (gameState.score > gameState.highScore) {
            scoreManager.updateHighScore(ScoreManager.GAME_2048, gameState.score)
            gameState = gameState.copy(highScore = gameState.score)
        }
    }

    // 检查胜利条件
    LaunchedEffect(gameState.board) {
        if (gameState.board.any { row -> row.any { it >= WIN_VALUE } }) {
            gameState = gameState.copy(isWin = true)
        }
    }

    GeometricGameBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 使用共享头部组件
                GameHeader(
                    score = gameState.score,
                    highScore = gameState.highScore,
                    themeColor = Color(0xFFFF4081)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 游戏板
                Box(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            var startX = 0f
                            var startY = 0f
                            var hasMoved = false

                            detectDragGestures(
                                onDragStart = { offset ->
                                    startX = offset.x
                                    startY = offset.y
                                    hasMoved = false
                                },
                                onDragCancel = {
                                    hasMoved = false
                                },
                                onDragEnd = {
                                    hasMoved = false
                                },
                                onDrag = { change, _ ->
                                    if (!canMove || isPaused || gameState.isGameOver || gameState.isWin || hasMoved) {
                                        return@detectDragGestures
                                    }

                                    val currentX = change.position.x
                                    val currentY = change.position.y
                                    val dx = currentX - startX
                                    val dy = currentY - startY

                                    val threshold = 50f

                                    if (abs(dx) > threshold || abs(dy) > threshold) {
                                        hasMoved = true
                                        scope.launch {
                                            canMove = false
                                            val direction = when {
                                                abs(dx) > abs(dy) && dx > 0 -> Direction.RIGHT
                                                abs(dx) > abs(dy) && dx < 0 -> Direction.LEFT
                                                abs(dy) > abs(dx) && dy > 0 -> Direction.UP // 修改：向下滑动时方块向上移动
                                                else -> Direction.DOWN // 修改：向上滑动时方块向下移动
                                            }
                                            gameState = move(gameState, direction)
                                            delay(ANIMATION_DURATION.toLong())
                                            canMove = true
                                        }
                                    }
                                }
                            )
                        }
                ) {
                    BoardView(gameState.board)
                }

                // 使用共享控制按钮组件
                GameControlButtons(
                    isPaused = isPaused,
                    isGameOver = gameState.isGameOver,
                    themeColor = Color(0xFFFF4081),
                    onPauseResume = { isPaused = !isPaused },
                    onRestart = { 
                        gameState = createInitialState(scoreManager)
                        isPaused = false
                    },
                    onBack = { onBack?.invoke() }
                )
            }

            // 游戏结束遮罩
            if (gameState.isGameOver || gameState.isWin) {
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
                            if (gameState.isWin) "胜利！" else "游戏结束",
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
                                containerColor = Color(0xFFFF4081)
                            )
                        ) {
                            Text("重新开始")
                        }
                    }
                }
            }

            // 胜利遮罩
            if (gameState.isWin) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(32.dp)
                    ) {
                        Text(
                            "胜利！",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "得分: ${gameState.score}",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                gameState = createInitialState(scoreManager)
                                isPaused = false
                            }
                        ) {
                            Text("再来一局")
                        }
                    }
                }
            }

            // 暂停遮罩
            if (isPaused && !gameState.isGameOver && !gameState.isWin) {
                PauseOverlay(
                    onResume = { isPaused = false },
                    onRestart = {
                        gameState = createInitialState(scoreManager)
                        isPaused = false
                    },
                    onBack = onBack,
                    themeColor = Color(0xFFFF4081)
                )
            }
        }
    }
}

@Composable
fun BoardView(board: List<List<Int>>) {
    val totalSize = 270.dp
    val spacing = 3.dp
    val cellSize = (totalSize - spacing * (GRID_SIZE - 1)) / GRID_SIZE

    Box(
        modifier = Modifier
            .size(totalSize)
            .background(Color(0xFFBBADA0), RoundedCornerShape(6.dp))
            .padding(0.dp),
        contentAlignment = Alignment.TopStart
    ) {
        // 背景网格
        repeat(GRID_SIZE) { row ->
            repeat(GRID_SIZE) { col ->
                Box(
                    modifier = Modifier
                        .offset(
                            x = col * (cellSize + spacing),
                            y = row * (cellSize + spacing)
                        )
                        .size(cellSize)
                        .background(Color(0xFFCDC1B4), RoundedCornerShape(3.dp))
                )
            }
        }

        // 数字方块
        board.forEachIndexed { row, rowCells ->
            rowCells.forEachIndexed { col, value ->
                if (value > 0) {
                    AnimatedTile(
                        value = value,
                        row = row,
                        col = col,
                        cellSize = cellSize,
                        spacing = spacing
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedTile(
    value: Int,
    row: Int,
    col: Int,
    cellSize: Dp,
    spacing: Dp
) {
    var isNew by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(
        targetValue = if (isNew) 1f else NEW_TILE_SCALE,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(value) {
        isNew = false
        delay(50)
        isNew = true
    }

    Box(
        modifier = Modifier
            .offset(
                x = col * (cellSize + spacing),
                y = row * (cellSize + spacing)
            )
            .size(cellSize)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                transformOrigin = TransformOrigin.Center
            )
            .background(getTileColor(value), RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            fontSize = when {
                value < 100 -> cellSize.value.sp * 0.4f
                value < 1000 -> cellSize.value.sp * 0.35f
                else -> cellSize.value.sp * 0.3f
            },
            fontWeight = FontWeight.Bold,
            color = if (value <= 4) Color(0xFF776E65) else Color.White
        )
    }
}

private fun createInitialState(scoreManager: ScoreManager): Game2048State {
    // 创建空棋盘
    val emptyBoard = List(GRID_SIZE) { List(GRID_SIZE) { 0 } }
    
    // 添加两个初始数字
    val board = addRandomTile(addRandomTile(emptyBoard))
    
    return Game2048State(board = board, highScore = scoreManager.getHighScore(ScoreManager.GAME_2048))
}

private fun addRandomTile(board: List<List<Int>>): List<List<Int>> {
    val emptyCells = mutableListOf<Pair<Int, Int>>()
    board.forEachIndexed { row, rowCells ->
        rowCells.forEachIndexed { col, value ->
            if (value == 0) {
                emptyCells.add(row to col)
            }
        }
    }
    
    if (emptyCells.isEmpty()) return board
    
    val (row, col) = emptyCells.random()
    val value = if (Random.nextFloat() < 0.9f) 2 else 4
    
    return board.mapIndexed { r, rowCells ->
        if (r == row) {
            rowCells.mapIndexed { c, cell ->
                if (c == col) value else cell
            }
        } else {
            rowCells
        }
    }
}

private suspend fun move(state: Game2048State, direction: Direction): Game2048State {
    val oldBoard = state.board
    var newBoard = oldBoard
    var scoreGain = 0
    
    when (direction) {
        Direction.LEFT -> {
            newBoard = rotateBoard(newBoard, 0)
            val result = mergeBoardLeft(newBoard)
            newBoard = result.first
            scoreGain = result.second
            newBoard = rotateBoard(newBoard, 0)
        }
        Direction.RIGHT -> {
            newBoard = rotateBoard(newBoard, 2)
            val result = mergeBoardLeft(newBoard)
            newBoard = result.first
            scoreGain = result.second
            newBoard = rotateBoard(newBoard, 2)
        }
        Direction.UP -> {
            newBoard = rotateBoard(newBoard, 1)
            val result = mergeBoardLeft(newBoard)
            newBoard = result.first
            scoreGain = result.second
            newBoard = rotateBoard(newBoard, 3)
        }
        Direction.DOWN -> {
            newBoard = rotateBoard(newBoard, 3)
            val result = mergeBoardLeft(newBoard)
            newBoard = result.first
            scoreGain = result.second
            newBoard = rotateBoard(newBoard, 1)
        }
    }
    
    // 如果有变化，添加新方块
    if (newBoard != oldBoard) {
        delay(ANIMATION_DURATION.toLong())
        newBoard = addRandomTile(newBoard)
        
        // 检查游戏是否结束
        val isGameOver = !canMove(newBoard)
        
        return state.copy(
            board = newBoard,
            score = state.score + scoreGain,
            isGameOver = isGameOver
        )
    }
    
    return state
}

private fun rotateBoard(board: List<List<Int>>, times: Int): List<List<Int>> {
    var result = board
    repeat(times % 4) {
        result = List(GRID_SIZE) { row ->
            List(GRID_SIZE) { col ->
                result[GRID_SIZE - 1 - col][row]
            }
        }
    }
    return result
}

private fun mergeBoardLeft(board: List<List<Int>>): Pair<List<List<Int>>, Int> {
    var scoreGain = 0
    val newBoard = board.map { row ->
        val merged = mutableListOf<Int>()
        var i = 0
        while (i < row.size) {
            if (row[i] == 0) {
                i++
                continue
            }
            if (i + 1 < row.size && row[i] == row[i + 1]) {
                merged.add(row[i] * 2)
                scoreGain += row[i] * 2
                i += 2
            } else {
                merged.add(row[i])
                i++
            }
        }
        merged + List(GRID_SIZE - merged.size) { 0 }
    }
    return newBoard to scoreGain
}

private fun canMove(board: List<List<Int>>): Boolean {
    // 检查是否有空格
    if (board.any { row -> row.any { it == 0 } }) return true
    
    // 检查是否有相邻的相同数字
    for (row in 0 until GRID_SIZE) {
        for (col in 0 until GRID_SIZE) {
            val current = board[row][col]
            // 检查右边
            if (col < GRID_SIZE - 1 && current == board[row][col + 1]) return true
            // 检查下边
            if (row < GRID_SIZE - 1 && current == board[row + 1][col]) return true
        }
    }
    return false
}

private fun getTileColor(value: Int): Color = when (value) {
    2 -> Color(0xFFEEE4DA)
    4 -> Color(0xFFEDE0C8)
    8 -> Color(0xFFF2B179)
    16 -> Color(0xFFF59563)
    32 -> Color(0xFFF67C5F)
    64 -> Color(0xFFF65E3B)
    128 -> Color(0xFFEDCF72)
    256 -> Color(0xFFEDCC61)
    512 -> Color(0xFFEDC850)
    1024 -> Color(0xFFEDC53F)
    2048 -> Color(0xFFEDC22E)
    else -> Color(0xFFEDC22E)
} 