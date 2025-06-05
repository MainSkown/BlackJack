package com.mainskown.blackjack.models

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsPageUiState(
    val soundVolume: Float = 0.5f,
    val musicVolume: Float = 0.5f,
    val skipIntro: Boolean = false
)

class SettingsPageViewModel(private val sharedPreferences: SharedPreferences): ViewModel(){
    private val _uiState = MutableStateFlow(SettingsPageUiState())
    val uiState: StateFlow<SettingsPageUiState> = _uiState.asStateFlow()
    val settingsPreferences: SettingsPreferences by lazy {
        SettingsPreferences(sharedPreferences)
    }

    init{
        _uiState.value = SettingsPageUiState(
            soundVolume = settingsPreferences.soundVolume,
            musicVolume = settingsPreferences.musicVolume,
            skipIntro = settingsPreferences.skipIntro
        )
    }

    fun updateSoundVolume(value: Float) {
        _uiState.value = _uiState.value.copy(soundVolume = value)
        settingsPreferences.updateSoundVolume(value)
    }

    fun updateMusicVolume(value: Float) {
        _uiState.value = _uiState.value.copy(musicVolume = value)
        settingsPreferences.updateMusicVolume(value)
    }

    fun updateSkipIntro(value: Boolean) {
        _uiState.value = _uiState.value.copy(skipIntro = value)
        settingsPreferences.updateSkipIntro(value)
    }

    companion object {
        fun createFactory(sharedPreferences: SharedPreferences): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SettingsPageViewModel(sharedPreferences)
                }
            }
        }
    }
}