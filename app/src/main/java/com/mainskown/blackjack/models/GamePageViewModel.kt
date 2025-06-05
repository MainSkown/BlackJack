package com.mainskown.blackjack.models

import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class GameResult {
    WIN, LOSE, DRAW
}

data class GamePageUiState(
    var chips: Int = 100, // Starting chips
    var betAmount: Int = 25, // Initial bet amount
    var gameOn: Boolean = false, // Game state
    var gameID: Long = 0L, // Game ID for database
    var loading: Boolean = false, // Loading state
    var showOutOfChipsDialog: Boolean = false, // Show out of chips dialog
    var gameCount: Int = 0 // Game count for recomposition
)

class GamePageViewModel(
    val gameDao: GameDao,
    val sharedPreferences: SharedPreferences,
    val assetManager: AssetManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(GamePageUiState())
    val uiState: StateFlow<GamePageUiState> = _uiState.asStateFlow()

    private fun updateState(update: GamePageUiState.() -> Unit) {
        val newState = _uiState.value.copy()
        update(newState)
        _uiState.value = newState
    }

    init {
        // Load the last game data from the database
        viewModelScope.launch {
            try {
                val lastGame = gameDao.getLastGame()
                if (lastGame != null && lastGame.result == null) {
                    _uiState.value = GamePageUiState(
                        chips = lastGame.chipsValue,
                        betAmount = lastGame.betValue,
                        gameID = lastGame.uid,
                        gameOn = true // Resume the game
                    )
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    suspend fun createGame(betAmount: Int) {
        // Loading
        updateState {
            loading = true
        }

        val gameID = gameDao.insertGame(
            GameData(
                chipsValue = uiState.value.chips,
                betValue = uiState.value.betAmount,
                date = LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                result = null // Game is in progress
            )
        )

        updateState {
            this.gameID = gameID
            gameOn = true // Start the game
            this.betAmount = betAmount
            loading = false // Stop loading
        }
    }

    fun resetGame() {
        // Reset game state
        updateState {
            chips = 100 // Reset chips to 100
            betAmount = 25 // Reset bet amount to 25
            gameOn = false // Reset game state
            gameID = 0L // Reset game ID
            loading = false // Stop loading
            showOutOfChipsDialog = false // Hide out of chips dialog
        }
    }

    suspend fun onGameEnd(result: GameResult) {
        // Update chips based on game result
        updateState {
            chips += when (result) {
                GameResult.WIN -> uiState.value.betAmount
                GameResult.LOSE -> -uiState.value.betAmount
                GameResult.DRAW -> 0
            }
        }

        // Reset game state
        updateState {
            gameOn = false
            gameCount++ // Increment game count to trigger recomposition
        }

        // Update game result in the database
        // Check if gameID is valid
        if (uiState.value.gameID > 0) {
            // Update the game result in the database
            gameDao.updateGame(
                GameData(
                    uid = uiState.value.gameID,
                    chipsValue = uiState.value.chips,
                    betValue = uiState.value.betAmount,
                    date = LocalDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    result = result
                )
            )
        } else {
            // Handle invalid gameID case
            throw IllegalStateException("Invalid gameID: ${uiState.value.gameID}")
        }

        DatabaseProvider.updateHighScores(null)
    }

    companion object {
        fun createFactory(gameDao: GameDao, sharedPreferences: SharedPreferences, assetManager: AssetManager): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    GamePageViewModel(gameDao, sharedPreferences, assetManager)
                }
            }
        }
    }
}