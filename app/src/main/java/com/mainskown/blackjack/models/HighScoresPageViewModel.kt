package com.mainskown.blackjack.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HighScoresPageUiState(
    val highScores: HighScores = HighScores(),
    val gameDataList: List<GameData> = emptyList()
)

class HighScoresPageViewModel(highScoresDao: HighScoresDao, gameDao: GameDao) : ViewModel() {
    private val _uiState = MutableStateFlow(HighScoresPageUiState())
    val uiState: StateFlow<HighScoresPageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadHighScores(highScoresDao, gameDao)
        }
    }

    private suspend fun loadHighScores(highScoresDao: HighScoresDao, gameDao: GameDao) {
        val highScores = highScoresDao.getHighScores() ?: HighScores()
        val gameDataList = gameDao.getAllGames()
        _uiState.value = HighScoresPageUiState(highScores, gameDataList)
    }

    companion object {
        fun createFactory(highScoresDao: HighScoresDao, gameDao: GameDao): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    HighScoresPageViewModel(highScoresDao, gameDao)
                }
            }
        }
    }
}