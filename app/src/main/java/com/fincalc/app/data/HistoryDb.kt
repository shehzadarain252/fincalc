package com.fincalc.app.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/** One saved calculation, shown on the History page. */
@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calcId: String,
    val calcTitle: String,
    val summary: String,
    val inputsJson: String,
    val timestamp: Long
)

/** A favourited calculator (by calculator id). */
@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val calcId: String
)

@Dao
interface FinCalcDao {
    @Insert
    suspend fun insertHistory(item: HistoryItem)

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun history(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT :limit")
    fun recentHistory(limit: Int): Flow<List<HistoryItem>>

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistory(id: Long)

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(fav: Favorite)

    @Query("DELETE FROM favorites WHERE calcId = :calcId")
    suspend fun removeFavorite(calcId: String)

    @Query("SELECT calcId FROM favorites")
    fun favorites(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM favorites WHERE calcId = :calcId")
    suspend fun isFavorite(calcId: String): Int
}

@Database(entities = [HistoryItem::class, Favorite::class], version = 1, exportSchema = false)
abstract class FinCalcDatabase : RoomDatabase() {
    abstract fun dao(): FinCalcDao

    companion object {
        @Volatile private var INSTANCE: FinCalcDatabase? = null

        fun get(context: Context): FinCalcDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FinCalcDatabase::class.java,
                    "fincalc.db"
                ).build().also { INSTANCE = it }
            }
    }
}
