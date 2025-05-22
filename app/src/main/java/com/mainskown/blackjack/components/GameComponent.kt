package com.mainskown.blackjack.components

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.CardStyle
import com.mainskown.blackjack.models.Deck
import com.mainskown.blackjack.models.Card
import com.mainskown.blackjack.models.CardSuit
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun GameComponent(
    context: Context,
    onGameEnd: (Boolean) -> Unit,
    chips: Int,
    bet: Int,
    modifier: Modifier = Modifier,
) {
    val deck = remember {  Deck(context, CardStyle.CLASSIC) }
    val dealerHand = remember { mutableStateListOf<Card>() }
    val playerHand = remember { mutableStateListOf<Card>() }

    var deckPosition = remember { mutableStateOf(Offset.Zero) }
    var dealerHandPosition = remember { mutableStateOf(Offset.Zero) }
    var playerHandPosition = remember { mutableStateOf(Offset.Zero) }

    var dealersKey by remember { mutableIntStateOf(0) }
    var playersKey by remember { mutableIntStateOf(0) }
    var inAnimation by remember { mutableStateOf(false) }

    var shouldStartGame by remember { mutableStateOf(false) }


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
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.game_chips, chips),
                style = MaterialTheme.typography.titleMedium,
            )
            // Display bet
            Text(
                text = stringResource(R.string.game_betting, bet),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Dealer's Hand",
            )

            // Template for dealer's hand
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (dealerHand.isEmpty()) {
                    // Fake card to get the position
                    DisplayCard(
                        modifier = Modifier
                            .fillMaxSize(),
                        card = null,
                        size = 130.dp,
                        visible = true,
                        positionRead = { offset ->
                            dealerHandPosition.value = offset
                        }
                    )
                }
                for (card in dealerHand) {
                    DisplayCard(
                        modifier = Modifier
                            .fillMaxSize(),
                        card = card,
                        size = 130.dp
                    )
                }
            }

            Text(
                text = "Dealer's value: ${calcValue(dealerHand.toTypedArray())}",
            )

            // A card that imitates deck
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    DisplayCard(
                        modifier = Modifier
                            .offset(x = (130.dp / 2))
                            .rotate(-90f),
                        card = Card(
                            context = context,
                            suit = CardSuit.DIAMONDS,
                            value = 1,
                            isFaceUp = false,
                        ),
                        size = 130.dp,
                        positionRead = { offset ->
                            deckPosition.value = offset
                        }
                    )
                    Text(
                        text = "Deck",
                        modifier = Modifier
                            .rotate(-90f)
                            .padding(bottom = 16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Player's hand
            displayCardHand(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                cards = playerHand,
                cardSize = 130.dp,
                rotateStep = 10f,
                arcRadius = 450.dp,
                globalPositionRead = { offset ->
                    playerHandPosition.value = offset
                }
            )
            Text(
                text = "Value: ${calcValue(playerHand.toTypedArray())}",
            )
        }
        var animationFaceDown by remember { mutableStateOf(false) }
        // Trigger the animation when positions are ready
        LaunchedEffect(deckPosition.value, dealerHandPosition.value) {
            if (deckPosition.value != Offset.Zero && dealerHandPosition.value != Offset.Zero && dealerHand.isEmpty()) {
                deck.shuffle()

                // Start the game
                for(i in 0 until 4){
                    if (i % 2 == 0) {
                        if (i == 0)
                            // First cards is faceDown
                            animationFaceDown = true

                        dealersKey++
                        inAnimation = true
                    } else {
                        playersKey++
                        inAnimation = true
                    }

                    // Wait for animation to end
                    while(inAnimation)
                        kotlinx.coroutines.delay(10)
                }

                shouldStartGame = true
            }
        }

        // Dealing card to dealer's hand
        if (dealersKey > 0)
            key(dealersKey) {
                dealCard(
                    hand = dealerHand,
                    card = deck.drawCard().apply { isFaceUp = !animationFaceDown },
                    deckPosition = deckPosition.value,
                    handPosition = dealerHandPosition.value,
                    size = 130.dp,
                    onAnimationEnd = {
                        animationFaceDown = false
                        inAnimation = false
                    }
                )
            }

        // Dealing card to player's hand
        if (playersKey > 0)
            key(playersKey) {
                dealCard(
                    hand = playerHand,
                    card = deck.drawCard(),
                    deckPosition = deckPosition.value,
                    handPosition = playerHandPosition.value,
                    size = 130.dp,
                    onAnimationEnd = {
                        animationFaceDown = false
                        inAnimation = false
                    }
                )
            }
    }
}

@Composable
fun AnimatedDealingCard(
    card: Card,
    deckPosition: Offset,
    handPosition: Offset,
    size: Dp = 130.dp,
    animationDuration: Int = 600,
    onAnimationEnd: (() -> Unit)? = null
) {
    val offset = 115f
    val startX = deckPosition.x
    // The Y value for some reason is not the same as in reality and this is a fix
    val startY = deckPosition.y - offset * 4
    val endX = handPosition.x
    val endY = handPosition.y - offset

    // Animatable values for position and rotation
    val animX = remember { Animatable(startX) }
    val animY = remember { Animatable(startY) }
    val animRotation = remember { Animatable(-90f) }

    var animationPlayed by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!animationPlayed) {
            animationPlayed = true
            coroutineScope {
                val xJob = launch {
                    animX.animateTo(
                        targetValue = endX,
                        animationSpec = tween(durationMillis = animationDuration)
                    )
                }
                val yJob = launch {
                    animY.animateTo(
                        targetValue = endY,
                        animationSpec = tween(durationMillis = animationDuration)
                    )
                }
                val rotJob = launch {
                    animRotation.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 400)
                    )
                }
                listOf(xJob, yJob, rotJob).joinAll()
            }

            // First call onAnimationEnd (which adds the card to the hand) 
            // before making the animated card invisible
            onAnimationEnd?.invoke()
            isVisible = false
        }
    }

    if (isVisible) {
        Box(
            Modifier
                .fillMaxSize()
        ) {

            DisplayCard(
                modifier = Modifier
                    .offset { IntOffset(animX.value.roundToInt(), animY.value.roundToInt()) }
                    .rotate(animRotation.value),
                card = card,
                size = size
            )

        }
    }
}

@Composable
fun dealCard(
    hand: MutableList<Card>,
    card: Card,
    deckPosition: Offset,
    handPosition: Offset,
    size: Dp = 130.dp,
    onAnimationEnd: (() -> Unit)? = null
) {
    AnimatedDealingCard(
        card = card,
        deckPosition = deckPosition,
        handPosition = handPosition,
        size = size,
        onAnimationEnd = {
            hand.add(card)
            onAnimationEnd?.invoke()
        }
    )
}

fun calcValue(
    hand: Array<Card>,
): Int {
    var value = 0
    var aces = 0

    for (card in hand) {
        if (!card.isFaceUp) continue

        if (card.value == 1) {
            aces++
        } else if (card.value >= 10) {
            value += 10
        } else {
            value += card.value
        }
    }

    // Add aces to the value
    for (i in 0 until aces) {
        value += if (value + 11 > 21) {
            1
        } else {
            11
        }
    }

    return value
}

