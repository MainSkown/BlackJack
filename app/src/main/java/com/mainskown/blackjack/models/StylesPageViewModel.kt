package com.mainskown.blackjack.models

import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StylesPageUiState(
    val selectedCardStyle: CardStyle = CardStyle.entries.first(),
    val selectedBackgroundStyle: BackgroundStyle = BackgroundStyle.entries.first()
)

class StylesPageViewModel(val assetManager: AssetManager, sharedPreferences: SharedPreferences) : ViewModel() {
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

    companion object {
        fun createFactory(
            assetManager: AssetManager,
            sharedPreferences: SharedPreferences
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    StylesPageViewModel(assetManager, sharedPreferences)
                }
            }
        }
    }
}