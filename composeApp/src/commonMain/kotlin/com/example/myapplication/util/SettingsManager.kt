package com.example.myapplication.util

import com.russhwolf.settings.Settings

class SettingsManager {
    private val settings: Settings = Settings()

    // 音效设置
    var soundEnabled: Boolean
        get() = settings.getBoolean(SOUND_ENABLED, true)
        set(value) = settings.putBoolean(SOUND_ENABLED, value)

    // 震动设置
    var vibrationEnabled: Boolean
        get() = settings.getBoolean(VIBRATION_ENABLED, true)
        set(value) = settings.putBoolean(VIBRATION_ENABLED, value)

    // 游戏难度（0: 简单, 1: 普通, 2: 困难）
    var gameDifficulty: Int
        get() = settings.getInt(GAME_DIFFICULTY, 1)
        set(value) = settings.putInt(GAME_DIFFICULTY, value)

    // 主题模式（0: 跟随系统, 1: 浅色, 2: 深色）
    var themeMode: Int
        get() = settings.getInt(THEME_MODE, 0)
        set(value) = settings.putInt(THEME_MODE, value)

    // 动画效果
    var animationsEnabled: Boolean
        get() = settings.getBoolean(ANIMATIONS_ENABLED, true)
        set(value) = settings.putBoolean(ANIMATIONS_ENABLED, value)

    // 游戏速度（0.5 - 2.0倍速）
    var gameSpeed: Float
        get() = settings.getFloat(GAME_SPEED, 1.0f)
        set(value) = settings.putFloat(GAME_SPEED, value)

    // 自动保存
    var autoSaveEnabled: Boolean
        get() = settings.getBoolean(AUTO_SAVE_ENABLED, true)
        set(value) = settings.putBoolean(AUTO_SAVE_ENABLED, value)

    // 显示提示
    var showHints: Boolean
        get() = settings.getBoolean(SHOW_HINTS, true)
        set(value) = settings.putBoolean(SHOW_HINTS, value)

    // 语言设置（0: 简体中文, 1: English, 2: 日本語）
    var language: Int
        get() = settings.getInt(LANGUAGE, 0)
        set(value) = settings.putInt(LANGUAGE, value)

    // 无障碍模式
    var accessibilityMode: Boolean
        get() = settings.getBoolean(ACCESSIBILITY_MODE, false)
        set(value) = settings.putBoolean(ACCESSIBILITY_MODE, value)

    // 数据使用（0: 低, 1: 中, 2: 高）
    var dataUsage: Int
        get() = settings.getInt(DATA_USAGE, 1)
        set(value) = settings.putInt(DATA_USAGE, value)

    // 重置所有设置
    fun resetToDefaults() {
        soundEnabled = true
        vibrationEnabled = true
        gameDifficulty = 1
        themeMode = 0
        animationsEnabled = true
        gameSpeed = 1.0f
        autoSaveEnabled = true
        showHints = true
        language = 0
        accessibilityMode = false
        dataUsage = 1
    }

    companion object {
        private const val SOUND_ENABLED = "sound_enabled"
        private const val VIBRATION_ENABLED = "vibration_enabled"
        private const val GAME_DIFFICULTY = "game_difficulty"
        private const val THEME_MODE = "theme_mode"
        private const val ANIMATIONS_ENABLED = "animations_enabled"
        private const val GAME_SPEED = "game_speed"
        private const val AUTO_SAVE_ENABLED = "auto_save_enabled"
        private const val SHOW_HINTS = "show_hints"
        private const val LANGUAGE = "language"
        private const val ACCESSIBILITY_MODE = "accessibility_mode"
        private const val DATA_USAGE = "data_usage"
    }
} 