package com.mainskown.blackjack.ui.theme

import android.content.Context
import android.os.Build
import android.util.Log
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.BackgroundStyle
import com.mainskown.blackjack.pages.StylesPreferences

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun BlackJackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            BackgroundWrapper(content = content)
        }
    )
}

@Composable
fun BackgroundWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current

    // Load the background image directly
    val preferencesKey = context.getString(R.string.preferences_style_key)
    val sharedPrefs = context.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE)

    // Use a remember with a unique key for each style to force recomposition when style changes
    val currentStyle = remember(Unit) {
        androidx.compose.runtime.mutableStateOf(
            sharedPrefs.getString("background_style", BackgroundStyle.entries.first().name) ?: BackgroundStyle.entries.first().name
        )
    }

    // Add a PreferenceChangeListener to detect changes when coming back to this screen
    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "background_style") {
                val newStyle = sharedPrefs.getString(key, BackgroundStyle.entries.first().name)
                    ?: BackgroundStyle.entries.first().name
                currentStyle.value = newStyle
            }
        }

        // Register the listener
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        // Clean up when leaving the composition
        onDispose {
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    // Parse the background style from the saved preference
    val backgroundStyle = BackgroundStyle.valueOf(currentStyle.value)
    Log.d("BlackJackTheme", "Using background style: ${backgroundStyle.name}")

    // Load the background bitmap
    val backgroundBitmap = loadBackgroundBitmap(context, backgroundStyle.name.lowercase())

    Box(modifier = Modifier.fillMaxSize()) {
        // Display the background image if loaded successfully
        backgroundBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Render the content on top of the background
        content()
    }
}

private fun loadBackgroundBitmap(context: Context, styleName: String): android.graphics.Bitmap? {
    val assetPath = "backgrounds/$styleName.png"
    Log.d("BlackJackTheme", "Attempting to load: $assetPath")

    return try {
        context.assets.open(assetPath).use { inputStream ->
            android.graphics.BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        Log.e("BlackJackTheme", "Failed to load background: $assetPath", e)
        try {
            // Try fallback
            context.assets.open("backgrounds/red.png").use { inputStream ->
                android.graphics.BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e("BlackJackTheme", "Failed to load fallback background", e)
            null
        }
    }
}

