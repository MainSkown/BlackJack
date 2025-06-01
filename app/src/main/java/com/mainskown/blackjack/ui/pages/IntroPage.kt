package com.mainskown.blackjack.ui.pages

import android.content.SharedPreferences
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.mainskown.blackjack.R
import com.mainskown.blackjack.ui.components.OutlinedText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun IntroPage(viewModel: IntroPageViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val isVideoFinished = uiState.isVideoFinished
    // Animate overlayAlpha and titleAlpha for smooth transitions
    val animatedOverlayAlpha by animateFloatAsState(targetValue = uiState.overlayAlpha, animationSpec = tween(1000))
    val animatedTitleAlpha by animateFloatAsState(targetValue = uiState.titleAlpha, animationSpec = tween(1000))

    // Handle the transition sequence
    LaunchedEffect(isVideoFinished) {
        if (isVideoFinished) {
            // Ensure full darkness for a moment
            delay(500)
            // Show the title
            viewModel.updateShowTitle(true)
            // Wait for the title to be shown fully
            delay(3000)
            // Navigate to MainActivity with fade-in transition animation
            navController.navigate("mainPage") {
                // Clear the back stack to prevent going back to the intro page
                popUpTo("introPage") { inclusive = true }
            }
        }
    }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Video Player
            VideoPlayer(
                videoPath = "asset:///intro/intro.mp4",
                onVideoComplete = {
                    viewModel.updateIsVideoFinished(true)
                },
                onPositionUpdate = { position, duration ->
                    viewModel.updateCurrentPosition(position)
                    viewModel.updateVideoDuration(duration)
                }
            )

            // Simple overlay for darkening effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(animatedOverlayAlpha)
                    .zIndex(1f)
                    .background(Color.Black)
            )

            // App Title that appears after fade to black - positioned to match MainActivity
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f)
                    .alpha(animatedTitleAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedText(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin.Center
                        }
                )
                // Empty space where cards would appear in MainActivity
                Box(modifier = Modifier.size(150.dp))
            }
        }
}

data class IntroPageUiState(
    val startIntro: Boolean = false,
    val isVideoFinished: Boolean = false,
    val showTitle: Boolean = false,
    val currentPosition: Float = 0f,
    val videoDuration: Float = 100f, // Default value
    val overlayAlpha: Float = 0f,
    val titleAlpha: Float = 0f
)

class IntroPageViewModel(private val sharedPreferences: SharedPreferences): ViewModel(){
    private val _uiState = MutableStateFlow(IntroPageUiState())
    val uiState: StateFlow<IntroPageUiState> = _uiState.asStateFlow()

    val settingsPreferences: SettingsPreferences by lazy {
        SettingsPreferences(this.sharedPreferences)
    }

    fun updateIsVideoFinished(finished: Boolean) {
        _uiState.value = _uiState.value.copy(isVideoFinished = finished)
        updateOverlayAlpha() // recalculate overlay alpha
    }

    fun updateShowTitle(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTitle = show)
        updateTitleAlpha() // recalculate title alpha
    }

    fun updateCurrentPosition(position: Float) {
        _uiState.value = _uiState.value.copy(currentPosition = position)
        updateOverlayAlpha() // recalculate overlay alpha
    }

    fun updateVideoDuration(duration: Float) {
        _uiState.value = _uiState.value.copy(videoDuration = duration)
        updateOverlayAlpha() // recalculate overlay alpha
    }

    private fun updateOverlayAlpha() {
        val state = _uiState.value
        val fadeStartRatio = 0.7f
        val fadeEndRatio = 0.9f
        val ratio = if (state.videoDuration > 0) state.currentPosition / state.videoDuration else 0f
        val overlayAlpha = when {
            state.isVideoFinished -> 1f
            ratio > fadeEndRatio -> 1f
            ratio > fadeStartRatio -> {
                val progress = (ratio - fadeStartRatio) / (fadeEndRatio - fadeStartRatio)
                progress.coerceIn(0f, 1f)
            }
            else -> 0f
        }
        _uiState.value = state.copy(overlayAlpha = overlayAlpha)
    }

    private fun updateTitleAlpha() {
        val state = _uiState.value
        val titleAlpha = if (state.showTitle) 1f else 0f
        _uiState.value = state.copy(titleAlpha = titleAlpha)
    }

    companion object {
        fun createFactory(sharedPreferences: SharedPreferences): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    IntroPageViewModel(sharedPreferences)
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoPath: String,
    onVideoComplete: () -> Unit,
    onPositionUpdate: (Float, Float) -> Unit
) {
    val context = LocalContext.current

    // Create an ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Set the media item to the player
            val mediaItem = MediaItem.fromUri(videoPath)
            setMediaItem(mediaItem)
            // Prepare the player
            prepare()
            // Start playback
            playWhenReady = true

            // Add a listener to detect when the video has ended
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        onVideoComplete()
                    }
                }
            })
        }
    }

    // Regularly update position using LaunchedEffect
    LaunchedEffect(exoPlayer) {
        while (true) {
            // Only update if player is ready and has valid duration
            if (exoPlayer.isPlaying && exoPlayer.duration > 0) {
                onPositionUpdate(
                    exoPlayer.currentPosition.toFloat(),
                    exoPlayer.duration.toFloat()
                )
            }
            delay(100) // Update every 100ms
        }
    }

    // Handle cleanup when the composable leaves the composition
    DisposableEffect(key1 = exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Render the player view
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Set resize mode to fill the screen completely (may crop parts of the video)
                useController = false  // Hide the player controls
                resizeMode =
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM  // Fill the screen by zooming/cropping
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

