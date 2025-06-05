package com.mainskown.blackjack.ui.components

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.Card
import com.mainskown.blackjack.models.CardSuit
import com.mainskown.blackjack.models.GameComponentViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class GameResult {
    WIN, LOSE, DRAW
}

@Composable
fun GameComponent(
    viewModel: GameComponentViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    var dealerHand = uiState.dealerHand
    val playerHand = uiState.playerHand

    val bet = uiState.bet

    // Needed for animation
    val deckPosition = remember { mutableStateOf(Offset.Zero) }
    val dealerHandPosition = remember { mutableStateOf(Offset.Zero) }
    val playerHandPosition = remember { mutableStateOf(Offset.Zero) }

    val dealersKey = uiState.dealersKey
    val playersKey = uiState.playersKey
    val inAnimation = uiState.inAnimation

    val gameStarted = uiState.gameStarted
    val playerFinished = uiState.playerFinished
    val gameEnded = uiState.gameEnded

    val showResultDialog = uiState.showResultDialog
    val gameResult = uiState.gameResult

    val chipsTarget = uiState.chips
    val animatedChips by animateIntAsState(
        targetValue = chipsTarget,
        animationSpec = tween(durationMillis = 800), label = "chipsAnim"
    )

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

            OutlinedText(
                text = stringResource(R.string.game_dealer_hand),
                style = MaterialTheme.typography.bodyLarge,
            )


            displayDealerHand(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                cards = dealerHand,
                cardSize = 130.dp,
                globalPositionRead = { offset ->
                    dealerHandPosition.value = offset
                }
            )


            OutlinedText(
                text = stringResource(
                    R.string.game_dealer_value,
                    viewModel.dealerValue(false)
                ),
                style = MaterialTheme.typography.bodyLarge,
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
                    OutlinedText(
                        text = stringResource(R.string.game_chips, animatedChips),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    OutlinedText(
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
                            viewModel.assetManager,
                            suit = CardSuit.DIAMONDS,
                            value = 1,
                            isFaceUp = false,
                            style = uiState.cardStyle
                        ),
                        size = 130.dp,
                        positionRead = { offset ->
                            deckPosition.value = offset
                        }
                    )
                    OutlinedText(
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
            OutlinedText(
                text = stringResource(R.string.game_value, viewModel.playerValue()),
                style = MaterialTheme.typography.bodyLarge,
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
                            viewModel.playerHit()
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
                        OutlinedText(
                            text = stringResource(R.string.game_hit),
                        )
                    }

                    // Hold button
                    Button(
                        onClick = {
                            viewModel.playerHold()
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
                        OutlinedText(
                            text = stringResource(R.string.game_hold),
                        )
                    }

                }
            }
        }

        // Trigger the animation when positions are ready
        LaunchedEffect(Unit) {
            // Wait until both positions are set
            while (deckPosition.value == Offset.Zero || dealerHandPosition.value == Offset.Zero) {
                delay(10)
            }

            viewModel.startGame()
        }

        // Calculate the player's hand value
        LaunchedEffect(playerHand.toList()) {
            viewModel.calculatePlayerValue()
        }

        LaunchedEffect(playerFinished) {
            viewModel.onPlayerFinished()
        }

        // End game
        LaunchedEffect(gameEnded) {
            viewModel.endGame()
        }

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
                                GameResult.WIN -> "+${
                                    stringResource(
                                        R.string.game_chips_left,
                                        bet
                                    )
                                }"

                                GameResult.LOSE -> "-${
                                    stringResource(
                                        R.string.game_chips_left,
                                        bet
                                    )
                                }"

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
                                viewModel.gameEnded()
                                viewModel.onGameEnd(gameResult)
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
                // Draw a new card only if we haven't already for this key
                val card = viewModel.drawCard(!uiState.animationFaceDown)

                dealCard(
                    card = card,
                    deckPosition = deckPosition.value,
                    handPosition = dealerHandPosition.value,
                    size = 130.dp,
                    onAnimationEnd = {
                        viewModel.addCardToDealerHand(card)
                        viewModel.setAnimationComplete()
                    }
                )
            }

        // Dealing card to player's hand
        if (playersKey > 0 && !gameEnded)
            key(playersKey) {
                // Draw a new card only if we haven't already for this key
                val card = viewModel.drawCard()

                dealCard(
                    card = card,
                    deckPosition = deckPosition.value,
                    handPosition = playerHandPosition.value,
                    size = 130.dp,
                    onAnimationEnd = {
                        viewModel.addCardToPlayerHand(card)
                        viewModel.setAnimationComplete()
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
    animationDuration: Int = 800,
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
            try {
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
            } catch (e: Exception) {
                // Log other exceptions if necessary
                e.printStackTrace()
            } finally {
                // Ensure onAnimationEnd is called and visibility is updated,
                // even if the coroutine is cancelled or an exception occurs.
                onAnimationEnd?.invoke()
                isVisible = false
            }
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
            onAnimationEnd?.invoke()
        }
    )
}

