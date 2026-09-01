package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.dao.AnalysisHistoryDao
import com.example.data.local.entity.AnalysisHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AnalysisHistoryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.analysisHistoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadAnalysisHistory() = runBlocking {
        val entry = AnalysisHistoryEntity(
            text = "Kotlin is concise. Compose is modern.",
            totalSentences = 2,
            totalWords = 6,
            meanWordsPerSentence = 3.0,
            variance = 0.0,
            sampleVariance = 0.0,
            standardDeviation = 0.0
        )
        val id = dao.insertHistory(entry)
        assertTrue(id > 0)

        val historyList = dao.getAllHistory().first()
        assertEquals(1, historyList.size)
        assertEquals("Kotlin is concise. Compose is modern.", historyList[0].text)
        assertEquals(2, historyList[0].totalSentences)
        assertEquals(6, historyList[0].totalWords)
    }

    @Test
    fun deleteAndClearHistory() = runBlocking {
        val entry1 = AnalysisHistoryEntity(text = "First text.", totalSentences = 1, totalWords = 2, meanWordsPerSentence = 2.0, variance = 0.0, sampleVariance = 0.0, standardDeviation = 0.0)
        val entry2 = AnalysisHistoryEntity(text = "Second text.", totalSentences = 1, totalWords = 2, meanWordsPerSentence = 2.0, variance = 0.0, sampleVariance = 0.0, standardDeviation = 0.0)
        
        val id1 = dao.insertHistory(entry1)
        val id2 = dao.insertHistory(entry2)

        dao.deleteHistoryById(id1)
        var list = dao.getAllHistory().first()
        assertEquals(1, list.size)
        assertEquals(id2, list[0].id)

        dao.clearAllHistory()
        list = dao.getAllHistory().first()
        assertTrue(list.isEmpty())
    }
}
