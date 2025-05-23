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
import com.mainskown.blackjack.components.GameResult

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

@Database(entities = [GameData::class], version = 1)
@TypeConverters(GameResultConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
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

        fun getInstance(context: Context): DatabaseProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = DatabaseProvider(context)
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return getInstance(context).database
        }
    }
}
