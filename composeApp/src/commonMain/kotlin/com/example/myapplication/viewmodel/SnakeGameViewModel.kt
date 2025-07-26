package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.game.common.Direction
import com.example.myapplication.game.common.Point
import com.example.myapplication.game.common.SnakeGameState
import com.example.myapplication.repository.GameRepository
import com.example.myapplication.util.ScoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SnakeGameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    companion object {
        const val BOARD_SIZE = 16
    }
    
    var gameState by mutableStateOf(createInitialState())
        private set
    
    var isPaused by mutableStateOf(false)
        private set
    
    private fun createInitialState(): SnakeGameState {
        val snake = listOf(Point(BOARD_SIZE / 2, BOARD_SIZE / 2))
        return SnakeGameState(
            snake = snake,
            food = generateFood(snake),
            direction = Direction.RIGHT,
            score = 0,
            highScore = gameRepository.getHighScore(ScoreManager.GAME_SNAKE),
            isGameOver = false
        )
    }
    
    private fun generateFood(snake: List<Point>): Point {
        var food: Point
        do {
            food = Point(Random.nextInt(BOARD_SIZE), Random.nextInt(BOARD_SIZE))
        } while (snake.contains(food))
        return food
    }
    
    fun startGame() {
        viewModelScope.launch {
            while (!gameState.isGameOver && !isPaused) {
                delay(150) // Game speed
                moveSnake()
            }
        }
    }
    
    fun pauseGame() {
        isPaused = !isPaused
    }
    
    fun resetGame() {
        gameState = createInitialState()
        isPaused = false
    }
    
    fun changeDirection(newDirection: Direction) {
        if (!isPaused && !gameState.isGameOver && newDirection != gameState.direction.opposite()) {
            gameState = gameState.copy(direction = newDirection)
        }
    }
    
    private fun moveSnake() {
        val head = gameState.snake.first()
        val newHead = when (gameState.direction) {
            Direction.UP -> Point(head.x, (head.y - 1 + BOARD_SIZE) % BOARD_SIZE)
            Direction.DOWN -> Point(head.x, (head.y + 1) % BOARD_SIZE)
            Direction.LEFT -> Point((head.x - 1 + BOARD_SIZE) % BOARD_SIZE, head.y)
            Direction.RIGHT -> Point((head.x + 1) % BOARD_SIZE, head.y)
        }
        
        // Check collision with self
        if (gameState.snake.contains(newHead)) {
            endGame()
            return
        }
        
        val newSnake = listOf(newHead) + gameState.snake
        var newFood = gameState.food
        var newScore = gameState.score
        
        // Check if food is eaten
        if (newHead == gameState.food) {
            newFood = generateFood(newSnake)
            newScore += 10
            gameRepository.updateHighScore(ScoreManager.GAME_SNAKE, newScore)
        } else {
            // Remove tail if no food eaten
            newSnake.dropLast(1)
        }
        
        gameState = gameState.copy(
            snake = if (newHead == gameState.food) newSnake else newSnake.dropLast(1),
            food = newFood,
            score = newScore,
            highScore = gameRepository.getHighScore(ScoreManager.GAME_SNAKE)
        )
    }
    
    private fun endGame() {
        gameState = gameState.copy(isGameOver = true)
    }
    
    private fun Direction.opposite(): Direction = when (this) {
        Direction.UP -> Direction.DOWN
        Direction.DOWN -> Direction.UP
        Direction.LEFT -> Direction.RIGHT
        Direction.RIGHT -> Direction.LEFT
    }
} 