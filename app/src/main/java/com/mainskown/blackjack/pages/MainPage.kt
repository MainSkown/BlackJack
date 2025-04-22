package com.mainskown.blackjack.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import com.mainskown.blackjack.components.CardButtonHand

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Buttons' box
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CardButtonHand(
                            cards = listOf("Start", "High Score", "Rules"),
                            onCardClick = { index ->
                                when (index) {
                                    0 -> { /* TODO: Start game logic */
                                    }

                                    1 -> { /* TODO: High score logic */
                                    }

                                    2 -> { /* TODO: Rules logic */
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlackJackTheme {
        Greeting("Android")
    }
}