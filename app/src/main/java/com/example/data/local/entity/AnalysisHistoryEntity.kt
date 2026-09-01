package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_history")
data class AnalysisHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val totalSentences: Int,
    val totalWords: Int,
    val meanWordsPerSentence: Double,
    val variance: Double,
    val sampleVariance: Double,
    val standardDeviation: Double,
    val timestamp: Long = System.currentTimeMillis()
)
