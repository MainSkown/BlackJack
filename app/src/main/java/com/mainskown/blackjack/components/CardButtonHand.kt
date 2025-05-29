package com.mainskown.blackjack.components

    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.rotate
    import androidx.compose.ui.platform.LocalDensity
    import androidx.compose.ui.unit.Dp
    import androidx.compose.ui.unit.dp
    import kotlin.math.cos
    import kotlin.math.sin

    @Composable
    fun CardButtonHand(
        cards: List<String>,
        onCardClick: (Int) -> Unit,
        cardSize: Dp = 170.dp,
        rotateStep: Float = 10f,
        arcRadius: Dp = 600.dp
    ) {
        val density = LocalDensity.current
        val arcRadiusPx = with(density) { arcRadius.toPx() }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            val totalAngle = (cards.size - 1) * rotateStep
            val startAngle = -totalAngle / 2

            // Reverse card order, so the left one is on top
            cards.asReversed().forEachIndexed { reversedIndex, text ->
                val index = cards.size - 1 - reversedIndex
                val angle = startAngle + (index * rotateStep)
                val angleRad = Math.toRadians(angle.toDouble())

                // Calculate the x and y positions based on the angle
                val x_px = arcRadiusPx * sin(angleRad).toFloat()
                val y_px = arcRadiusPx * (1 - cos(angleRad).toFloat())

                val x_dp = with(density) { x_px.toDp() }
                val y_dp = with(density) { y_px.toDp() }

                CardButton(
                    text = text,
                    onClick = { onCardClick(index) },
                    modifier = Modifier
                        .offset(x = x_dp, y = y_dp)
                        .rotate(angle),
                    size = cardSize,
                    cardSymbol = when (reversedIndex){
                        0 -> "♦"
                        1 -> "♠"
                        2 -> "♥"
                        else -> "♠"
                    }
                )
            }
        }
    }