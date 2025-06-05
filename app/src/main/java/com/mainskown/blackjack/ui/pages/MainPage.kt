package com.mainskown.blackjack.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.SoundProvider
import com.mainskown.blackjack.models.SoundType
import com.mainskown.blackjack.ui.components.CardButtonHand
import com.mainskown.blackjack.ui.components.OutlinedText

@Composable
fun MainPage(navController: NavController) {
    LaunchedEffect(Unit) {
        SoundProvider.startPlayingMusic()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Settings button
        IconButton(
            onClick = {
                // Transition to SettingsPage
                SoundProvider.playSound(SoundType.BUTTON_CLICK)
                navController.navigate("settingsPage"){
                    launchSingleTop = true // Avoid multiple instances of the same page
                }
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main Title
            OutlinedText(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            // Cards
            CardButtonHand(
                cards = stringArrayResource(R.array.main_menu_options).toList(),
                onCardClick = { index ->
                    when (index) {
                        0 -> {
                            // Transition to GamePage
                            SoundProvider.playSound(SoundType.BUTTON_CLICK)
                            navController.navigate("gamePage")
                        }

                        1 -> {
                            // Transition to HighScoresPage
                            SoundProvider.playSound(SoundType.BUTTON_CLICK)
                            navController.navigate("highScoresPage")
                        }

                        2 -> {
                            // Transition to RulesPage
                            SoundProvider.playSound(SoundType.BUTTON_CLICK)
                            navController.navigate("rulesPage")
                        }
                    }
                },
            )

            // Customization button
            OutlinedButton(
                onClick = {
                    // Transition to StylesPage
                    SoundProvider.playSound(SoundType.BUTTON_CLICK)
                    navController.navigate("stylesPage")
                },
                modifier = Modifier
                    .padding(top = 30.dp)
                    .align(Alignment.CenterHorizontally),
                border = BorderStroke(1.dp, Color(0xFFFFFFFF)) // Gold color border
            ) {
                OutlinedText(
                    text = stringResource(R.string.main_menu_customize),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
