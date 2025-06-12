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
    val skipIntro: Boolean = false,
    val language: String = "English",
    val languageIndex: Int = 0,
    val languageSelectorExpanded: Boolean = false,
)

class SettingsPageViewModel(private val sharedPreferences: SharedPreferences): ViewModel(){
    private val languageTags = listOf("en", "pl")
    private val _uiState = MutableStateFlow(SettingsPageUiState())
    val uiState: StateFlow<SettingsPageUiState> = _uiState.asStateFlow()
    val settingsPreferences: SettingsPreferences by lazy {
        SettingsPreferences(sharedPreferences)
    }

    init{
        val tag = settingsPreferences.language
        val idx = languageTags.indexOf(tag).takeIf { it >= 0 } ?: 0
        _uiState.value = SettingsPageUiState(
            soundVolume = settingsPreferences.soundVolume,
            musicVolume = settingsPreferences.musicVolume,
            skipIntro = settingsPreferences.skipIntro,
            language = tag,
            languageIndex = idx
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

    fun toggleLanguageSelector() {
        _uiState.value = _uiState.value.copy(languageSelectorExpanded = !_uiState.value.languageSelectorExpanded)
    }

    fun updateLanguage(tag: String, index: Int) {
        _uiState.value = _uiState.value.copy(
            language = tag,
            languageIndex = index,
            languageSelectorExpanded = false
        )
        settingsPreferences.updateLanguage(tag)
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