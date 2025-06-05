package com.mainskown.blackjack.models

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class IntroPageUiState(
    val startIntro: Boolean = false,
    val isVideoFinished: Boolean = false,
    val showTitle: Boolean = false,
    val currentPosition: Float = 0f,
    val videoDuration: Float = 100f, // Default value
    val overlayAlpha: Float = 0f,
    val titleAlpha: Float = 0f,
    val forceFullBlack: Boolean = false,  // New state to force full black overlay
    val readyForNavigation: Boolean = false // New state to control when navigation should occur
)

class IntroPageViewModel(private val sharedPreferences: SharedPreferences): ViewModel(){
    private val _uiState = MutableStateFlow(IntroPageUiState())
    val uiState: StateFlow<IntroPageUiState> = _uiState.asStateFlow()

    val settingsPreferences: SettingsPreferences by lazy {
        SettingsPreferences(this.sharedPreferences)
    }

    fun updateIsVideoFinished(finished: Boolean) {
        _uiState.value = _uiState.value.copy(isVideoFinished = finished)
        updateOverlayAlpha() // recalculate overlay alpha
    }

    fun updateShowTitle(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTitle = show)
        updateTitleAlpha() // recalculate title alpha
    }

    fun updateCurrentPosition(position: Float) {
        _uiState.value = _uiState.value.copy(currentPosition = position)
        updateOverlayAlpha() // recalculate overlay alpha
    }

    fun updateVideoDuration(duration: Float) {
        _uiState.value = _uiState.value.copy(videoDuration = duration)
        updateOverlayAlpha() // recalculate overlay alpha
    }

    fun forceBlackOverlay(force: Boolean) {
        _uiState.value = _uiState.value.copy(forceFullBlack = force)
    }

    fun setReadyForNavigation(ready: Boolean) {
        _uiState.value = _uiState.value.copy(readyForNavigation = ready)
    }

    // Modified to consider forceFullBlack state
    private fun updateOverlayAlpha() {
        val state = _uiState.value

        // If forcing full black, always set overlay to 1f regardless of other conditions
        if (state.forceFullBlack) {
            _uiState.value = state.copy(overlayAlpha = 1f)
            return
        }

        val fadeStartRatio = 0.7f
        val fadeEndRatio = 0.9f
        val ratio = if (state.videoDuration > 0) state.currentPosition / state.videoDuration else 0f
        val overlayAlpha = when {
            state.isVideoFinished -> 1f
            ratio > fadeEndRatio -> 1f
            ratio > fadeStartRatio -> {
                val progress = (ratio - fadeStartRatio) / (fadeEndRatio - fadeStartRatio)
                progress.coerceIn(0f, 1f)
            }
            else -> 0f
        }
        _uiState.value = state.copy(overlayAlpha = overlayAlpha)
    }

    private fun updateTitleAlpha() {
        val state = _uiState.value
        val titleAlpha = if (state.showTitle) 1f else 0f
        _uiState.value = state.copy(titleAlpha = titleAlpha)
    }

    companion object {
        fun createFactory(sharedPreferences: SharedPreferences): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    IntroPageViewModel(sharedPreferences)
                }
            }
        }
    }
}
