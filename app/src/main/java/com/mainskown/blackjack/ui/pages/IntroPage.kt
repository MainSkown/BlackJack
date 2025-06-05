package com.mainskown.blackjack.ui.pages

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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.mainskown.blackjack.R
import com.mainskown.blackjack.models.IntroPageViewModel
import com.mainskown.blackjack.ui.components.OutlinedText
import kotlinx.coroutines.delay

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

