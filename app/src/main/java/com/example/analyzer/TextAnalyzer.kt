package com.example.analyzer

import com.example.model.AnalysisResult
import com.example.model.SentenceInfo
import java.text.BreakIterator
import java.util.Locale
import kotlin.math.sqrt

object TextAnalyzer {

    // Regex to match English words including contractions (e.g., "don't", "it's", "well-known") and alphanumeric tokens
    private val wordRegex = Regex("[a-zA-Z0-9]+(?:['’\\-][a-zA-Z0-9]+)*")

    /**
     * Analyzes English text to extract sentences, count words per sentence,
     * calculate total words, and compute sentence word-length variance.
     */
    fun analyze(input: String): AnalysisResult {
        val cleanInput = input.trim()
        if (cleanInput.isEmpty()) {
            return AnalysisResult.empty()
        }

        // Segment text into sentences using BreakIterator configured for English
        val rawSentences = splitIntoSentences(cleanInput)
        if (rawSentences.isEmpty()) {
            return AnalysisResult.empty()
        }

        // Extract words and compute word count for each sentence
        val initialSentenceInfos = rawSentences.mapIndexed { index, sentenceText ->
            val words = extractWords(sentenceText)
            SentenceInfo(
                index = index + 1,
                text = sentenceText,
                words = words,
                wordCount = words.size,
                charCount = sentenceText.length
            )
        }

        val totalSentences = initialSentenceInfos.size
        val totalWords = initialSentenceInfos.sumOf { it.wordCount }
        val meanWords = if (totalSentences > 0) totalWords.toDouble() / totalSentences else 0.0

        // Calculate sum of squared differences from the mean
        var sumSquaredDiff = 0.0
        val enrichedSentences = initialSentenceInfos.map { sentence ->
            val diff = sentence.wordCount - meanWords
            val sqDiff = diff * diff
            sumSquaredDiff += sqDiff
            sentence.copy(
                deviationFromMean = diff,
                squaredDeviation = sqDiff
            )
        }

        val populationVariance = if (totalSentences > 0) sumSquaredDiff / totalSentences else 0.0
        val sampleVariance = if (totalSentences > 1) sumSquaredDiff / (totalSentences - 1) else 0.0
        val populationStdDev = sqrt(populationVariance)
        val sampleStdDev = sqrt(sampleVariance)

        val minSentence = enrichedSentences.minByOrNull { it.wordCount }
        val maxSentence = enrichedSentences.maxByOrNull { it.wordCount }

        return AnalysisResult(
            originalText = cleanInput,
            sentences = enrichedSentences,
            totalSentences = totalSentences,
            totalWords = totalWords,
            meanWordsPerSentence = meanWords,
            variance = populationVariance,
            sampleVariance = sampleVariance,
            standardDeviation = populationStdDev,
            sampleStandardDeviation = sampleStdDev,
            minWordsSentence = minSentence,
            maxWordsSentence = maxSentence,
            totalCharacters = cleanInput.length,
            totalCharactersNoSpaces = cleanInput.count { !it.isWhitespace() },
            isCalculated = true
        )
    }

    /**
     * Splits text into individual sentences using standard English boundary rules.
     */
    private fun splitIntoSentences(text: String): List<String> {
        val result = mutableListOf<String>()
        val iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH)
        iterator.setText(text)

        var start = iterator.first()
        var end = iterator.next()

        while (end != BreakIterator.DONE) {
            val candidate = text.substring(start, end).trim()
            if (candidate.isNotEmpty()) {
                // If the candidate contains hard newlines separating paragraphs/distinct lines,
                // also consider newline boundaries
                val subSegments = candidate.split(Regex("\n{2,}"))
                for (sub in subSegments) {
                    val trimmed = sub.trim()
                    if (trimmed.isNotEmpty()) {
                        result.add(trimmed)
                    }
                }
            }
            start = end
            end = iterator.next()
        }

        return if (result.isEmpty() && text.isNotBlank()) {
            listOf(text.trim())
        } else {
            result
        }
    }

    /**
     * Extracts individual words from a sentence string.
     */
    fun extractWords(sentence: String): List<String> {
        return wordRegex.findAll(sentence).map { it.value }.toList()
    }
}
