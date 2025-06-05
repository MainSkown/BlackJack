package com.mainskown.blackjack.models

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.mainskown.blackjack.R

enum class SoundType {
    CARD_DRAW,
}

class SoundProvider {
    private val cardDrawSoundPlayer: MediaPlayer
    private val musicPlayer: MediaPlayer
    private val settingsPreferences: SettingsPreferences

    constructor(context: Context, sharedPreferences: SharedPreferences) {
        settingsPreferences = SettingsPreferences(sharedPreferences)

        //  Initialize sound resources here
        cardDrawSoundPlayer = MediaPlayer.create(
            context,
            R.raw.card_draw
        )

        musicPlayer = MediaPlayer.create(
            context,
            R.raw.music
        )
    }

    companion object {
        @Volatile
        private lateinit var instance: SoundProvider

        fun init(context: Context, sharedPreferences: SharedPreferences) {
            synchronized(this) {
                instance = SoundProvider(context, sharedPreferences)
            }
        }

        fun playSound(soundType: SoundType) {
            when (soundType) {
                SoundType.CARD_DRAW -> {
                    // Play the card draw sound
                    val volume = instance.settingsPreferences.soundVolume
                    instance.cardDrawSoundPlayer.setVolume(volume, volume)
                    instance.cardDrawSoundPlayer.start()
                }
            }
        }
    }
}