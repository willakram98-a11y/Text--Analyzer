package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AnalysisHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisHistoryDao {

    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<AnalysisHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: AnalysisHistoryEntity): Long

    @Query("DELETE FROM analysis_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM analysis_history")
    suspend fun clearAllHistory()
}
