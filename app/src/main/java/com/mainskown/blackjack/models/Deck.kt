package com.mainskown.blackjack.models

import android.content.Context

class Deck (context: Context, style: CardStyle) {
    private val cards: MutableList<Card> = mutableListOf()

    init {
        for (suit in CardSuit.entries) {
            for (value in 1..13) {
                cards.add(Card(context, value, suit, style = style))
            }
        }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun drawCard(): Card{
        return if (cards.isNotEmpty()) {
            cards.removeAt(cards.size - 1)
        } else {
            throw IllegalStateException("No more cards in the deck")
        }
    }

    fun amountOfCards(): Int {
        return cards.size
    }
}