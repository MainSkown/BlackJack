package com.mainskown.blackjack.ui.pages

import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mainskown.blackjack.ui.components.BiddingComponent
import com.mainskown.blackjack.ui.components.GameComponent
import com.mainskown.blackjack.ui.components.GameResult
import com.mainskown.blackjack.R
import com.mainskown.blackjack.ui.components.GameComponentViewModel
import com.mainskown.blackjack.models.DatabaseProvider
import com.mainskown.blackjack.models.GameDao
import com.mainskown.blackjack.models.GameData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun GamePage(viewModel: GamePageViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    var chips = uiState.chips // Starting chips
    var betAmount = uiState.betAmount // Initial bet amount
    var gameOn = uiState.gameOn // Game state
    var gameID = uiState.gameID // Game ID for database
    var gameCount = uiState.gameCount // Game count for recomposition

    var loading = uiState.loading // Loading state

    // Add state for the "out of chips" dialog
    var showOutOfChipsDialog = uiState.showOutOfChipsDialog

    // Check if player is out of chips
    if (chips <= 0 && !showOutOfChipsDialog) {
        showOutOfChipsDialog = true
    }

    // Out of chips dialog
    if (showOutOfChipsDialog && !loading) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(R.string.game_over),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.game_out_of_chips),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.game_would_you_like_to_play_again),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = {
                            // Reset game with 100 chips
                            viewModel.resetGame()
                        },
                        modifier = Modifier
                            .size(120.dp, 40.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFFFD700), // Gold
                                shape = MaterialTheme.shapes.medium
                            ),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(
                            text = stringResource(R.string.game_new_game),
                            color = Color.White
                        )
                    }
                }
            }
        )
    }

    /* Bidding Faze */
    if (!loading && !showOutOfChipsDialog) {
        if (!gameOn) {
            BiddingComponent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                chips = chips,
                onBetSelected = { bet ->
                    scope.launch {
                        try {
                            viewModel.createGame(bet)
                        } catch (e: Exception) {
                            // Handle error
                            e.printStackTrace()
                        } finally {
                            loading = false
                            gameOn = true // Start the game
                        }
                    }
                }
            )
        }
        /* Game Faze */
        else {
            key(gameCount) { // Add key here
                GameComponent(
                    modifier = Modifier
                        .fillMaxSize(),
                    viewModel = viewModel(key = "${gameID}_$gameCount", factory = GameComponentViewModel.createFactory(
                        chips = chips,
                        bet = betAmount,
                        gameID = gameID,
                        gameDao = viewModel.gameDao,
                        sharedPreferences = viewModel.sharedPreferences,
                        assetManager = viewModel.assetManager,
                        onGameEnd = { result ->
                            scope.launch {
                                try {
                                    viewModel.onGameEnd(result)
                                } catch (e: Exception) {
                                    // Handle error
                                    e.printStackTrace()
                                }
                            }
                        }
                    ))
                )
            }
        }
    }
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
