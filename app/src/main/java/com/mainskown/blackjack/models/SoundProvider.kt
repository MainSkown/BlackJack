package com.mainskown.blackjack.models

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.util.Log
import com.mainskown.blackjack.R

enum class SoundType {
    CARD_DRAW,
    BUTTON_CLICK,
    GAME_WON,
    GAME_LOST,
}

class SoundProvider {
    // MediaPlayer instances for different sounds
    private val cardDrawSoundPlayer: MediaPlayer
    private val buttonClickSoundPlayer: MediaPlayer
    private val gameWonSoundPlayer: MediaPlayer
    private val gameLostSoundPlayer: MediaPlayer

    private val settingsPreferences: SettingsPreferences
    private val musicPlayer: MediaPlayer
    private var musicVolume: Float

    constructor(context: Context, sharedPreferences: SharedPreferences) {
        settingsPreferences = SettingsPreferences(sharedPreferences)
        musicVolume = settingsPreferences.musicVolume

        //  Initialize sound resources here
        cardDrawSoundPlayer = MediaPlayer.create(
            context,
            R.raw.card_draw
        )

        musicPlayer = MediaPlayer.create(
            context,
            R.raw.music
        )

        buttonClickSoundPlayer = MediaPlayer.create(
            context,
            R.raw.button_click
        )

        gameWonSoundPlayer = MediaPlayer.create(
            context,
            R.raw.win
        )

        gameLostSoundPlayer = MediaPlayer.create(
            context,
            R.raw.lost
        )
    }

    companion object {
        @Volatile
        private lateinit var instance: SoundProvider

        fun init(context: Context, sharedPreferences: SharedPreferences) {
            if (!::instance.isInitialized) {
                instance = SoundProvider(context, sharedPreferences)
            }
        }

        fun initiated(): Boolean {
            return ::instance.isInitialized
        }

        fun playSound(soundType: SoundType) {
            if (!::instance.isInitialized) {
                throw IllegalStateException("SoundProvider not initialized. Call init() first.")
            }
            val volume = instance.settingsPreferences.soundVolume
            when (soundType) {
                SoundType.CARD_DRAW -> {
                    // Play the card draw sound
                    instance.cardDrawSoundPlayer.setVolume(volume, volume)
                    instance.cardDrawSoundPlayer.start()
                }

                SoundType.BUTTON_CLICK -> {
                    // Play the button click sound
                    instance.buttonClickSoundPlayer.setVolume(volume, volume)
                    instance.buttonClickSoundPlayer.start()
                }

                SoundType.GAME_WON -> {
                    // Play the game won sound
                    instance.gameWonSoundPlayer.setVolume(volume, volume)
                    instance.gameWonSoundPlayer.start()
                }

                SoundType.GAME_LOST -> {
                    // Play the game lost sound
                    instance.gameLostSoundPlayer.setVolume(volume, volume)
                    instance.gameLostSoundPlayer.start()
                }
            }
        }

        fun startPlayingMusic() {
            if (!::instance.isInitialized) {
                throw IllegalStateException("SoundProvider not initialized. Call init() first.")
            }
            if (instance.musicVolume == 0f) {
                if (instance.musicPlayer.isPlaying) {
                    instance.musicPlayer.pause()  // Use pause instead of stop
                }
                return
            }
            else if (!instance.musicPlayer.isPlaying) {
                try {
                    instance.musicPlayer.isLooping = true
                    val volume = instance.musicVolume
                    instance.musicPlayer.setVolume(volume, volume)
                    if (instance.musicPlayer.currentPosition > 0) {
                        instance.musicPlayer.prepare()
                    }
                    instance.musicPlayer.start()
                } catch (e: Exception) {
                    // Handle potential errors
                    e.printStackTrace()
                }
            }
        }

        fun updateMusicVolume(volume: Float) {
            if (!::instance.isInitialized) {
                throw IllegalStateException("SoundProvider not initialized. Call init() first.")
            }
            instance.musicPlayer.setVolume(volume, volume)
            instance.musicVolume = volume

            if (volume == 0f) {
                if (instance.musicPlayer.isPlaying) {
                    instance.musicPlayer.pause()
                }
            } else if (!instance.musicPlayer.isPlaying) {
                try {
                    instance.musicPlayer.start()
                } catch (e: Exception) {
                    // If it fails, try preparing first
                    try {
                        instance.musicPlayer.prepare()
                        instance.musicPlayer.start()
                    } catch (e: Exception) {
                        // Handle any errors
                        e.printStackTrace()
                    }
                }
            }
        }

        fun pauseMusic() {
            if (!::instance.isInitialized) {
                throw IllegalStateException("SoundProvider not initialized. Call init() first.")
            }
            if (instance.musicPlayer.isPlaying) {
                instance.musicPlayer.pause()
            }
        }

        fun resumeMusic() {
            if (!::instance.isInitialized) {
                throw IllegalStateException("SoundProvider not initialized. Call init() first.")
            }
            if (!instance.musicPlayer.isPlaying && instance.settingsPreferences.musicVolume > 0f) {
                instance.musicPlayer.start()
            }
        }
    }
}