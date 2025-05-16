package com.mainskown.blackjack.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import com.mainskown.blackjack.models.Card

@Composable
fun DisplayCard(
    card: Card,
    modifier: Modifier = Modifier,
) {
    var isFaceUp by remember { mutableStateOf(card.isFaceUp) }

    // Sync state when card.isFaceUp changes externally
    LaunchedEffect(card.isFaceUp) {
        isFaceUp = card.isFaceUp
    }

    // Rotation angle for the Y-axis flip
    val rotation by animateFloatAsState(
        targetValue = if (isFaceUp) 0f else 180f,
        animationSpec = tween(durationMillis = 600),
        label = "CardRotation"
    )

    val isFrontVisible = rotation <= 90f

    Box(
        modifier = modifier.graphicsLayer {
            rotationY = rotation
            cameraDistance = 12 * density
        }
    ) {
        Image(
            bitmap = if (isFrontVisible) card.frontImage.asImageBitmap() else card.backImage.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.graphicsLayer {
                // Invert back face so it appears correctly when flipped
                if (!isFrontVisible) {
                    rotationY = 180f
                }
            }
        )
    }
}
