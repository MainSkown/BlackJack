package com.mainskown.blackjack.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.models.Card

@Composable
fun displayDealerHand(
    modifier: Modifier = Modifier,
    cards: List<Card>,
    cardSize: Dp = 130.dp,
    globalPositionRead: ((Offset) -> Unit)? = null
)  {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (cards.isEmpty()) {
            DisplayCard(
                card = null,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    globalPositionRead?.invoke(coordinates.positionOnScreen())
                },
                visible = false,
                size = cardSize
            )
            return@Box
        }

        // Center card position for globalPositionRead
        val centerIdx = cards.size / 2

        // Calculate overlap for cards that don't fit
        val overlap = if (cards.size * cardSize.value > 400) {
            // If cards would exceed 400dp total width, overlap them
            // Use a gentler overlap calculation to prevent excessive stacking
            val requiredWidth = cards.size * cardSize.value
            val availableWidth = 400f
            val neededOverlapTotal = requiredWidth - availableWidth

            // Divide by cards.size instead of (cards.size-1) for a more gentle overlap
            (neededOverlapTotal / cards.size).coerceAtMost(cardSize.value * 0.7f)
        } else 0f

        // Use absolute positioning to arrange cards
        Box(contentAlignment = Alignment.Center) {
            // Calculate card dimensions and spacing
            // Note: DisplayCard uses size(size * 5 / 7, size) so actual card width is 5/7 of cardSize
            val actualCardWidth = cardSize.value * 5 / 7

            // When cards need to overlap
            val cardSpacing = if (overlap > 0) {
                // Adjust for the actual card width
                actualCardWidth - (overlap * 5f / 7f)
            } else {
                // When cards don't need to overlap, position them adjacent
                actualCardWidth
            }

            // Calculate total width of all cards with spacing
            val totalWidth = actualCardWidth + (cards.size - 1) * cardSpacing

            // Start position for the first card (centered)
            val startX = -totalWidth / 2 + actualCardWidth / 2

            // Position each card
            cards.forEachIndexed { index, card ->
                val xPosition = startX + index * cardSpacing

                Box(
                    modifier = Modifier.offset(x = xPosition.dp, y = 0.dp)
                ) {
                    DisplayCard(
                        card = card,
                        modifier = Modifier.fillMaxSize(),
                        visible = true,
                        size = cardSize
                    )
                }
            }
        }
    }
}
