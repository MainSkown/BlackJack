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
    RED {
        override fun toString(): String {
            return "red";
        }
    },
    BLUE {
        override fun toString(): String {
            return "blue";
        }
    },
    GREEN {
        override fun toString(): String {
            return "green";
        }
    },
    CAT {
        override fun toString(): String {
            return "cat";
        }
    },
    ACE_CAT {
        override fun toString(): String {
            return "ace_cat";
        }
    },
}