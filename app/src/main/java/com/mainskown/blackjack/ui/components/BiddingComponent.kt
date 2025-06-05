package com.mainskown.blackjack.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.SoundProvider
import com.mainskown.blackjack.models.SoundType
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun BiddingComponent(
    modifier: Modifier = Modifier,
    chips: Int,
    onBetSelected: (Int) -> Unit,
) {
    var betAmount by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            // Display amount of chips
            OutlinedText(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
    // Vertical slider for betting
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .padding(top = 50.dp, bottom = 50.dp)
                .align(Alignment.CenterStart),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedText(
                text = stringResource(R.string.game_chips, chips),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 30.sp,
            )
            OutlinedText(
                text = stringResource(R.string.game_betting, betAmount),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 30.sp,
            )
        }
        BettingVerticalSlider(
            modifier = Modifier,
            onBetSelected = { bet ->
                betAmount = when (bet) {
                    0 -> ceil(chips / 4.0).toInt()
                    1 -> ceil(chips / 2.0).toInt()
                    2 -> ceil(chips * 3 / 4.0).toInt()
                    3 -> chips
                    else -> betAmount
                }
            }
        )
    }

    // Bet button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = { SoundProvider.playSound(SoundType.BUTTON_CLICK); onBetSelected(betAmount) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White)
        ) {
            OutlinedText(
                text = stringResource(R.string.game_place_bet),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun BettingVerticalSlider(
    modifier: Modifier = Modifier,
    onBetSelected: (Int) -> Unit
) {
    val labels = stringArrayResource(R.array.game_bet_options)
    var sliderValue by remember { mutableFloatStateOf(0f) }

    // Calculate the selected index based on the slider value
    val selectedIndex = sliderValue.roundToInt().coerceIn(0, labels.size - 1)

    // Notify the selected bet
    LaunchedEffect(selectedIndex) {
        onBetSelected(selectedIndex)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Labels
        Column(
            modifier = Modifier
                .height(200.dp)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            labels.reversed().forEach { label ->
                OutlinedText(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Vertical Slider
        ColorfulIconSlider(
            value = sliderValue,
            onValueChange = { it: Float -> sliderValue = it.roundToInt().toFloat() },
            valueRange = 0f..(labels.size - 1).toFloat(),
            steps = labels.size - 2,
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = 270f
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints(
                            minWidth = constraints.minHeight,
                            maxWidth = constraints.maxHeight,
                            minHeight = constraints.minWidth,
                            maxHeight = constraints.maxHeight,
                        )
                    )
                    layout(placeable.height, placeable.width) {
                        placeable.place(-placeable.width, 0)
                    }
                }
                .width(200.dp)
                .height(50.dp),
            borderStroke = BorderStroke(1.dp, Color(0xFFFFFFFF)),
            colors = MaterialSliderDefaults.materialColors(
                inactiveTickColor = SliderBrushColor(color = Color.White),
                activeTickColor = SliderBrushColor(color = Color.Transparent),
                activeTrackColor = SliderBrushColor(color = Color(0xFFFF0000)),
                inactiveTrackColor = SliderBrushColor(color = Color.Transparent),
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.spade),
                contentDescription = "Volume",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(90f),
                colorFilter = ColorFilter.tint(Color(0xFFFF0000))
            )
        }
    }
}