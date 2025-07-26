package com.example.myapplication.repository

import com.example.myapplication.util.ScoreManager

interface GameRepository {
    fun getHighScore(gameKey: String): Int
    fun updateHighScore(gameKey: String, score: Int)
    fun clearAllScores()
}

class GameRepositoryImpl(private val scoreManager: ScoreManager) : GameRepository {
    
    override fun getHighScore(gameKey: String): Int {
        return scoreManager.getHighScore(gameKey)
    }
    
    override fun updateHighScore(gameKey: String, score: Int) {
        scoreManager.updateHighScore(gameKey, score)
    }
    
    override fun clearAllScores() {
        // 清除所有游戏的分数
        scoreManager.updateHighScore(ScoreManager.GAME_2048, 0)
        scoreManager.updateHighScore(ScoreManager.GAME_SNAKE, 0)
        scoreManager.updateHighScore(ScoreManager.GAME_MEMORY, 0)
    }
} 