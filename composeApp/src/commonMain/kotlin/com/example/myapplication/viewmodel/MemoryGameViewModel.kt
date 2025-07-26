package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.game.common.Card
import com.example.myapplication.game.common.MemoryGameState
import com.example.myapplication.repository.GameRepository
import com.example.myapplication.util.ScoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MemoryGameViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    companion object {
        const val GRID_SIZE = 4
        const val MATCH_DELAY = 1000L
        const val ANIMATION_DURATION = 300
    }
    
    var gameState by mutableStateOf(createInitialState())
        private set
    
    var isPaused by mutableStateOf(false)
        private set
    
    private fun createInitialState(): MemoryGameState {
        val totalPairs = (GRID_SIZE * GRID_SIZE) / 2
        val values = (1..totalPairs).toList()
        val allValues = values + values // Create pairs
        val shuffledValues = allValues.shuffled(Random)
        
        val cards = shuffledValues.mapIndexed { index, value ->
            Card(
                id = index,
                value = value,
                isFlipped = false,
                isMatched = false
            )
        }
        
        return MemoryGameState(
            cards = cards,
            flippedCards = emptyList(),
            score = 0,
            moves = 0,
            highScore = gameRepository.getHighScore(ScoreManager.GAME_MEMORY),
            isGameOver = false
        )
    }
    
    fun pauseGame() {
        isPaused = !isPaused
    }
    
    fun resetGame() {
        gameState = createInitialState()
        isPaused = false
    }
    
    fun flipCard(index: Int) {
        if (isPaused || gameState.isGameOver ||
            gameState.cards[index].isMatched ||
            gameState.cards[index].isFlipped ||
            gameState.flippedCards.size >= 2) {
            return
        }
        
        viewModelScope.launch {
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
                    // Match found
                    val updatedCards = newCards.map { card ->
                        if (card.isFlipped) {
                            card.copy(isMatched = true)
                        } else card
                    }
                    val newScore = gameState.score + 10
                    gameRepository.updateHighScore(ScoreManager.GAME_MEMORY, newScore)
                    
                    gameState = gameState.copy(
                        cards = updatedCards,
                        flippedCards = emptyList(),
                        score = newScore,
                        highScore = gameRepository.getHighScore(ScoreManager.GAME_MEMORY)
                    )
                    
                    // Check if game is completed
                    if (updatedCards.all { it.isMatched }) {
                        gameState = gameState.copy(isGameOver = true)
                    }
                } else {
                    // No match - flip back
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