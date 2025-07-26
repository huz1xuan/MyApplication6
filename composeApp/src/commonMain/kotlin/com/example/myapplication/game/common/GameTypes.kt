package com.example.myapplication.game.common

import androidx.compose.ui.geometry.Size

// 坐标数据类
data class Point(val x: Int, val y: Int)

// 方向枚举
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

// 2048游戏状态
data class Game2048State(
    val board: List<List<Int>>,
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false,
    val isWin: Boolean = false
)

// 贪吃蛇游戏状态
data class SnakeGameState(
    val snake: List<Point>,
    val food: Point,
    val direction: Direction,
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false,
    val moveDelay: Long = 150L
)

// 卡片数据类
data class Card(
    val id: Int,
    val value: Int,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

// 记忆翻牌游戏状态
data class MemoryGameState(
    val cards: List<Card>,
    val flippedCards: List<Card> = emptyList(),
    val score: Int = 0,
    val highScore: Int = 0,
    val moves: Int = 0,
    val isGameOver: Boolean = false
) 