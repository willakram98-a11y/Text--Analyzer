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

    @Test
    fun testTitleAbbreviationIsNotASentence() {
        val result = TextAnalyzer.analyze("Mr. Smith went to Washington. He liked it.")

        assertEquals(2, result.totalSentences)
        assertEquals("Mr. Smith went to Washington.", result.sentences[0].text)
        assertEquals(5, result.sentences[0].wordCount)
        assertEquals(3, result.sentences[1].wordCount)
    }

    @Test
    fun testInitialsAndAcronymsAreNotSentenceBreaks() {
        val result = TextAnalyzer.analyze("Dr. Jane F. Kennedy visited the U.S. last year. She loved it.")

        assertEquals(2, result.totalSentences)
        // Dr. / Jane / F. / Kennedy / visited / the / U.S. / last / year
        assertEquals(9, result.sentences[0].wordCount)
        assertEquals(3, result.sentences[1].wordCount)
    }

    @Test
    fun testSentenceEndingWithAbbreviationIsStillCounted() {
        val result = TextAnalyzer.analyze("Costs rose approx. 5.5 percent in Jan.")

        assertEquals(1, result.totalSentences)
        // Costs / rose / approx. / 5.5 / percent / in / Jan.
        assertEquals(7, result.totalWords)
    }

    @Test
    fun testDecimalNumberCountsAsOneWord() {
        val result = TextAnalyzer.analyze("The value is 5.0 today.")

        assertEquals(1, result.totalSentences)
        // The / value / is / 5.0 / today
        assertEquals(5, result.totalWords)
        assertTrue(result.sentences[0].words.contains("5.0"))
    }

    @Test
    fun testGroupedAndVersionNumbersCountAsOneWord() {
        val result = TextAnalyzer.analyze("Revenue reached 3,500.25 dollars. Version 1.2.3 shipped.")

        assertEquals(2, result.totalSentences)
        assertEquals(4, result.sentences[0].wordCount)
        assertEquals(3, result.sentences[1].wordCount)
        assertTrue(result.sentences[0].words.contains("3,500.25"))
        assertTrue(result.sentences[1].words.contains("1.2.3"))
    }

    @Test
    fun testSingleLetterWordStillEndsASentence() {
        // "A" and "I" are ordinary words, not initials.
        val result = TextAnalyzer.analyze("I got a Grade A. Then I celebrated.")

        assertEquals(2, result.totalSentences)
        assertEquals(5, result.sentences[0].wordCount)
        assertEquals(3, result.sentences[1].wordCount)
    }
}
