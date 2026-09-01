package com.example

import com.example.analyzer.TextAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBasicTextAnalysis() {
        val text = "Kotlin is great. Android apps are fast."
        val result = TextAnalyzer.analyze(text)

        assertEquals(2, result.totalSentences)
        assertEquals(3, result.sentences[0].wordCount) // "Kotlin is great" -> 3
        assertEquals(4, result.sentences[1].wordCount) // "Android apps are fast" -> 4
        assertEquals(7, result.totalWords)

        // Mean = 7 / 2 = 3.5
        assertEquals(3.5, result.meanWordsPerSentence, 0.001)

        // Population Variance: ((3 - 3.5)^2 + (4 - 3.5)^2) / 2 = (0.25 + 0.25) / 2 = 0.25
        assertEquals(0.25, result.variance, 0.001)

        // Sample Variance: ((3 - 3.5)^2 + (4 - 3.5)^2) / (2 - 1) = 0.50
        assertEquals(0.50, result.sampleVariance, 0.001)
    }

    @Test
    fun testZeroVariance() {
        val text = "One two three. Four five six. Seven eight nine."
        val result = TextAnalyzer.analyze(text)

        assertEquals(3, result.totalSentences)
        assertEquals(3, result.sentences[0].wordCount)
        assertEquals(3, result.sentences[1].wordCount)
        assertEquals(3, result.sentences[2].wordCount)
        assertEquals(9, result.totalWords)
        assertEquals(3.0, result.meanWordsPerSentence, 0.001)
        assertEquals(0.0, result.variance, 0.001)
        assertEquals(0.0, result.sampleVariance, 0.001)
    }

    @Test
    fun testContractionsAndPunctuation() {
        val text = "It's a wonderful, state-of-the-art day! Isn't it wonderful?"
        val result = TextAnalyzer.analyze(text)

        assertEquals(2, result.totalSentences)
        assertTrue(result.totalWords > 0)
        assertNotNull(result.sentences[0])
    }

    @Test
    fun testEmptyText() {
        val result = TextAnalyzer.analyze("   ")
        assertEquals(0, result.totalSentences)
        assertEquals(0, result.totalWords)
        assertEquals(0.0, result.variance, 0.001)
    }
}
