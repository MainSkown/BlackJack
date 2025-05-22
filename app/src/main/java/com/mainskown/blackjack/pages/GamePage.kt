package com.mainskown.blackjack.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import com.mainskown.blackjack.components.BiddingComponent
import com.mainskown.blackjack.components.GameComponent

class GamePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var chips by remember { mutableIntStateOf(100) }// Starting chips
                    var betAmount by remember { mutableIntStateOf(25) } // Initial bet amount
                    var gameOn by remember { mutableStateOf(false) } // Game state
                    var gameKey by remember { mutableIntStateOf(0) } // Game key for re-composition
                    /* Bidding Faze */
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
                            onGameEnd = { gameResult ->
                                // Handle game end
                                // Update chips based on game result
                                chips += when (gameResult) {
                                    true -> betAmount
                                    false -> -betAmount
                                }
                                gameOn = false // Reset game state
                                gameKey += 1   // Force full recomposition and state reset
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

