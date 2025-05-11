package com.mainskown.blackjack.pages

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
import androidx.compose.material3.Slider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

class SettingsPage : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Main Title
                        Text(
                            text = getString(R.string.app_name),
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFFFFFF),
                        )
                        // Subtitle (Settings)
                        Text(
                            text = getString(R.string.settings_title),
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(bottom = 35.dp)
                        )

                        // Sound volume slider
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            // Title
                            Text(
                                text = getString(R.string.settings_sound_volume),
                                color = Color(0xFFFFFFFF),
                            )
                            // Slider
                            ColorfulIconSlider(
                                value = 0.5f,
                                onValueChange = { /* Handle volume change */ },
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
                        ){
                            // Title
                            Text(
                                text = getString(R.string.settings_music_volume),
                                color = Color(0xFFFFFFFF),
                            )
                            // Slider
                            ColorfulIconSlider(
                                value = 0.5f,
                                onValueChange = { /* Handle volume change */ },
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
                                checked = false,
                                onCheckedChange = { /* Handle checkbox change */ },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFFF0000),
                                    uncheckedColor = Color(0xFFFFFFFF)
                                )
                            )
                            Text(
                                text = getString(R.string.settings_skip_intro),
                                color = Color(0xFFFFFFFF),
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