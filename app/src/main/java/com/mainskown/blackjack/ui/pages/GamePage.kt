package com.mainskown.blackjack.ui.pages

import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mainskown.blackjack.ui.components.BiddingComponent
import com.mainskown.blackjack.ui.components.GameComponent
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.GameComponentViewModel
import com.mainskown.blackjack.models.GamePageViewModel
import com.mainskown.blackjack.models.SoundProvider
import com.mainskown.blackjack.models.SoundType
import kotlinx.coroutines.launch

@Composable
fun GamePage(viewModel: GamePageViewModel, navController: NavController) {
    BackHandler {
        // Navigate back to main page
        navController.navigate("mainPage"){
            popUpTo("mainPage") { inclusive = true } // Clear the back stack
            launchSingleTop = true // Avoid multiple instances of the same page
        }
        SoundProvider.playSound(SoundType.BUTTON_CLICK)
    }

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
