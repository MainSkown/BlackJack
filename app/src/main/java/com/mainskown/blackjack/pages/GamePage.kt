package com.mainskown.blackjack.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import com.mainskown.blackjack.components.BiddingComponent

class GamePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val chips = 100 // Starting chips
                    var gameOn by remember { mutableStateOf(false) } // Game state
                    /* Bidding Faze */
                    if (!gameOn) {
                        BiddingComponent(
                            modifier = Modifier.
                                fillMaxSize()
                                .padding(innerPadding)
                                .padding(top= 50.dp),
                            chips = chips,
                            onBetSelected = { bet ->
                                // Handle bet selection
                                gameOn = true // Start the game
                            }
                        )
                    }
                    /* Game Faze */
                    else {
                        Box(
                            modifier = Modifier

                                .padding(innerPadding)
                        ) {
                            Text(
                                text = "Game Faze",
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

