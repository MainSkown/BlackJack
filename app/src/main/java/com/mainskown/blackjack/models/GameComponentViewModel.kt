package com.mainskown.blackjack.models

import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameComponentUiState(
    var dealerHand: MutableList<Card> = mutableStateListOf(),
    var playerHand: MutableList<Card> = mutableStateListOf(),
    var chips: Int = 0,
    var bet: Int = 0,
    var gameStarted: Boolean = false,
    var playerFinished: Boolean = false,
    var gameEnded: Boolean = false,
    var gameResult: GameResult = GameResult.LOSE,
    var showResultDialog: Boolean = false,

    var cardStyle: CardStyle = CardStyle.entries.first(),

    // Animations
    var dealersKey: Int = 0,
    var playersKey: Int = 0,
    var inAnimation: Boolean = false,
    var animationFaceDown: Boolean = false
)

class GameComponentViewModel(
    private val chips: Int,
    private val bet: Int,
    private val gameID: Long,
    private val sharedPreferences: SharedPreferences,
    val assetManager: AssetManager,
    private val gameDao: GameDao,
    val onGameEnd: (GameResult) -> Unit
) : ViewModel() {
    private val deck: Deck by lazy {
        Deck(assetManager, StylesPreferences(sharedPreferences).cardStyle)
    }
    private val _uiState = MutableStateFlow(GameComponentUiState())
    val uiState: StateFlow<GameComponentUiState> = _uiState.asStateFlow()

    private val stylesPreferences = StylesPreferences(sharedPreferences)

    init {
        // Initialize the game state
        _uiState.value = GameComponentUiState(
            chips = chips,
            bet = bet,
            cardStyle = stylesPreferences.cardStyle
        )
    }

    // Helper method to update state and trigger recomposition
    private fun updateState(update: GameComponentUiState.() -> Unit) {
        val newState = _uiState.value.copy()
        update(newState)
        _uiState.value = newState
    }

    fun addCardToDealerHand(card: Card) {
        Log.d("GameComponentViewModel", "Adding card to dealer hand: $card")
        updateState { dealerHand.add(card) }
    }
    fun addCardToPlayerHand(card: Card) {
        updateState { playerHand.add(card) }
    }

    suspend fun startGame() {
        // Shuffle the deck
        val gameData = gameDao.getGameById(gameID)

        gameDao.updateGameSeed(gameID, deck.shuffle(gameData?.deckSeed))

        for (i in 0 until 4) {
            if (i % 2 == 0) {
                if (i == 0) {
                    // First card is faceDown
                    updateState { animationFaceDown = true }
                }
                updateState { dealersKey++ }
            } else {
                updateState { playersKey++ }
            }

            updateState { inAnimation = true }

            do {
                delay(100)
            } while (_uiState.value.inAnimation)
        }

        // If dealer has blackjack, end the game
        if (calcValue(_uiState.value.dealerHand.toTypedArray(), ignoreFaceDown = true) == 21) {
            updateState { playerFinished = true }
            delay(600)
        }
        // Same for the player
        if (calcValue(_uiState.value.playerHand.toTypedArray()) == 21 && !_uiState.value.playerFinished) {
            updateState { playerFinished = true }
            delay(600)
        }
        updateState { gameStarted = true }
    }

    suspend fun calculatePlayerValue() {
        if (_uiState.value.playerHand.isNotEmpty()) {
            val playerValue = calcValue(_uiState.value.playerHand.toTypedArray())
            if (playerValue >= 21) {
                if (playerValue > 21) {
                    updateState { gameEnded = true }
                    // Wait for a bit before ending the game for player to realise what happened
                    delay(600)
                }

                updateState { playerFinished = true }
            }
        }
    }

    suspend fun onPlayerFinished() {
        if (!_uiState.value.playerFinished) return
        // Dealer's turn: flip the first card by replacing it with a new instance
        if (_uiState.value.dealerHand.isNotEmpty() && !_uiState.value.dealerHand[0].isFaceUp) {
            val old = _uiState.value.dealerHand[0]
            updateState { dealerHand[0] = Card(
                assetManager,
                value = old.value,
                suit = old.suit,
                isFaceUp = true,
                style = old.style
            ) }

            // Wait for flip the animation to end
            delay(600)
        }

        val playersValue = calcValue(_uiState.value.playerHand.toTypedArray())

        // Dealer's turn: keep drawing cards until dealer's value is greater than or equal to player's value
        if (playersValue != 21 || _uiState.value.playerHand.size != 2)
            while (calcValue(_uiState.value.dealerHand.toTypedArray()) < playersValue) {
                updateState { dealersKey++ }
                updateState { inAnimation = true }

                // Wait for animation to end
                do {
                    delay(100)
                } while (_uiState.value.inAnimation)
            }

        // Wait for a bit before ending the game for player to realise what happened
        delay(600)
        updateState { gameEnded = true }
    }

    fun endGame() {
        if (!_uiState.value.gameEnded) return

        val dealersValue = calcValue(_uiState.value.dealerHand.toTypedArray())
        val playersValue = calcValue(_uiState.value.playerHand.toTypedArray())

        // Check for win/loss
        if (playersValue > 21 || (dealersValue <= 21 && playersValue < dealersValue)) {
            // Game lost if player value is greater than 21
            updateState {
                chips -= bet
                gameResult = GameResult.LOSE
            }
        } else if (playersValue > dealersValue || dealersValue > 21) {
            // Game won if player value is greater than dealer value or dealer exceeds 21
            updateState {
                chips += bet
                gameResult = GameResult.WIN
            }
        } else {
            // Game draw if player value is equal to dealer value
            updateState { gameResult = GameResult.DRAW }
        }

        updateState { showResultDialog = true }
    }

    fun gameEnded(){
        updateState { showResultDialog = false }
    }

    // Methods to update state from UI
    fun playerHit() {
        updateState {
            playersKey++
            inAnimation = true
        }
    }

    fun playerHold() {
        updateState { playerFinished = true }
    }

    fun setAnimationComplete() {
        updateState { inAnimation = false }
        updateState { animationFaceDown = false }
    }

    fun drawCard(faceUp: Boolean = true): Card {
        return deck.drawCard().apply {
            isFaceUp = faceUp
        }
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
        (0 until aces).forEach { i ->
            value += if (value + 11 > 21) {
                1
            } else {
                11
            }
        }

        return value
    }

    fun playerValue(): Int {
        return calcValue(uiState.value.playerHand.toTypedArray())
    }

    fun dealerValue(ignoreFaceDown: Boolean = true): Int {
        return calcValue(uiState.value.dealerHand.toTypedArray(), ignoreFaceDown)
    }

    companion object {
        fun createFactory(
            chips: Int,
            bet: Int,
            gameID: Long,
            gameDao: GameDao,
            sharedPreferences: SharedPreferences,
            assetManager: AssetManager,
            onGameEnd: (GameResult) -> Unit
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    GameComponentViewModel(
                        chips = chips,
                        bet = bet,
                        gameID = gameID, // Default value, can be adjusted as needed
                        sharedPreferences = sharedPreferences,
                        assetManager = assetManager,
                        gameDao = gameDao,
                        onGameEnd = onGameEnd
                    )
                }
            }
        }
    }
}
