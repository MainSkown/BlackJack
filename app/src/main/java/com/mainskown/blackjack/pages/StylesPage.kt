package com.mainskown.blackjack.pages

import android.content.SharedPreferences
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.mainskown.blackjack.R
import com.mainskown.blackjack.components.DisplayCard
import com.mainskown.blackjack.models.BackgroundStyle
import com.mainskown.blackjack.models.Card
import com.mainskown.blackjack.models.CardStyle
import com.mainskown.blackjack.models.CardSuit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.mainskown.blackjack.components.OutlinedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun StylesPage(viewModel: StylesPageViewModel) {
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
                        viewModel.updateSelectedCardStyle(style)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 250.dp, height = 200.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSelected) Color(0xFFFFD700) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
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
                                context,
                                value = 1,
                                suit = CardSuit.SPADES,
                                isFaceUp = true,
                                style = style
                            )
                            val cardDown = Card(
                                context,
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
                        viewModel.updateSelectedBackgroundStyle(style)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 250.dp, height = 200.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSelected) Color(0xFFFFD700) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
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
                        var bitmap: android.graphics.Bitmap? = null
                        try {
                            val inputStream = assetManager.open(assetPath)
                            bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
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

data class StylesPageUiState(
    val selectedCardStyle: CardStyle = CardStyle.entries.first(),
    val selectedBackgroundStyle: BackgroundStyle = BackgroundStyle.entries.first()
)

class StylesPageViewModel(sharedPreferences: SharedPreferences) : ViewModel() {
    private val _uiState = MutableStateFlow(StylesPageUiState())
    val uiState: StateFlow<StylesPageUiState> = _uiState.asStateFlow()

    init {
        // Initialize the styles preferences
        val stylesPreferences = StylesPreferences(sharedPreferences)
        _uiState.value = StylesPageUiState(
            selectedCardStyle = stylesPreferences.cardStyle,
            selectedBackgroundStyle = stylesPreferences.backgroundStyle
        )
    }

    val stylesPreferences: StylesPreferences by lazy {
        StylesPreferences(sharedPreferences)
    }

    fun updateSelectedCardStyle(style: CardStyle) {
        _uiState.value = _uiState.value.copy(selectedCardStyle = style)
        stylesPreferences.cardStyle = style
    }

    fun updateSelectedBackgroundStyle(style: BackgroundStyle) {
        _uiState.value = _uiState.value.copy(selectedBackgroundStyle = style)
        stylesPreferences.backgroundStyle = style
    }
}


class StylesPreferences(preferences: SharedPreferences) {
    private val sharedPreferences = preferences

    var cardStyle: CardStyle = CardStyle.entries.first()
        get() {
            val value = sharedPreferences.getString("card_style", field.name) ?: field.name
            return CardStyle.valueOf(value)
        }
        set(value) {
            sharedPreferences.edit { putString("card_style", value.name) }
            field = value
        }

    var backgroundStyle: BackgroundStyle = BackgroundStyle.entries.first()
        get() {
            val storedValue =
                sharedPreferences.getString("background_style", field.name) ?: field.name
            return BackgroundStyle.valueOf(storedValue)
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    "background_style",
                    value.name
                )
            } // Store the enum name (not toString)
            field = value
        }
}

