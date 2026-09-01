package com.example.analyzer

import com.example.model.AnalysisResult
import com.example.model.SentenceInfo
import java.text.BreakIterator
import java.util.Locale
import kotlin.math.sqrt

object TextAnalyzer {

    // A word is one of, in priority order:
    //  1. a dotted acronym/abbreviation kept whole ("p.m.", "U.S.", "e.g.")
    //  2. a decimal or grouped number kept whole ("5.0", "3,500.25", "1.2.3")
    //  3. an alphanumeric token with inner apostrophes or hyphens ("don't", "state-of-the-art")
    private val wordRegex = Regex(
        "(?:[A-Za-z]\\.){2,}" +
            "|\\d+(?:[.,]\\d+)+" +
            "|[a-zA-Z0-9]+(?:['’\\-][a-zA-Z0-9]+)*"
    )

    // Common English abbreviations whose trailing period does NOT end a sentence.
    private val abbreviations = setOf(
        "mr", "mrs", "ms", "dr", "prof", "sr", "jr", "st", "mt", "rev", "hon", "gen", "col",
        "capt", "lt", "sgt", "gov", "sen", "rep", "pres", "supt", "messrs", "fr", "atty",
        "asst", "insp", "det", "ave", "blvd", "rd", "apt", "dept", "est", "fig", "figs",
        "vol", "vols", "no", "nos", "pp", "ed", "eds", "inc", "ltd", "co", "corp", "univ",
        "approx", "min", "max", "etc", "vs", "cf", "viz", "al", "ibid",
        "jan", "feb", "mar", "apr", "jun", "jul", "aug", "sep", "sept", "oct", "nov", "dec",
        "mon", "tue", "tues", "wed", "thu", "thur", "thurs", "fri", "sat", "sun"
    )

    // Trailing token before a final period, ignoring closing quotes/brackets.
    private val trailingWordRegex = Regex("([A-Za-z]+)\\.[\"'”’)\\]]*$")

    // A dotted acronym at the end of a segment, e.g. "U.S.", "p.m.", "e.g."
    private val trailingAcronymRegex = Regex("(?:[A-Za-z]\\.){2,}[\"'”’)\\]]*$")

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

        // BreakIterator breaks on every period, so a segment may stop mid-sentence at an
        // abbreviation ("Mr."). Such a segment is joined with the one that follows it.
        var pending: StringBuilder? = null

        fun addSegment(segment: String) {
            val current = pending
            val combined = if (current != null) {
                current.append(' ').append(segment)
                current
            } else {
                StringBuilder(segment)
            }
            if (endsWithAbbreviation(combined)) {
                pending = combined
            } else {
                result.add(combined.toString())
                pending = null
            }
        }

        while (end != BreakIterator.DONE) {
            val candidate = text.substring(start, end).trim()
            if (candidate.isNotEmpty()) {
                // If the candidate contains hard newlines separating paragraphs/distinct lines,
                // also consider newline boundaries
                val subSegments = candidate.split(Regex("\n{2,}"))
                for (sub in subSegments) {
                    val trimmed = sub.trim()
                    if (trimmed.isNotEmpty()) {
                        addSegment(trimmed)
                    }
                }
            }
            start = end
            end = iterator.next()
        }

        // A trailing abbreviation at the very end of the text still forms a sentence.
        pending?.let { result.add(it.toString()) }

        return if (result.isEmpty() && text.isNotBlank()) {
            listOf(text.trim())
        } else {
            result
        }
    }

    /**
     * Returns true when [segment] ends with an abbreviation rather than a real sentence end,
     * e.g. "Mr.", "approx.", "U.S." or the initial in "John F. Kennedy".
     */
    private fun endsWithAbbreviation(segment: CharSequence): Boolean {
        val trimmed = segment.trim().toString()
        if (trailingAcronymRegex.containsMatchIn(trimmed)) {
            return true
        }
        val lastWord = trailingWordRegex.find(trimmed)?.groupValues?.get(1) ?: return false
        if (lastWord.lowercase(Locale.ENGLISH) in abbreviations) {
            return true
        }
        // A single capital letter is an initial ("John F. Kennedy"),
        // except "A" and "I", which are ordinary English words.
        return lastWord.length == 1 &&
            lastWord[0].isUpperCase() &&
            lastWord != "A" &&
            lastWord != "I"
    }

    /**
     * Extracts individual words from a sentence string.
     */
    fun extractWords(sentence: String): List<String> {
        return wordRegex.findAll(sentence).map { it.value }.toList()
    }
}
