package com.mainskown.blackjack.models

import android.content.SharedPreferences
import androidx.core.content.edit

class StylesPreferences(preferences: SharedPreferences) {
    private val sharedPreferences = preferences

    var cardStyle: CardStyle = CardStyle.entries.first()
        get() {
            val value = sharedPreferences.getString("card_style", field.name) ?: field.name
            return CardStyle.valueOf(value)
        }
        set(value) {
            sharedPreferences.edit { putString("card_style", value.name) }
            field = value
        }

    var backgroundStyle: BackgroundStyle = BackgroundStyle.entries.first()
        get() {
            val storedValue =
                sharedPreferences.getString("background_style", field.name) ?: field.name
            return BackgroundStyle.valueOf(storedValue)
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    "background_style",
                    value.name
                )
            } // Store the enum name (not toString)
            field = value
        }
}