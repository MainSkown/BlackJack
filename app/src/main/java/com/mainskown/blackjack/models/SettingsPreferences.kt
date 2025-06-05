package com.mainskown.blackjack.models

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

class SettingsPreferences(preferences: SharedPreferences) {
    private val sharedPreferences = preferences

    var soundVolume by mutableFloatStateOf(sharedPreferences.getFloat("sound_volume", 0.5f))
        private set
    var musicVolume by mutableFloatStateOf(sharedPreferences.getFloat("music_volume", 0.5f))
        private set
    var skipIntro by mutableStateOf<Boolean>(sharedPreferences.getBoolean("skip_intro", false))
        private set

    fun updateSoundVolume(value: Float) {
        sharedPreferences.edit { putFloat("sound_volume", value) }
        soundVolume = value
    }

    fun updateMusicVolume(value: Float) {
        sharedPreferences.edit { putFloat("music_volume", value) }
        musicVolume = value
        SoundProvider.updateMusicVolume(value)
    }

    fun updateSkipIntro(value: Boolean) {
        sharedPreferences.edit { putBoolean("skip_intro", value) }
        skipIntro = value
    }

}