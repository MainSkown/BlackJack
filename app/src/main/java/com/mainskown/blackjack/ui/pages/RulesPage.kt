package com.mainskown.blackjack.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.mainskown.blackjack.ui.components.OutlinedText

@Composable
fun RulesPage() {
    val rules: Array<String> = stringArrayResource(R.array.rules_rules);

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp, top = 50.dp, bottom = 35.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main Title
        OutlinedText(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
        )
        // Subtitle (Rules)
        OutlinedText(
            text = stringResource(R.string.rules_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 35.dp)
        )
        // Rules from array
        for (i in rules.indices) {
            OutlinedText(
                text = "${i + 1}. ${rules[i]}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            if (i < rules.size - 1) {
                Spacer(modifier = Modifier.padding(bottom = 15.dp))
            }
        }
        Spacer(modifier = Modifier.padding(bottom = 35.dp))
    }
}
