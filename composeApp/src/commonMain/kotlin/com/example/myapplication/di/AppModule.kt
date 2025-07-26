package com.example.myapplication.di

import com.example.myapplication.repository.GameRepository
import com.example.myapplication.repository.GameRepositoryImpl
import com.example.myapplication.util.ScoreManager
import com.example.myapplication.util.SettingsManager
import com.example.myapplication.viewmodel.Game2048ViewModel
import com.example.myapplication.viewmodel.SnakeGameViewModel
import com.example.myapplication.viewmodel.MemoryGameViewModel
import org.koin.dsl.module

val appModule = module {
    // Utilities
    single { ScoreManager() }
    single { SettingsManager() }
    
    // Repository
    single<GameRepository> { GameRepositoryImpl(get()) }
    
    // ViewModels
    factory { Game2048ViewModel(get()) }
    factory { SnakeGameViewModel(get()) }
    factory { MemoryGameViewModel(get()) }
}

val allModules = listOf(appModule) 