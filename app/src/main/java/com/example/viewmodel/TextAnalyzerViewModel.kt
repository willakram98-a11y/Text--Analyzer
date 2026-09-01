package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.analyzer.TextAnalyzer
import com.example.model.AnalysisResult
import com.example.model.SampleText
import com.example.model.SentenceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextAnalyzerViewModel : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _analysisResult = MutableStateFlow(AnalysisResult.empty())
    val analysisResult: StateFlow<AnalysisResult> = _analysisResult.asStateFlow()

    // Flag for Population Variance vs Sample Variance
    private val _useSampleVariance = MutableStateFlow(false)
    val useSampleVariance: StateFlow<Boolean> = _useSampleVariance.asStateFlow()

    private val _selectedSentence = MutableStateFlow<SentenceInfo?>(null)
    val selectedSentence: StateFlow<SentenceInfo?> = _selectedSentence.asStateFlow()

    private val _showStepByStepDialog = MutableStateFlow(false)
    val showStepByStepDialog: StateFlow<Boolean> = _showStepByStepDialog.asStateFlow()

    private val _showSamplePicker = MutableStateFlow(false)
    val showSamplePicker: StateFlow<Boolean> = _showSamplePicker.asStateFlow()

    init {
        // Initialize with default sample text so user immediately sees how it works!
        val defaultSample = "Kotlin is a modern and concise programming language. Jetpack Compose simplifies building native Android user interfaces. Developers create elegant mobile applications faster with less boilerplate code."
        _inputText.value = defaultSample
        _analysisResult.value = TextAnalyzer.analyze(defaultSample)
    }

    fun onTextChanged(text: String) {
        _inputText.value = text
        // Live calculation as user types
        if (text.isBlank()) {
            _analysisResult.value = AnalysisResult.empty()
            _selectedSentence.value = null
        } else {
            _analysisResult.value = TextAnalyzer.analyze(text)
        }
    }

    fun analyzeNow() {
        _analysisResult.value = TextAnalyzer.analyze(_inputText.value)
    }

    fun clearText() {
        _inputText.value = ""
        _analysisResult.value = AnalysisResult.empty()
        _selectedSentence.value = null
    }

    fun loadSample(sample: SampleText) {
        _inputText.value = sample.text
        _analysisResult.value = TextAnalyzer.analyze(sample.text)
        _selectedSentence.value = null
        _showSamplePicker.value = false
    }

    fun setSamplePickerVisible(visible: Boolean) {
        _showSamplePicker.value = visible
    }

    fun setStepByStepDialogVisible(visible: Boolean) {
        _showStepByStepDialog.value = visible
    }

    fun toggleVarianceMode() {
        _useSampleVariance.value = !_useSampleVariance.value
    }

    fun selectSentence(sentence: SentenceInfo?) {
        _selectedSentence.value = if (_selectedSentence.value?.index == sentence?.index) null else sentence
    }

    fun generateFormattedReport(): String {
        val result = _analysisResult.value
        if (!result.isCalculated || result.totalSentences == 0) {
            return "No text analyzed."
        }

        val sb = StringBuilder()
        sb.appendLine("📊 English Text Analysis Report")
        sb.appendLine("====================================")
        sb.appendLine("• Number of sentences: ${result.totalSentences} sentences")
        sb.appendLine("• Total words: ${result.totalWords} words")
        sb.appendLine("• Average (Mean) words/sentence: ${String.format(Locale.US, "%.2f", result.meanWordsPerSentence)}")
        sb.appendLine("• Population Variance (σ²): ${String.format(Locale.US, "%.4f", result.variance)}")
        sb.appendLine("• Sample Variance (s²): ${String.format(Locale.US, "%.4f", result.sampleVariance)}")
        sb.appendLine("• Standard Deviation (σ): ${String.format(Locale.US, "%.4f", result.standardDeviation)}")
        sb.appendLine()
        sb.appendLine("📝 Number of Words Per Sentence:")
        sb.appendLine("------------------------------------")
        result.sentences.forEach { s ->
            sb.appendLine("Sentence #${s.index} [${s.wordCount} words] (Dev: ${String.format(Locale.US, "%+.2f", s.deviationFromMean)}, SqDev: ${String.format(Locale.US, "%.2f", s.squaredDeviation)}):")
            sb.appendLine("\"${s.text}\"")
            sb.appendLine()
        }
        sb.appendLine("====================================")
        sb.appendLine("Analyzed with Text Analyzer App")
        return sb.toString()
    }
}
