package com.mainskown.blackjack.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R

import com.mainskown.blackjack.components.CardButtonHand

class RulesPage : ComponentActivity() {
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
                    val rules: Array<String> = resources.getStringArray(R.array.rules_rules);
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(start = 15.dp, end = 15.dp, top = 50.dp, bottom = 35.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Main Title
                        Text(
                            text = getString(R.string.app_name),
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFFFFFF),
                        )
                        // Subtitle (Rules)
                        Text(
                            text = getString(R.string.rules_title),
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 35.dp)
                        )
                        // Rules from array
                        for (i in rules.indices) {
                            Text(
                                text = buildAnnotatedString {
                                    // Append the rule number with bold style
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append((i + 1).toString() + ". ")
                                    }
                                    // Append the rule text
                                    append(rules[i])
                                },
                                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                                color = Color(0xFFFFFFFF),
                                textAlign = TextAlign.Center
                            )
                            if (i < rules.size - 1) {
                                Spacer(modifier = Modifier.padding(bottom = 15.dp))
                            }
                        }
                        Spacer(modifier = Modifier.padding(bottom = 35.dp))
                    }
                }
            }
        }
    }
}