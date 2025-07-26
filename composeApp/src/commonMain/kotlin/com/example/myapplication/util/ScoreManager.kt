package com.example.myapplication.util

import com.russhwolf.settings.Settings

class ScoreManager {
    private val settings: Settings = Settings()

    fun getHighScore(gameKey: String): Int {
        return settings.getInt(gameKey, 0)
    }

    fun updateHighScore(gameKey: String, score: Int) {
        val currentHighScore = settings.getInt(gameKey, 0)
        if (score > currentHighScore) {
            settings.putInt(gameKey, score)
        }
    }

    companion object {
        const val GAME_2048 = "game_2048"
        const val GAME_SNAKE = "game_snake"
        const val GAME_MEMORY = "game_memory"
    }
} 