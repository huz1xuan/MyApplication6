package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.game.common.Direction
import com.example.myapplication.game.common.Game2048State
import com.example.myapplication.repository.GameRepository
import com.example.myapplication.util.ScoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class Game2048ViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    var gameState by mutableStateOf(createInitialState())
        private set
    
    var isPaused by mutableStateOf(false)
        private set
    
    var canMove by mutableStateOf(true)
        private set
    
    private fun createInitialState(): Game2048State {
        val initialBoard = List(6) { MutableList(6) { 0 } }
        
        // 添加两个初始方块
        repeat(2) {
            addRandomTile(initialBoard)
        }
        
        return Game2048State(
            board = initialBoard.map { it.toList() },
            score = 0,
            highScore = gameRepository.getHighScore(ScoreManager.GAME_2048),
            isGameOver = false,
            isWin = false
        )
    }
    
    private fun addRandomTile(board: List<MutableList<Int>>) {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in board.indices) {
            for (j in board[i].indices) {
                if (board[i][j] == 0) {
                    emptyCells.add(i to j)
                }
            }
        }
        
        if (emptyCells.isNotEmpty()) {
            val (row, col) = emptyCells.random()
            board[row][col] = if (Random.nextFloat() < 0.9f) 2 else 4
        }
    }
    
    fun move(direction: Direction) {
        if (!canMove || gameState.isGameOver || isPaused) return
        
        canMove = false
        viewModelScope.launch {
            val newBoard = gameState.board.map { it.toMutableList() }.toMutableList()
            val scoreAdded = performMove(newBoard, direction)
            
            if (scoreAdded > 0 || boardChanged(gameState.board, newBoard)) {
                addRandomTile(newBoard)
                
                val newScore = gameState.score + scoreAdded
                val newHighScore = if (newScore > gameState.highScore) {
                    gameRepository.updateHighScore(ScoreManager.GAME_2048, newScore)
                    newScore
                } else {
                    gameState.highScore
                }
                
                gameState = gameState.copy(
                    board = newBoard.map { it.toList() },
                    score = newScore,
                    highScore = newHighScore,
                    isWin = newBoard.any { row -> row.any { it >= 128 } },
                    isGameOver = isGameOver(newBoard)
                )
            }
            
            delay(150)
            canMove = true
        }
    }
    
    private fun performMove(board: List<MutableList<Int>>, direction: Direction): Int {
        var score = 0
        
        when (direction) {
            Direction.LEFT -> {
                for (row in board) {
                    score += moveRowLeft(row)
                }
            }
            Direction.RIGHT -> {
                for (row in board) {
                    row.reverse()
                    score += moveRowLeft(row)
                    row.reverse()
                }
            }
            Direction.UP -> {
                for (col in board[0].indices) {
                    val column = board.map { it[col] }.toMutableList()
                    score += moveRowLeft(column)
                    for (row in board.indices) {
                        board[row][col] = column[row]
                    }
                }
            }
            Direction.DOWN -> {
                for (col in board[0].indices) {
                    val column = board.map { it[col] }.toMutableList()
                    column.reverse()
                    score += moveRowLeft(column)
                    column.reverse()
                    for (row in board.indices) {
                        board[row][col] = column[row]
                    }
                }
            }
        }
        
        return score
    }
    
    private fun moveRowLeft(row: MutableList<Int>): Int {
        var score = 0
        var writeIndex = 0
        
        // 移动非零元素到左侧
        for (readIndex in row.indices) {
            if (row[readIndex] != 0) {
                row[writeIndex] = row[readIndex]
                if (writeIndex != readIndex) {
                    row[readIndex] = 0
                }
                writeIndex++
            }
        }
        
        // 合并相同元素
        for (i in 0 until writeIndex - 1) {
            if (row[i] == row[i + 1]) {
                row[i] *= 2
                score += row[i]
                row[i + 1] = 0
                
                // 移动后续元素
                for (j in i + 1 until writeIndex - 1) {
                    row[j] = row[j + 1]
                    row[j + 1] = 0
                }
                writeIndex--
            }
        }
        
        return score
    }
    
    private fun boardChanged(oldBoard: List<List<Int>>, newBoard: List<List<Int>>): Boolean {
        for (i in oldBoard.indices) {
            for (j in oldBoard[i].indices) {
                if (oldBoard[i][j] != newBoard[i][j]) {
                    return true
                }
            }
        }
        return false
    }
    
    private fun isGameOver(board: List<List<Int>>): Boolean {
        // 检查是否有空格
        for (row in board) {
            if (row.contains(0)) return false
        }
        
        // 检查是否可以合并
        for (i in board.indices) {
            for (j in board[i].indices) {
                val current = board[i][j]
                // 检查右侧
                if (j < board[i].size - 1 && board[i][j + 1] == current) return false
                // 检查下方
                if (i < board.size - 1 && board[i + 1][j] == current) return false
            }
        }
        
        return true
    }
    
    fun resetGame() {
        gameState = createInitialState()
        canMove = true
    }
    
    fun pauseGame() {
        isPaused = true
    }
    
    fun resumeGame() {
        isPaused = false
    }
} 