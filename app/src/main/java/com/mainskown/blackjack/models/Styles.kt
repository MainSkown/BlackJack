package com.mainskown.blackjack.models

enum class CardStyle {
    CLASSIC {
        override fun toString(): String {
            return "Classic";
        }
    },
    MODERN {
        override fun toString(): String {
            return "Modern";
        }
    },
}

enum class BackgroundStyle {
    RED,
    BLUE,
    GREEN,
    CAT,
    ACE_CAT
}