package com.mainskown.blackjack.ui.pages

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.mainskown.blackjack.ui.components.OutlinedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun SettingsPage(viewModel: SettingsPageViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val soundVolume = uiState.soundVolume
    val musicVolume = uiState.musicVolume
    val skipIntro = uiState.skipIntro
    
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main Title
        OutlinedText(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
        )
        // Subtitle (Settings)
        OutlinedText(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 35.dp)
        )

        // Sound volume slider
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            OutlinedText(
                text = stringResource(R.string.settings_sound_volume),
            )
            // Slider
            ColorfulIconSlider(
                value = soundVolume,
                onValueChange = { volume -> viewModel.updateSoundVolume(volume) },
                valueRange = 0f..1f,
                steps = 20,
                borderStroke = BorderStroke(1.dp, Color(0xFFFFFFFF)),
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp),
                colors = MaterialSliderDefaults.materialColors(
                    inactiveTickColor = SliderBrushColor(color = Color.Transparent),
                    activeTickColor = SliderBrushColor(color = Color.Transparent),
                    activeTrackColor = SliderBrushColor(color = Color(0xFFFF0000)),
                    inactiveTrackColor = SliderBrushColor(color = Color.Transparent),
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.spade),
                    contentDescription = "Volume",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color(0xFFFF0000))
                )
            }
        }

        // Music volume slider
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            OutlinedText(
                text = stringResource(R.string.settings_music_volume),
            )
            // Slider
            ColorfulIconSlider(
                value = musicVolume,
                onValueChange = { volume -> viewModel.updateMusicVolume(volume) },
                valueRange = 0f..1f,
                steps = 20,
                borderStroke = BorderStroke(1.dp, Color(0xFFFFFFFF)),
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp),
                colors = MaterialSliderDefaults.materialColors(
                    inactiveTickColor = SliderBrushColor(color = Color.Transparent),
                    activeTickColor = SliderBrushColor(color = Color.Transparent),
                    activeTrackColor = SliderBrushColor(color = Color(0xFFFF0000)),
                    inactiveTrackColor = SliderBrushColor(color = Color.Transparent),
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clubs),
                    contentDescription = "Volume",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color(0xFFFF0000))
                )
            }
        }

        // Skip intro checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = skipIntro,
                onCheckedChange = { skip ->
                    viewModel.updateSkipIntro(skip)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFFF0000),
                    uncheckedColor = Color(0xFFFFFFFF)
                )
            )
            OutlinedText(
                text = stringResource(R.string.settings_skip_intro),
            )
        }

        // Language selector
        // TODO: Implement language selector
    }
}

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
}


class SettingsPreferences(preferences: SharedPreferences) {
    private val sharedPreferences = preferences

    var soundVolume by mutableStateOf(sharedPreferences.getFloat("sound_volume", 0.5f))
        private set
    var musicVolume by mutableStateOf(sharedPreferences.getFloat("music_volume", 0.5f))
        private set
    var skipIntro by mutableStateOf(sharedPreferences.getBoolean("skip_intro", false))
        private set

    fun updateSoundVolume(value: Float) {
        sharedPreferences.edit { putFloat("sound_volume", value) }
        soundVolume = value
    }

    fun updateMusicVolume(value: Float) {
        sharedPreferences.edit { putFloat("music_volume", value) }
        musicVolume = value
    }

    fun updateSkipIntro(value: Boolean) {
        sharedPreferences.edit { putBoolean("skip_intro", value) }
        skipIntro = value
    }
}