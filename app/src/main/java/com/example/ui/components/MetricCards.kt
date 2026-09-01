package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnalysisResult
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoCardBg
import com.example.ui.theme.GeoDarkPurpleText
import com.example.ui.theme.GeoHighlightCardBg
import com.example.ui.theme.GeoLightPurpleBar
import com.example.ui.theme.GeoPurpleAccent
import java.util.Locale

@Composable
fun PrimaryMetricsSection(
    result: AnalysisResult,
    useSampleVariance: Boolean,
    onToggleVarianceMode: () -> Unit,
    onShowStepsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Analysis Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "نتائج التحليل الإحصائي",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (result.isCalculated && result.totalSentences > 0) {
                AssistChip(
                    onClick = onShowStepsClick,
                    label = { Text("Formula & Steps", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Formula details",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = GeoPurpleAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.testTag("view_steps_chip")
                )
            }
        }

        // Geometric Grid - Top Row: Number of Sentences & Total Words
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val formattedSentenceCount = if (result.totalSentences < 10) "0${result.totalSentences}" else "${result.totalSentences}"
            GeometricMetricCard(
                categoryLabel = "SENTENCES",
                arabicTitle = "عدد الجمل",
                value = formattedSentenceCount,
                highlight = false,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_sentences_card")
            )

            val formattedWordCount = if (result.totalWords < 10 && result.totalWords > 0) "0${result.totalWords}" else "${result.totalWords}"
            GeometricMetricCard(
                categoryLabel = "TOTAL WORDS",
                arabicTitle = "مجموع الكلمات",
                value = formattedWordCount,
                highlight = false,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_total_words_card")
            )
        }

        // Geometric Grid - Bottom Row: Variance & Avg Words
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val varianceValue = if (useSampleVariance) result.sampleVariance else result.variance
            val varianceFormatted = if (result.isCalculated && result.totalSentences > 0) {
                String.format(Locale.US, "%.1f", varianceValue)
            } else {
                "0.0"
            }
            // The symbol lives in the mode badge so the header stays on one line in a half-width card.
            val varianceBadge = if (useSampleVariance) "s² ⟲" else "σ² ⟲"
            val varianceSubtitle = if (useSampleVariance) "التباين (عينة)" else "التباين (مجتمع)"

            GeometricMetricCard(
                categoryLabel = "VARIANCE",
                arabicTitle = varianceSubtitle,
                value = varianceFormatted,
                highlight = false,
                badgeText = varianceBadge,
                onCardClick = onToggleVarianceMode,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_variance_card")
            )

            val avgValue = if (result.isCalculated && result.totalSentences > 0) {
                String.format(Locale.US, "%.1f", result.meanWordsPerSentence)
            } else {
                "0.0"
            }

            GeometricMetricCard(
                categoryLabel = "AVG WORDS",
                arabicTitle = "متوسط الكلمات",
                value = avgValue,
                highlight = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_average_card")
            )
        }

        // Geometric Sentence Breakdown Bar Chart Card
        if (result.isCalculated && result.sentences.isNotEmpty()) {
            GeometricBreakdownChartCard(result = result)
        }

        // Standard Deviation Banner Card
        if (result.isCalculated && result.totalSentences > 0) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .testTag("std_dev_card"),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Standard Deviation",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Standard Deviation (σ)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "الانحراف المعياري (الجذر التربيعي للتباين)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = String.format(Locale.US, "%.3f", result.standardDeviation),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Geometric Balance Metric Card with 24dp radius, bold uppercase header, and light large numbers
 */
@Composable
fun GeometricMetricCard(
    categoryLabel: String,
    arabicTitle: String,
    value: String,
    highlight: Boolean = false,
    badgeText: String? = null,
    onCardClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val containerBg = if (highlight) {
        GeoHighlightCardBg
    } else {
        GeoCardBg
    }

    val borderColor = if (highlight) {
        GeoPurpleAccent
    } else {
        GeoBorder
    }

    val valueColor = if (highlight) {
        GeoDarkPurpleText
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(24.dp))
            .then(if (onCardClick != null) Modifier.clickable(onClick = onCardClick) else Modifier),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = GeoPurpleAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // The label absorbs the leftover width so the badge keeps its natural size
                    // instead of being squeezed into a vertical stack of letters.
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = GeoPurpleAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoPurpleAccent,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = valueColor,
                lineHeight = 36.sp
            )

            Text(
                text = arabicTitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Geometric Sentence Breakdown Visual Chart
 */
@Composable
fun GeometricBreakdownChartCard(
    result: AnalysisResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sentence Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "WORDS/SENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            val maxWords = result.sentences.maxOfOrNull { it.wordCount }?.coerceAtLeast(1) ?: 1

            // Bar Chart Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                result.sentences.forEachIndexed { index, sent ->
                    val heightFraction = (sent.wordCount.toFloat() / maxWords.toFloat()).coerceIn(0.15f, 1f)
                    // Alternate bar colors between Deep Purple (#6750A4) and Light Purple (#D0BCFF) for max variance visibility
                    val isPeak = sent.wordCount == maxWords
                    val barColor = if (isPeak) GeoLightPurpleBar else GeoPurpleAccent

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${sent.wordCount}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(heightFraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }
    }
}

