package com.example.model

/**
 * Information regarding an individual sentence extracted from the text.
 */
data class SentenceInfo(
    val index: Int,
    val text: String,
    val words: List<String>,
    val wordCount: Int,
    val charCount: Int,
    val deviationFromMean: Double = 0.0,
    val squaredDeviation: Double = 0.0
)

/**
 * Complete statistical results of analyzing an English text.
 */
data class AnalysisResult(
    val originalText: String = "",
    val sentences: List<SentenceInfo> = emptyList(),
    val totalSentences: Int = 0,
    val totalWords: Int = 0,
    val meanWordsPerSentence: Double = 0.0,
    val variance: Double = 0.0, // Population Variance: σ² = Σ(w_i - μ)² / N
    val sampleVariance: Double = 0.0, // Sample Variance: s² = Σ(w_i - μ)² / (N - 1)
    val standardDeviation: Double = 0.0, // σ = sqrt(variance)
    val sampleStandardDeviation: Double = 0.0,
    val minWordsSentence: SentenceInfo? = null,
    val maxWordsSentence: SentenceInfo? = null,
    val totalCharacters: Int = 0,
    val totalCharactersNoSpaces: Int = 0,
    val isCalculated: Boolean = false
) {
    companion object {
        fun empty(): AnalysisResult = AnalysisResult()
    }
}

/**
 * Sample English text options for quick demonstration and testing.
 */
data class SampleText(
    val title: String,
    val description: String,
    val text: String
)
