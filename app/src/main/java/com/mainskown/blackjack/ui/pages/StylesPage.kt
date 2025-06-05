package com.mainskown.blackjack.ui.pages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.mainskown.blackjack.ui.components.DisplayCard
import com.mainskown.blackjack.models.BackgroundStyle
import com.mainskown.blackjack.models.Card
import com.mainskown.blackjack.models.CardStyle
import com.mainskown.blackjack.models.CardSuit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.mainskown.blackjack.models.SoundProvider
import com.mainskown.blackjack.models.SoundType
import com.mainskown.blackjack.models.StylesPageViewModel
import com.mainskown.blackjack.ui.components.OutlinedText

@Composable
fun StylesPage(viewModel: StylesPageViewModel, navController: NavController) {
    BackHandler {
        // Navigate back to main page
        navController.navigate("mainPage"){
            popUpTo("mainPage") { inclusive = true } // Clear the back stack
            launchSingleTop = true // Avoid multiple instances of the same page
        }
        SoundProvider.playSound(SoundType.BUTTON_CLICK)
    }

    val uiState by viewModel.uiState.collectAsState()
    var selectedCardStyle = uiState.selectedCardStyle
    var selectedBackgroundStyle = uiState.selectedBackgroundStyle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedText(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card Style Selection
            OutlinedText(
                text = stringResource(R.string.styles_card_styles),
                style = MaterialTheme.typography.titleMedium
            )
            CardStyle.entries.forEach { style ->
                // Preview button with cards in Card Style
                val context = LocalContext.current
                val isSelected = selectedCardStyle == style
                OutlinedButton(
                    onClick = {
                        SoundProvider.playSound(SoundType.BUTTON_CLICK)
                        viewModel.updateSelectedCardStyle(style)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 250.dp, height = 200.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSelected) Color(0xFFFFD700) else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row {
                            val cardUp = Card(
                                viewModel.assetManager,
                                value = 1,
                                suit = CardSuit.SPADES,
                                isFaceUp = true,
                                style = style
                            )
                            val cardDown = Card(
                                viewModel.assetManager,
                                value = 1,
                                suit = CardSuit.SPADES,
                                isFaceUp = false,
                                style = style
                            )
                            DisplayCard(card = cardUp, size = 130.dp)
                            DisplayCard(card = cardDown, size = 130.dp)
                        }
                        OutlinedText(
                            text = stringResource(
                                id = context.resources.getIdentifier(
                                    "styles_card_${style.name.lowercase()}",
                                    "string",
                                    context.packageName
                                ),
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Background Style Selection
            OutlinedText(
                text = stringResource(R.string.styles_background_styles),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            BackgroundStyle.entries.forEach { style ->
                val isSelected = selectedBackgroundStyle == style
                OutlinedButton(
                    onClick = {
                        SoundProvider.playSound(SoundType.BUTTON_CLICK)
                        viewModel.updateSelectedBackgroundStyle(style)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 250.dp, height = 200.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSelected) Color(0xFFFFD700) else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    // Show background image and name
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val context = LocalContext.current
                        // Load image from assets/backgrounds
                        val assetManager = context.assets
                        val assetPath = "backgrounds/${style.name.lowercase()}.png"
                        var bitmap: Bitmap? = null
                        try {
                            val inputStream = assetManager.open(assetPath)
                            bitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream.close()
                        } catch (_: Exception) {
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = style.name,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        OutlinedText(
                            text = stringResource(
                                id = context.resources.getIdentifier(
                                    "styles_background_${style.name.lowercase()}",
                                    "string",
                                    context.packageName
                                ),
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
