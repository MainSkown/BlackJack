package com.mainskown.blackjack.pages

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R

import com.mainskown.blackjack.components.CardButtonHand
import com.mainskown.blackjack.components.OutlinedText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent, // Make Scaffold background transparent
                    contentColor = MaterialTheme.colorScheme.onBackground
                ){ innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Settings button
                        IconButton(
                            onClick = {
                                // Transition to SettingsPage
                                val intent =
                                    Intent(this@MainActivity, SettingsPage::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(15.dp)
                                .align(Alignment.TopEnd)
                                .size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_settings),
                                contentDescription = "Settings",
                                tint = Color(0xFFFFFFFF),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize()
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Main Title
                            OutlinedText(
                                text = getString(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 50.dp)
                            )

                            // Cards
                            CardButtonHand(
                                cards = resources.getStringArray(R.array.main_menu_options).toList(),
                                onCardClick = { index ->
                                    when (index) {
                                        0 -> {
                                            // Transition to GamePage
                                            val intent =
                                                Intent(this@MainActivity, GamePage::class.java)
                                            startActivity(intent)
                                        }

                                        1 -> {
                                            // Transition to HighScoresPage
                                            val intent =
                                                Intent(this@MainActivity, HighScoresPage::class.java)
                                            startActivity(intent)
                                        }

                                        2 -> {
                                            // Transition to RulesPage
                                            val intent =
                                                Intent(this@MainActivity, RulesPage::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                },
                            )

                            // Customization button
                            OutlinedButton(
                                onClick = {
                                    // Transition to StylesPage
                                    val intent =
                                        Intent(this@MainActivity, StylesPage::class.java)
                                    startActivity(intent)
                                },
                                modifier = Modifier
                                    .padding(top = 30.dp)
                                    .align(Alignment.CenterHorizontally),
                                border = BorderStroke(1.dp, Color(0xFFFFFFFF)) // Gold color border
                            ) {
                                OutlinedText(
                                    text = getString(R.string.main_menu_customize),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}