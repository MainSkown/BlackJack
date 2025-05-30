package com.mainskown.blackjack.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import com.mainskown.blackjack.components.BiddingComponent
import com.mainskown.blackjack.components.GameComponent
import com.mainskown.blackjack.components.GameResult
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.DatabaseProvider
import com.mainskown.blackjack.models.GameData
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GamePage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent, // Make Scaffold background transparent
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) { innerPadding ->
                    val scope = rememberCoroutineScope()
                    var chips by remember { mutableIntStateOf(100) }// Starting chips
                    var betAmount by remember { mutableIntStateOf(25) } // Initial bet amount
                    var gameOn by remember { mutableStateOf(false) } // Game state
                    var gameID by remember { mutableLongStateOf(0L) } // Game ID for database

                    var loading by remember { mutableStateOf(true) } // Loading state
                    // Load the last game data from the database
                    val gameDao = DatabaseProvider.getDatabase(this.applicationContext).gameDao()

                    LaunchedEffect(Unit) {
                            try {
                                val lastGame = gameDao.getLastGame()
                                if (lastGame != null && lastGame.result == null) {
                                    chips = lastGame.chipsValue
                                    betAmount = lastGame.betValue
                                    gameID = lastGame.uid
                                    gameOn = true // Resume the game
                                }
                            } catch (e: Exception) {
                                // Handle error
                                e.printStackTrace()
                            } finally {
                                loading = false
                            }
                    }


                    // Add state for the "out of chips" dialog
                    var showOutOfChipsDialog by remember { mutableStateOf(false) }

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
                                    text = getString(R.string.game_over),
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
                                        text = getString(R.string.game_out_of_chips),
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = getString(R.string.game_would_you_like_to_play_again),
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
                                            chips = 100
                                            betAmount = 25
                                            gameOn = false
                                            showOutOfChipsDialog = false
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
                                            text = getString(R.string.game_new_game),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        )
                    }

                    /* Bidding Faze */
                    if(!loading && !showOutOfChipsDialog) {
                        if (!gameOn) {
                            BiddingComponent(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .padding(top = 50.dp),
                                chips = chips,
                                onBetSelected = { bet ->
                                    // Handle bet selection
                                    gameOn = true // Start the game
                                    // Update bet
                                    betAmount = bet

                                    // Add game data to the database
                                    val gameDao =
                                        DatabaseProvider.getDatabase(this.applicationContext)
                                            .gameDao()
                                    scope.launch {
                                        loading = true
                                        try {
                                            gameID = gameDao.insertGame(
                                                GameData(
                                                    chipsValue = chips,
                                                    betValue = betAmount,
                                                    date = LocalDateTime.now()
                                                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                                    result = null // Game is in progress
                                                )
                                            )
                                        } catch (e: Exception) {
                                            // Handle error
                                            e.printStackTrace()
                                        } finally {
                                            loading = false
                                        }
                                    }
                                }
                            )
                        }
                        /* Game Faze */
                        else {
                            GameComponent(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                context = this,
                                gameID = gameID,
                                onGameEnd = { gameResult ->
                                    // Handle game end
                                    // Update chips based on game result
                                    chips += when (gameResult) {
                                        GameResult.WIN -> betAmount
                                        GameResult.LOSE -> -betAmount
                                        GameResult.DRAW -> 0
                                    }
                                    gameOn = false // Reset game state

                                    // Update game result in the database
                                    val gameDao =
                                        DatabaseProvider.getDatabase(this.applicationContext)
                                            .gameDao()
                                    scope.launch {
                                        // Update the game result in the database
                                        // Check if gameID is valid
                                        if (gameID > 0) {
                                            // Update the game result in the database
                                            gameDao.updateGame(
                                                GameData(
                                                    uid = gameID,
                                                    chipsValue = chips,
                                                    betValue = betAmount,
                                                    date = LocalDateTime.now()
                                                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                                    result = gameResult
                                                )
                                            )
                                        } else {
                                            // Handle invalid gameID case
                                            throw IllegalStateException("Invalid gameID: $gameID")
                                        }

                                        DatabaseProvider.updateHighScores(this@GamePage)
                                    }
                                },
                                chips = chips,
                                bet = betAmount
                            )

                        }
                    }
                }
            }
        }
    }
}



