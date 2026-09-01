package com.example.data

import com.example.model.SampleText

object SampleTexts {
    val samples = listOf(
        SampleText(
            title = "Simple Sentences (3 Sentences)",
            description = "Clear 3-sentence test case for checking variance calculations easily.",
            text = "Kotlin is a modern and concise programming language. Jetpack Compose simplifies building native Android user interfaces. Developers create elegant mobile applications faster with less boilerplate code."
        ),
        SampleText(
            title = "Academic & Technology",
            description = "Complex technical sentences with varied word counts.",
            text = "Artificial intelligence models are rapidly reshaping scientific research and engineering workflows across the globe. Researchers combine advanced neural architectures with large datasets to discover novel drug compounds and forecast climate patterns. These powerful systems empower innovators to solve previously intractable problems in record time."
        ),
        SampleText(
            title = "Short Story Excerpt",
            description = "Descriptive literary prose with short and long rhythmic sentences.",
            text = "The old lighthouse stood tall against the raging sea. Huge waves crashed violently against the black cliffs below. Inside the warm tower, an amber beacon rotated continuously through the dense fog, guiding weary sailors safely toward the harbor."
        ),
        SampleText(
            title = "Equal Length Sentences (Zero Variance)",
            description = "Three sentences with identical word counts to test zero variance (σ² = 0).",
            text = "Sun shines bright today. Birds sing sweet songs. Rain falls down softly."
        )
    )
}
