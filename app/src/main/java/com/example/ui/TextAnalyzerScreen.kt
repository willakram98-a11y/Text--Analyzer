package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.SampleTexts
import com.example.model.AnalysisResult
import com.example.model.SentenceInfo
import com.example.ui.components.PrimaryMetricsSection
import com.example.ui.components.SampleTextsDialog
import com.example.ui.components.SentencesBreakdownSection
import com.example.ui.components.StepByStepVarianceDialog
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoPurpleAccent
import com.example.viewmodel.TextAnalyzerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAnalyzerScreen(
    viewModel: TextAnalyzerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()
    val useSampleVariance by viewModel.useSampleVariance.collectAsStateWithLifecycle()
    val selectedSentence by viewModel.selectedSentence.collectAsStateWithLifecycle()
    val showStepByStepDialog by viewModel.showStepByStepDialog.collectAsStateWithLifecycle()
    val showSamplePicker by viewModel.showSamplePicker.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Text Analyzer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setSamplePickerVisible(true) },
                        modifier = Modifier.testTag("sample_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Sample Texts",
                            tint = GeoPurpleAccent
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("overflow_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (analysisResult.isCalculated && analysisResult.totalSentences > 0) {
                                DropdownMenuItem(
                                    text = { Text("Copy Analysis Report") },
                                    leadingIcon = {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GeoPurpleAccent)
                                    },
                                    onClick = {
                                        showMenu = false
                                        val report = viewModel.generateFormattedReport()
                                        clipboardManager.setText(AnnotatedString(report))
                                        Toast.makeText(context, context.getString(R.string.report_copied), Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Share Report") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = GeoPurpleAccent)
                                    },
                                    onClick = {
                                        showMenu = false
                                        val report = viewModel.generateFormattedReport()
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, "English Text Analysis Report")
                                            putExtra(Intent.EXTRA_TEXT, report)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Analysis Report"))
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Load Sample Texts") },
                                leadingIcon = {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GeoPurpleAccent)
                                },
                                onClick = {
                                    showMenu = false
                                    viewModel.setSamplePickerVisible(true)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("main_scroll_column"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Geometric Input Box Section
            item {
                GeometricTextInputSection(
                    inputText = inputText,
                    onTextChanged = viewModel::onTextChanged,
                    onClearClick = viewModel::clearText,
                    onPasteClick = {
                        val clipText = clipboardManager.getText()?.text
                        if (!clipText.isNullOrBlank()) {
                            viewModel.onTextChanged(clipText)
                        } else {
                            Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onOpenSamplePicker = { viewModel.setSamplePickerVisible(true) },
                    onAnalyzeClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.analyzeNow()
                    }
                )
            }

            // Results or Empty State
            if (analysisResult.isCalculated && analysisResult.totalSentences > 0) {
                // Primary Metrics Geometric Grid (Sentences, Total Words, Variance, Avg Words, Bar Chart, StdDev)
                item {
                    PrimaryMetricsSection(
                        result = analysisResult,
                        useSampleVariance = useSampleVariance,
                        onToggleVarianceMode = viewModel::toggleVarianceMode,
                        onShowStepsClick = { viewModel.setStepByStepDialogVisible(true) }
                    )
                }

                // Sentence by Sentence Breakdown Cards
                item {
                    SentencesBreakdownSection(
                        result = analysisResult,
                        selectedSentence = selectedSentence,
                        onSentenceClick = viewModel::selectSentence
                    )
                }
            } else {
                // Empty state card with clean geometry
                item {
                    EmptyAnalysisState(
                        onSelectSampleClick = {
                            viewModel.loadSample(SampleTexts.samples.first())
                        }
                    )
                }
            }

            // Geometric Action Button (Footer Style)
            item {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.analyzeNow()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("recalculate_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPurpleAccent,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (analysisResult.isCalculated && analysisResult.totalSentences > 0) "Recalculate Stats" else "Analyze Text",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Bottom padding for scrolling comfort
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogs
    if (showStepByStepDialog && analysisResult.isCalculated && analysisResult.totalSentences > 0) {
        StepByStepVarianceDialog(
            result = analysisResult,
            useSampleVariance = useSampleVariance,
            onDismiss = { viewModel.setStepByStepDialogVisible(false) }
        )
    }

    if (showSamplePicker) {
        SampleTextsDialog(
            onSelectSample = viewModel::loadSample,
            onDismiss = { viewModel.setSamplePickerVisible(false) }
        )
    }
}

/**
 * Geometric Balance Input Section styled with rounded geometry, inner shadow/fill, and purple accent border
 */
@Composable
fun GeometricTextInputSection(
    inputText: String,
    onTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onPasteClick: () -> Unit,
    onOpenSamplePicker: () -> Unit,
    onAnalyzeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "English Text Input",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GeoPurpleAccent
            )

            // Quick actions (Samples, Clear, Paste)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (inputText.isNotEmpty()) {
                    IconButton(
                        onClick = onClearClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("clear_text_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear text",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onPasteClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("paste_text_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Paste from clipboard",
                        tint = GeoPurpleAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Geometric Input Card with custom container styling & bottom purple border
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onTextChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("text_input_field"),
                    placeholder = {
                        Text(
                            text = "Modern text analysis tools help writers understand the rhythm of their prose. Some sentences are short. Others can be quite long and complex, leading to a high variance in word count. This variety keeps readers engaged and maintains a natural flow throughout the entire document.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            lineHeight = 22.sp
                        )
                    },
                    minLines = 4,
                    maxLines = 10,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPurpleAccent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAnalyzeClick() })
                )

                // Bottom strip of input box: Purple indicator line & stats
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(GeoPurpleAccent)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${inputText.length} characters • ${if (inputText.isBlank()) 0 else inputText.trim().split("\\s+".toRegex()).size} words",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "English Syntax",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAnalysisState(
    onSelectSampleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(24.dp))
            .testTag("empty_state_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = GeoPurpleAccent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Text Statistical Analyzer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter English text or load a sample to calculate:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Metric Features Grid
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureCheckItem(title = "Number of sentences", arabic = "عدد الجمل في النص")
                    FeatureCheckItem(title = "Number of words in each sentence", arabic = "عدد الكلمات في كل جملة")
                    FeatureCheckItem(title = "Variance calculation (σ² & s²)", arabic = "حساب تباين عدد كلمات الجمل")
                    FeatureCheckItem(title = "Total words across text", arabic = "مجموع عدد الكلمات في النص كاملاً")
                }
            }

            OutlinedButton(
                onClick = onSelectSampleClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GeoPurpleAccent),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GeoPurpleAccent)),
                modifier = Modifier.testTag("load_default_sample_button")
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Sample Text", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FeatureCheckItem(title: String, arabic: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(GeoPurpleAccent)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = arabic,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

