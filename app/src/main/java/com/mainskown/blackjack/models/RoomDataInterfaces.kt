package com.mainskown.blackjack.models

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.mainskown.blackjack.ui.components.GameResult

@Entity
data class GameData(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "chips_value") val chipsValue: Int,
    @ColumnInfo(name = "bet_value") val betValue: Int,
    @ColumnInfo(name = "date") val date: String,
    // If result is null, the game is still in progress
    @ColumnInfo(name = "result") val result: GameResult?,
    @ColumnInfo(name = "deck_seed") val deckSeed: Long? = null,
)

@Dao
interface GameDao {
    // Insert a new game
    @Insert
    suspend fun insertGame(gameData: GameData): Long

    // Get all games
    @Query("SELECT * FROM gameData ORDER BY date DESC")
    suspend fun getAllGames(): List<GameData>

    // Get a game by its ID
    @Query("SELECT * FROM gameData WHERE uid = :uid")
    suspend fun getGameById(uid: Long): GameData?

    // Get the last game
    @Query("SELECT * FROM gameData WHERE result IS NULL ORDER BY uid DESC LIMIT 1")
    suspend fun getLastGame(): GameData?

    // Update a game
    @Update
    suspend fun updateGame(vararg gameData: GameData)

    // Update seed of a game
    @Query("UPDATE gameData SET deck_seed = :seed WHERE uid = :uid")
    suspend fun updateGameSeed(uid: Long, seed: Long)

    // Update the result of a game
    @Query("UPDATE gameData SET result = :result WHERE uid = :uid")
    suspend fun updateGameResult(uid: Long, result: GameResult)

    // Delete a game
    @Delete
    suspend fun deleteGame(gameData: GameData)
}

class GameResultConverter {
    @TypeConverter
    fun fromGameResult(value: GameResult?): String? {
        return value?.name
    }

    @TypeConverter
    fun toGameResult(value: String?): GameResult? {
        return value?.let { GameResult.valueOf(it) }
    }
}

// There should be only one entry of high scores
@Entity
data class HighScores(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "best_chips_value") val chipsValue: Int = 0, // Highest chips achieved
    @ColumnInfo(name = "best_bet_value") val betValue: Int = 0,     // Highest bet made
    @ColumnInfo(name = "best_streak") val streak: Int = 0,
)

@Dao
interface HighScoresDao {
    // Insert a new high score
    @Insert
    suspend fun insertHighScore(highScores: HighScores): Long

    // Update best chips value
    @Query("UPDATE highScores SET best_chips_value = :chipsValue WHERE uid = 1")
    suspend fun updateBestChipsValue(chipsValue: Int)

    // Update best bet value
    @Query("UPDATE highScores SET best_bet_value = :betValue WHERE uid = 1")
    suspend fun updateBestBetValue(betValue: Int)

    // Update best streak
    @Query("UPDATE highScores SET best_streak = :streak WHERE uid = 1")
    suspend fun updateBestStreak(streak: Int)

    // Get the high scores
    @Query("SELECT * FROM highScores WHERE uid = 1")
    suspend fun getHighScores(): HighScores?
}

@Database(entities = [GameData::class, HighScores::class], version = 1)
@TypeConverters(GameResultConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun highScoresDao(): HighScoresDao
}

// Static singleton instance of the database
class DatabaseProvider private constructor(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "game_database"
    ).build()  // Call build() to create the database

    companion object {
        @Volatile
        private var INSTANCE: DatabaseProvider? = null

        fun getInstance(context: Context?): DatabaseProvider {
            return INSTANCE ?: synchronized(this) {
                if(context == null) {
                    throw IllegalArgumentException("Can not initialize DatabaseProvider with null context")
                }
                val instance = DatabaseProvider(context)
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context?): AppDatabase {
            return getInstance(context).database
        }

        suspend fun updateHighScores(context: Context?){
            val highScoresDao = getDatabase(context).highScoresDao()
            // Check if high scores exist, if not, create a new one
            var highScore = highScoresDao.getHighScores()
            if (highScore == null) {
                highScoresDao.insertHighScore(HighScores())
                highScore = highScoresDao.getHighScores()
            }

            val gameDao = getDatabase(context).gameDao()
            val gameDataList = gameDao.getAllGames()

            // Update best chips value
            val bestChipsValue = gameDataList.maxOfOrNull { it.chipsValue } ?: 0
            if(highScore != null) {
                if (bestChipsValue > highScore.chipsValue) {
                    highScoresDao.updateBestChipsValue(bestChipsValue)
                }

                // Update best bet value
                val bestBetValue = gameDataList.maxOfOrNull { it.betValue } ?: 0
                if (bestBetValue > highScore.betValue) {
                    highScoresDao.updateBestBetValue(bestBetValue)
                }

                // Update best streak
                // Count the longest winning streak
                val longestStreak = gameDataList.fold(0 to 0) { (current, longest), game ->
                    if (game.result == GameResult.WIN) (current + 1) to maxOf(longest, current + 1)
                    else 0 to longest
                }.second
                if (longestStreak > highScore.streak) {
                    highScoresDao.updateBestStreak(longestStreak)
                }
            }
        }
    }
}
