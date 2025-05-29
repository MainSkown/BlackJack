package com.mainskown.blackjack.pages

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.app.ActivityOptionsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.mainskown.blackjack.R
import com.mainskown.blackjack.components.OutlinedText
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import kotlinx.coroutines.delay

class IntroPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Load settings
                    val settingsPreferences = SettingsPreferences(this.getSharedPreferences(getString(R.string.preferences_settings_key), MODE_PRIVATE))
                    var startIntro by remember { mutableStateOf(false) }
                    // States for controlling the transition animations
                    var isVideoFinished by remember { mutableStateOf(false) }
                    var showTitle by remember { mutableStateOf(false) }
                    var currentPosition by remember { mutableFloatStateOf(0f) }
                    var videoDuration by remember { mutableFloatStateOf(100f) } // Default value

                    // Calculate the point where darkening should start (40% of the video)
                    val fadeStartRatio = 0.7f
                    val fadeEndRatio = 0.9f // Fade ends at 90% of the video

                    // Simple fade calculation
                    val overlayAlpha by animateFloatAsState(
                        targetValue = when {
                            isVideoFinished -> 1f // Fully dark when video is finished
                            currentPosition / videoDuration > fadeEndRatio -> 1f // Fully dark after fadeEndRatio
                            currentPosition / videoDuration > fadeStartRatio -> {
                                // Linear fade between fadeStartRatio and fadeEndRatio
                                val progress = (currentPosition / videoDuration - fadeStartRatio) / (fadeEndRatio - fadeStartRatio)
                                progress.coerceIn(0f, 1f)
                            }
                            else -> 0f // No darkening for the first part of the video
                        },
                        animationSpec = tween(durationMillis = 300),
                        label = "overlayAlpha"
                    )

                    // Title animation - only fade in
                    val titleAlpha by animateFloatAsState(
                        targetValue = if (showTitle) 1f else 0f,
                        animationSpec = tween(durationMillis = 1000),
                        label = "titleAlpha"
                    )

                    LaunchedEffect(Unit) {
                        if(settingsPreferences.skipIntro) {
                            startActivity(Intent(this@IntroPage, MainActivity::class.java))
                            finish()
                        } else
                            startIntro = true
                    }

                    // Handle the transition sequence
                    LaunchedEffect(isVideoFinished) {
                        if (isVideoFinished) {
                            // Ensure full darkness for a moment
                            delay(500)
                            // Show the title
                            showTitle = true
                            // Wait for the title to be shown fully
                            delay(3000)
                            // Navigate to MainActivity with fade-in transition animation
                            val intent = Intent(this@IntroPage, MainActivity::class.java)

                            startActivity(intent)
                        }
                    }

                    // Main content
                    if(startIntro)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Video Player
                        VideoPlayer(
                            videoPath = "asset:///intro/intro.mp4",
                            onVideoComplete = {
                                isVideoFinished = true
                            },
                            onPositionUpdate = { position, duration ->
                                currentPosition = position
                                videoDuration = duration
                            }
                        )

                        // Simple overlay for darkening effect
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(overlayAlpha)
                                .zIndex(1f)
                                .background(Color.Black)
                        )

                        // App Title that appears after fade to black - positioned to match MainActivity
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(2f)
                                .alpha(titleAlpha),
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
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoPath: String, onVideoComplete: () -> Unit, onPositionUpdate: (Float, Float) -> Unit) {
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
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM  // Fill the screen by zooming/cropping
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

