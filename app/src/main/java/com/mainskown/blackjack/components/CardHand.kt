package com.mainskown.blackjack.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.models.Card
import com.mainskown.blackjack.models.CardSuit
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun displayCardHand(
    modifier: Modifier = Modifier,
    cards: List<Card>,
    cardSize: Dp = 130.dp,
    rotateStep: Float = 10f,
    arcRadius: Dp = 600.dp,
    globalPositionRead: ((Offset) -> Unit)? = null
) {
    val density = LocalDensity.current
    val arcRadiusPx = with(density) { arcRadius.toPx() }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        // If there are no cards, use fake invisible card
        DisplayCard(
            card = null,
            modifier = Modifier.onGloballyPositioned { coordinates ->
                globalPositionRead?.invoke(coordinates.positionOnScreen())
            },
            visible = false,
            size = cardSize
        )

        val totalAngle = (cards.size - 1) * rotateStep
        val startAngle = -totalAngle / 2

        // Reverse card order, so the left one is on top
        cards.asReversed().forEachIndexed { reversedIndex, card ->
            val index = cards.size - 1 - reversedIndex
            val angle = startAngle + (index * rotateStep)
            val angleRad = Math.toRadians(angle.toDouble())

            // Calculate the x and y positions based on the angle
            val x_px = arcRadiusPx * sin(angleRad).toFloat()
            val y_px = arcRadiusPx * (1 - cos(angleRad).toFloat())

            val x_dp = with(density) { x_px.toDp() }
            val y_dp = with(density) { y_px.toDp() }

            DisplayCard(
                card = card,
                modifier = Modifier
                    .offset(x = x_dp, y = y_dp)
                    .rotate(angle),
                visible = true,
                size = cardSize
            )
        }
    }
}
