package com.example.data.repository

import com.example.data.local.dao.AnalysisHistoryDao
import com.example.data.local.entity.AnalysisHistoryEntity
import com.example.model.AnalysisResult
import kotlinx.coroutines.flow.Flow

class AnalysisHistoryRepository(
    private val dao: AnalysisHistoryDao
) {
    val allHistory: Flow<List<AnalysisHistoryEntity>> = dao.getAllHistory()

    suspend fun saveAnalysis(text: String, result: AnalysisResult): Long {
        if (text.isBlank() || !result.isCalculated || result.totalSentences == 0) {
            return -1L
        }

        val entity = AnalysisHistoryEntity(
            text = text.trim(),
            totalSentences = result.totalSentences,
            totalWords = result.totalWords,
            meanWordsPerSentence = result.meanWordsPerSentence,
            variance = result.variance,
            sampleVariance = result.sampleVariance,
            standardDeviation = result.standardDeviation,
            timestamp = System.currentTimeMillis()
        )
        return dao.insertHistory(entity)
    }

    suspend fun deleteHistory(id: Long) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }
}
