package com.mainskown.blackjack.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.components.GameResult
import com.mainskown.blackjack.models.DatabaseProvider
import com.mainskown.blackjack.models.GameData
import com.mainskown.blackjack.models.HighScores
import com.mainskown.blackjack.ui.theme.BlackJackTheme

class HighScoresPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var highScores by remember { mutableStateOf(HighScores()) } // Placeholder for high scores
                    var gameDataList =
                        remember { mutableStateListOf<GameData>() } // Placeholder for game data

                    LaunchedEffect(Unit) {
                        val gameDao = DatabaseProvider.getDatabase(this@HighScoresPage).gameDao()
                        val highScoresDao =
                            DatabaseProvider.getDatabase(this@HighScoresPage).highScoresDao()
                        gameDataList.apply {
                            addAll(gameDao.getAllGames())
                        }
                        highScores = highScoresDao.getHighScores() ?: HighScores()
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(top = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Main Title
                        Text(
                            text = "BlackJack",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFFFFFF),
                        )
                        // High Scores
                        Text(
                            text = "Best scores",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top=20.dp, bottom = 20.dp)
                        )
                        Text(
                            text = "Best chips: ${highScores.chipsValue}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = "Best bet: ${highScores.betValue}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = "Best streak: ${highScores.streak}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFFFFF),
                        )

                        // Statistics
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top=20.dp, bottom = 20.dp)
                        )
                        Text(
                            text = "Games played: ${gameDataList.size}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = "Games won: ${gameDataList.count { it.result == GameResult.WIN  }}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = "Games lost: ${gameDataList.count { it.result == GameResult.LOSE }}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = "Games draw: ${gameDataList.count { it.result == GameResult.DRAW }}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }
        }
    }
}
