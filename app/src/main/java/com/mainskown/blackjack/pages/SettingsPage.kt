package com.mainskown.blackjack.pages

import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import androidx.core.content.edit
import com.mainskown.blackjack.components.OutlinedText

class SettingsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = SettingsPreferences(this.getSharedPreferences(getString(R.string.preferences_settings_key), MODE_PRIVATE))

        setContent {
            BlackJackTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent, // Make Scaffold background transparent
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Main Title
                        OutlinedText(
                            text = getString(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        // Subtitle (Settings)
                        OutlinedText(
                            text = getString(R.string.settings_title),
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
                                text = getString(R.string.settings_sound_volume),
                            )
                            // Slider
                            ColorfulIconSlider(
                                value = preferences.soundVolume,
                                onValueChange = { volume -> preferences.updateSoundVolume(volume)},
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
                                text = getString(R.string.settings_music_volume),
                            )
                            // Slider
                            ColorfulIconSlider(
                                value = preferences.musicVolume,
                                onValueChange = { volume -> preferences.updateMusicVolume(volume)},
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
                                checked = preferences.skipIntro,
                                onCheckedChange = { skip ->
                                    preferences.updateSkipIntro(skip)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFFF0000),
                                    uncheckedColor = Color(0xFFFFFFFF)
                                )
                            )
                            OutlinedText(
                                text = getString(R.string.settings_skip_intro),
                            )
                        }

                        // Language selector
                        // TODO: Implement language selector
                    }
                }
            }
        }
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