package com.mainskown.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mainskown.blackjack.models.DatabaseProvider
import com.mainskown.blackjack.models.GamePageViewModel
import com.mainskown.blackjack.models.HighScoresPageViewModel
import com.mainskown.blackjack.models.IntroPageViewModel
import com.mainskown.blackjack.models.SettingsPageViewModel
import com.mainskown.blackjack.models.SettingsPreferences
import com.mainskown.blackjack.models.SoundProvider
import com.mainskown.blackjack.models.SoundType
import com.mainskown.blackjack.models.StylesPageViewModel
import com.mainskown.blackjack.ui.theme.BlackJackTheme
import com.mainskown.blackjack.ui.pages.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackJackTheme {
                val navController = rememberNavController()
                // Observe the current back stack entry
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            // Conditionally apply padding
                            // Apply padding if the current route is NOT "introPage"
                            .then(
                                if (currentRoute != "introPage") {
                                    Modifier.padding(innerPadding)
                                } else {
                                    Modifier // No padding for introPage
                                }
                            )
                    ) {
                        NavigationHost(
                            navController = navController,
                            modifier = Modifier.fillMaxSize(),
                            context = this@MainActivity
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause the music when the activity is paused
        if(SoundProvider.initiated())
            SoundProvider.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        // Resume the music when the activity is resumed
        if(SoundProvider.initiated())
            SoundProvider.resumeMusic()
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier, context: Context) {
    // Check if skipIntro is enabled
    val sharedPreferences = context.getSharedPreferences(stringResource(R.string.app_name), Context.MODE_PRIVATE)
    val settingsPreferences = SettingsPreferences(sharedPreferences)
    // Database
    val database = DatabaseProvider.getDatabase(context)
    // AssetManager
    val assetManager = context.assets

    // Setup SoundProvider
    SoundProvider.init(context, sharedPreferences)

    NavHost(
        navController = navController,
        startDestination = if (settingsPreferences.skipIntro) "mainPage" else "introPage",
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(0.dp) // Ensure no padding is applied by NavHost itself
    ) {
        composable("mainPage") { MainPage(navController) }
        composable("gamePage") {
            val gamePageViewModel: GamePageViewModel = viewModel(factory = GamePageViewModel.createFactory(
                gameDao = database.gameDao(),
                sharedPreferences = sharedPreferences,
                assetManager = assetManager,
            ))
            GamePage(gamePageViewModel)
        }
        composable("highScoresPage") {
            val highScoresPageViewModel: HighScoresPageViewModel = viewModel(factory = HighScoresPageViewModel.createFactory(
                highScoresDao = database.highScoresDao(),
                gameDao = database.gameDao()
            ))
            HighScoresPage(highScoresPageViewModel)
        }
        composable("introPage") {
            val introPageViewModel: IntroPageViewModel = viewModel(factory = IntroPageViewModel.createFactory(
                sharedPreferences = sharedPreferences,
            ))
            IntroPage(introPageViewModel, navController)
        }
        composable("rulesPage") { RulesPage() }
        composable("settingsPage") {
            val settingsPageViewModel: SettingsPageViewModel = viewModel(factory = SettingsPageViewModel.createFactory(
                sharedPreferences = sharedPreferences,
            ))
            SettingsPage(settingsPageViewModel)
        }
        composable("stylesPage") {
            val stylesPageViewModel: StylesPageViewModel = viewModel(factory = StylesPageViewModel.createFactory(
                assetManager = assetManager,
                sharedPreferences = sharedPreferences,
            ))
            StylesPage(stylesPageViewModel)
        }
    }
}