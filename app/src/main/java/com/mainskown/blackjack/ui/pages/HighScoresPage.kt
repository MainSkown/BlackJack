package com.mainskown.blackjack.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.GameResult
import com.mainskown.blackjack.ui.components.OutlinedText
import com.mainskown.blackjack.models.HighScoresPageViewModel

@Composable
fun HighScoresPage(viewModel: HighScoresPageViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val gameDataList = uiState.gameDataList // Placeholder for game data
    val highScores = uiState.highScores // Placeholder for high scores


    Column(
        modifier = Modifier
            .fillMaxSize()           
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Title
        OutlinedText(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
        )
        // High Scores
        OutlinedText(
            text = stringResource(R.string.high_scores_best_scores),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
        )
        OutlinedText(
            text = stringResource(R.string.high_scores_best_chips, highScores.chipsValue),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedText(
            text = stringResource(R.string.high_scores_best_bet, highScores.betValue),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedText(
            text = stringResource(R.string.high_scores_best_streak, highScores.streak),
            style = MaterialTheme.typography.titleSmall,
        )

        // Statistics
        OutlinedText(
            text = stringResource(R.string.high_scores_statistics),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
        )
        OutlinedText(
            text = stringResource(R.string.high_scores_total_games, gameDataList.size),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedText(
            text = stringResource(
                R.string.high_scores_wins,
                gameDataList.count { it.result == GameResult.WIN }),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedText(
            text = stringResource(
                R.string.high_scores_losses,
                gameDataList.count { it.result == GameResult.LOSE }),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedText(
            text = stringResource(
                R.string.high_scores_draws,
                gameDataList.count { it.result == GameResult.DRAW }),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}



