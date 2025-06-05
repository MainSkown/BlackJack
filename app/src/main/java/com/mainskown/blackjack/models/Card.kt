package com.mainskown.blackjack.models

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import androidx.core.graphics.createBitmap

enum class CardSuit {
    HEARTS {
        override fun toString(): String {
            return "h";
        }
    },
    DIAMONDS {
        override fun toString(): String {
            return "d";
        }
    },
    CLUBS {
        override fun toString(): String {
            return "c";
        }
    },
    SPADES {
        override fun toString(): String {
            return "s";
        }
    }
}

class Card(
    assetManager: AssetManager,
    val value: Int,
    val suit: CardSuit,
    var isFaceUp: Boolean = true,
    val style: CardStyle = CardStyle.CLASSIC,
) {
    // Try loading file
    val frontImage: Bitmap = try {
        // Image template: "<suit><value>.png" where if value < 10, add a 0 before it
        val fileName = "${suit}${if (value < 10) "0" else ""}$value.png"
        val path = "cards/$style/$fileName"
        val inputStream = assetManager.open(path)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        createBitmap(1, 1) // Placeholder bitmap
    }

    // Try loading back image
    val backImage: Bitmap = try {
        val path = "cards/$style/back.png"
        val inputStream = assetManager.open(path)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        createBitmap(1, 1) // Placeholder bitmap
    }
}