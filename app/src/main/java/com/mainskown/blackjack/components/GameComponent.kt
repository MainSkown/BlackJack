package com.mainskown.blackjack.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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

enum class GameResult {
    WIN, LOSE, DRAW
}

@Composable
fun GameComponent(
    context: Context,
    onGameEnd: (GameResult) -> Unit,
    chips: Int,
    bet: Int,
    modifier: Modifier = Modifier,
) {
    val deck = remember { Deck(context, CardStyle.CLASSIC) }
    val dealerHand = remember { mutableStateListOf<Card>() }
    val playerHand = remember { mutableStateListOf<Card>() }

    var deckPosition = remember { mutableStateOf(Offset.Zero) }
    var dealerHandPosition = remember { mutableStateOf(Offset.Zero) }
    var playerHandPosition = remember { mutableStateOf(Offset.Zero) }

    var dealersKey by remember { mutableIntStateOf(0) }
    var playersKey by remember { mutableIntStateOf(0) }
    var inAnimation by remember { mutableStateOf(false) }

    var gameStarted by remember { mutableStateOf(false) }
    var playerFinished by remember { mutableStateOf(false) }
    var gameEnded by remember { mutableStateOf(false) }

    var showResultDialog by remember { mutableStateOf(false) }
    var gameResult by remember { mutableStateOf(GameResult.LOSE) }

    var chipsTarget by remember { mutableIntStateOf(chips) }
    val animatedChips by animateIntAsState(
        targetValue = chipsTarget,
        animationSpec = tween(durationMillis = 800), label = "chipsAnim"
    )
    var resultAmount by remember { mutableIntStateOf(0) }

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
                text = stringResource(R.string.game_dealer_hand),
            )

            // Template for dealer's hand
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                displayDealerHand(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    cards = dealerHand,
                    cardSize = 130.dp,
                    globalPositionRead = { offset ->
                        dealerHandPosition.value = offset
                    }
                )
            }

            Text(
                text = stringResource(R.string.game_dealer_value, calcValue(dealerHand.toTypedArray())),
            )


            // Row to align chips/bet on the left and deck on the right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side: Chips and Bet
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.game_chips, animatedChips),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.game_betting, bet),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Right side: Deck
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
                        text = stringResource(R.string.game_deck),
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
                text = stringResource(R.string.game_value, calcValue(playerHand.toTypedArray())),
            )


            AnimatedVisibility(
                visible = gameStarted && !inAnimation && !playerFinished,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, // Start below the screen
                    animationSpec = tween(durationMillis = 200)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }, // Exit below the screen
                    animationSpec = tween(durationMillis = 200)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 25.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Hit button
                    Button(
                        onClick = {
                            playersKey++
                            inAnimation = true
                        },
                        modifier = Modifier
                            .size(90.dp, 40.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFFFD700), // Gold
                                shape = MaterialTheme.shapes.medium
                            ),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(
                            text = stringResource(R.string.game_hit),
                            color = Color.White
                        )
                    }

                    // Hold button
                    Button(
                        onClick = {
                            playerFinished = true
                        },
                        modifier = Modifier
                            .size(90.dp, 40.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFFFD700), // Gold
                                shape = MaterialTheme.shapes.medium
                            ),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(
                            text = stringResource(R.string.game_hold),
                            color = Color.White
                        )
                    }

                }
            }
        }

        var animationFaceDown by remember { mutableStateOf(false) }

        // Remember the drawn cards for dealer and player to prevent multiple draws during recomposition
        val dealerCards = remember { mutableStateListOf<Card>() }
        val playerCards = remember { mutableStateListOf<Card>() }

        // Trigger the animation when positions are ready
        LaunchedEffect(deckPosition.value, dealerHandPosition.value, gameEnded) {
            if (!gameEnded && deckPosition.value != Offset.Zero && dealerHandPosition.value != Offset.Zero && dealerHand.isEmpty()) {
                deck.shuffle()

                // Start the game
                for (i in 0 until 4) {
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
                    while (inAnimation)
                        kotlinx.coroutines.delay(10)
                }

                // If dealer has blackjack, end the game
                if (calcValue(dealerHand.toTypedArray(), ignoreFaceDown = true) == 21) {
                    playerFinished = true
                    kotlinx.coroutines.delay(600)
                }
                // Same for the player
                if (calcValue(playerHand.toTypedArray()) == 21 && !playerFinished) {
                    playerFinished = true
                    kotlinx.coroutines.delay(600)
                }
                gameStarted = true
            }
        }

        // Calculate the player's hand value
        LaunchedEffect(playerHand.toList()) {
            if (playerHand.isNotEmpty()) {
                val playerValue = calcValue(playerHand.toTypedArray())
                if (playerValue >= 21) {
                    if(playerValue > 21) {
                        gameEnded = true
                        // Wait for a bit before ending the game for player to realise what happened
                        kotlinx.coroutines.delay(600)
                    }

                    playerFinished = true

                }
            }
        }

        LaunchedEffect(playerFinished) {
            if (playerFinished && !gameEnded) {
                // Dealer's turn: flip the first card by replacing it with a new instance
                if (dealerHand.isNotEmpty() && !dealerHand[0].isFaceUp) {
                    val old = dealerHand[0]
                    dealerHand[0] = Card(
                        context = old.context,
                        value = old.value,
                        suit = old.suit,
                        isFaceUp = true,
                        style = old.style
                    )
                    // Wait for flip the animation to end
                    kotlinx.coroutines.delay(600)
                }

                val playersValue = calcValue(playerHand.toTypedArray())

                if(playersValue != 21)
                    while (calcValue(dealerHand.toTypedArray()) < playersValue) {
                        dealersKey++
                        inAnimation = true

                        // Wait for animation to end
                        while (inAnimation)
                            kotlinx.coroutines.delay(10)
                    }

                // Wait for a bit before ending the game for player to realise what happened
                kotlinx.coroutines.delay(600)
                gameEnded = true
            }
        }

        // End game
        LaunchedEffect(gameEnded) {
            if (gameEnded) {
                val dealersValue = calcValue(dealerHand.toTypedArray())
                val playersValue = calcValue(playerHand.toTypedArray())

                // Check for win/loss
                gameResult =
                        // Game lost if player value is greater than 21
                    if (playersValue > 21 || (dealersValue <= 21 && playersValue < dealersValue)) {
                        chipsTarget -= bet
                        resultAmount = -bet
                        GameResult.LOSE
                    } else {
                        // Game won if player value is greater than dealer value or dealer exceeds 21
                        if (playersValue > dealersValue || dealersValue > 21) {
                            chipsTarget += bet
                            resultAmount = bet
                            GameResult.WIN
                        }  else {
                            // Game draw if player value is equal to dealer value
                            chipsTarget += 0
                            resultAmount = 0
                            GameResult.DRAW
                        }
                    }
                showResultDialog = true
            }}

            if (showResultDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text(
                            text = when (gameResult) {
                                GameResult.WIN -> stringResource(R.string.game_won)
                                GameResult.LOSE -> stringResource(R.string.game_lost)
                                GameResult.DRAW -> stringResource(R.string.game_draw)

                            },
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.fillMaxWidth(),
                            color = when (gameResult) {
                                GameResult.WIN -> Color(0xFF43A047)
                                GameResult.LOSE -> Color(0xFFD32F2F)
                                GameResult.DRAW -> Color(0xFFFFA000)
                            },
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.game_chips, animatedChips),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = when (gameResult) {
                                    GameResult.WIN -> "+${stringResource(R.string.game_chips_left, resultAmount)}"
                                    GameResult.LOSE -> stringResource(R.string.game_chips_left, resultAmount)
                                    GameResult.DRAW -> "+${stringResource(R.string.game_chips_left, 0)}"
                                },
                                color = when (gameResult) {
                                    GameResult.WIN -> Color(0xFF43A047)
                                    GameResult.LOSE -> Color(0xFFD32F2F)
                                    GameResult.DRAW -> Color(0xFFFFA000)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Button(
                                onClick = {
                                    showResultDialog = false
                                    onGameEnd(
                                        gameResult
                                    )
                                },
                                modifier = Modifier
                                    .size(120.dp, 40.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Color(0xFFFFD700), // Gold
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text(
                                    text = stringResource(R.string.game_continue),
                                    color = Color.White
                                )
                            }
                        }
                    })
        }

        // Dealing card to dealer's hand
        if (dealersKey > 0 && !gameEnded)
            key(dealersKey) {
                // Check if we've already drawn a card for this key
                if (dealerCards.size < dealersKey) {
                    // Draw a new card only if we haven't already for this key
                    val card = deck.drawCard().apply { isFaceUp = !animationFaceDown }
                    dealerCards.add(card)

                    dealCard(
                        hand = dealerHand,
                        card = card,
                        deckPosition = deckPosition.value,
                        handPosition = dealerHandPosition.value,
                        size = 130.dp,
                        onAnimationEnd = {
                            animationFaceDown = false
                            inAnimation = false
                        }
                    )
                } else {
                    // Use the previously drawn card
                    dealCard(
                        hand = dealerHand,
                        card = dealerCards[dealersKey - 1],
                        deckPosition = deckPosition.value,
                        handPosition = dealerHandPosition.value,
                        size = 130.dp,
                        onAnimationEnd = {
                            animationFaceDown = false
                            inAnimation = false
                        }
                    )
                }
            }

        // Dealing card to player's hand
        if (playersKey > 0 && !gameEnded)
            key(playersKey) {
                // Check if we've already drawn a card for this key
                if (playerCards.size < playersKey) {
                    // Draw a new card only if we haven't already for this key
                    val card = deck.drawCard()
                    playerCards.add(card)

                    dealCard(
                        hand = playerHand,
                        card = card,
                        deckPosition = deckPosition.value,
                        handPosition = playerHandPosition.value,
                        size = 130.dp,
                        onAnimationEnd = {
                            animationFaceDown = false
                            inAnimation = false
                        }
                    )
                } else {
                    // Use the previously drawn card
                    dealCard(
                        hand = playerHand,
                        card = playerCards[playersKey - 1],
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
    ignoreFaceDown: Boolean = false
): Int {
    var value = 0
    var aces = 0

    for (card in hand) {
        if (!ignoreFaceDown && !card.isFaceUp) continue

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
