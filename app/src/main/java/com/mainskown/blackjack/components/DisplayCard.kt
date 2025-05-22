package com.mainskown.blackjack.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.models.Card

@Composable
fun DisplayCard(
    card: Card?,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    size: Dp = 80.dp,
    positionRead: ((Offset) -> Unit)? = null
) {
    var isFaceUp by remember { mutableStateOf(card?.isFaceUp) }

    // Sync state when card.isFaceUp changes externally
    LaunchedEffect(card?.isFaceUp) {
        isFaceUp = card?.isFaceUp
    }

    // Rotation angle for the Y-axis flip
    val rotation by animateFloatAsState(
        targetValue = if (isFaceUp == true) 0f else 180f,
        animationSpec = tween(durationMillis = 600),
        label = "CardRotation"
    )

    val isFrontVisible = rotation <= 90f


    Box(
        modifier = Modifier
            .size(size * 5 / 7, size)
            .then(modifier)
            .onGloballyPositioned { coordinates ->
                positionRead?.invoke(coordinates.positionInWindow())
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
    ) {
        if (!visible || card == null) return

        Image(
            bitmap = if (isFrontVisible) card.frontImage.asImageBitmap() else card.backImage.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Invert back face so it appears correctly when flipped
                    if (!isFrontVisible) {
                        rotationY = 180f
                    }
                }

        )
    }
}
