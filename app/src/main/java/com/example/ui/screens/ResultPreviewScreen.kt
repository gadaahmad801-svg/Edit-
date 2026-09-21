package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AdaptedProjectTimeline
import com.example.engine.render.RenderTimelineCalculator
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComparisonViewMode

@Composable
fun ResultPreviewScreen(
    timeline: AdaptedProjectTimeline,
    projectName: String,
    isPlaying: Boolean,
    playbackTimeMs: Long,
    comparisonMode: ComparisonViewMode,
    splitSliderPosition: Float,
    isSynchronizedDualPlay: Boolean,
    isProTimelineExpanded: Boolean,
    zoomOverride: Float,
    colorOverride: Float,
    shakeOverride: Float,
    timelineCalculator: RenderTimelineCalculator,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onSetComparisonMode: (ComparisonViewMode) -> Unit,
    onSetSplitSliderPosition: (Float) -> Unit,
    onToggleSynchronizedDualPlay: () -> Unit,
    onToggleProTimeline: () -> Unit,
    onAdjustZoom: (Float) -> Unit,
    onAdjustColor: (Float) -> Unit,
    onAdjustShake: (Float) -> Unit,
    onOpenExport: () -> Unit,
    onSaveTemplate: (String) -> Unit,
    onBack: () -> Unit
) {
    val currentFrameState = remember(playbackTimeMs, timeline) {
        timelineCalculator.calculateFrameState(timeline, playbackTimeMs)
    }

    var showSaveTemplateDialog by remember { mutableStateOf(false) }
    var templateNameInput by remember { mutableStateOf("My Custom Recreated Style") }

    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            StudioTopBar(
                title = "Result Preview Studio",
                subtitle = projectName,
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = onOpenExport,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(StudioCyan)
                            .testTag("btn_top_export")
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Export", tint = Color(0xFF00363D))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // VIDEO PLAYER CANVAS
            item {
                VideoCanvasRenderer(
                    frameState = currentFrameState,
                    comparisonMode = comparisonMode,
                    splitSliderPosition = splitSliderPosition,
                    onSplitSliderChange = onSetSplitSliderPosition,
                    zoomOverride = zoomOverride,
                    colorOverride = colorOverride,
                    shakeOverride = shakeOverride,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // COMPARISON MODE TOGGLE ROW
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModePill(
                        label = "Result",
                        isSelected = comparisonMode == ComparisonViewMode.RESULT,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetComparisonMode(ComparisonViewMode.RESULT) }
                    )
                    ModePill(
                        label = "Original",
                        isSelected = comparisonMode == ComparisonViewMode.ORIGINAL,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetComparisonMode(ComparisonViewMode.ORIGINAL) }
                    )
                    ModePill(
                        label = "Split ↔",
                        isSelected = comparisonMode == ComparisonViewMode.SPLIT_SLIDER,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetComparisonMode(ComparisonViewMode.SPLIT_SLIDER) }
                    )
                    ModePill(
                        label = "Reference",
                        isSelected = comparisonMode == ComparisonViewMode.REFERENCE,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetComparisonMode(ComparisonViewMode.REFERENCE) }
                    )
                }
            }

            // TRANSPORT CONTROLS & TIMELINE SCRUBBER
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    // Time text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%02d:%02d.%02d", playbackTimeMs / 60000, (playbackTimeMs / 1000) % 60, (playbackTimeMs % 1000) / 10),
                            color = StudioCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("%02d:%02d.00", timeline.totalDurationMs / 60000, (timeline.totalDurationMs / 1000) % 60),
                            color = StudioTextTertiary,
                            fontSize = 12.sp
                        )
                    }

                    // Scrubber Slider
                    Slider(
                        value = playbackTimeMs.toFloat(),
                        onValueChange = { onSeekTo(it.toLong()) },
                        valueRange = 0f..timeline.totalDurationMs.toFloat().coerceAtLeast(1000f),
                        colors = SliderDefaults.colors(
                            thumbColor = StudioCyan,
                            activeTrackColor = StudioCyan,
                            inactiveTrackColor = StudioSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("playback_scrubber_slider")
                    )

                    // Transport Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onStepFrame(false) }) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Prev Frame", tint = StudioTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(StudioCyan)
                                .clickable { onTogglePlayPause() }
                                .testTag("btn_play_pause"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF00363D),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = { onStepFrame(true) }) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next Frame", tint = StudioTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = onToggleProTimeline) {
                            Icon(
                                imageVector = Icons.Default.ViewTimeline,
                                contentDescription = "Toggle Timeline",
                                tint = if (isProTimelineExpanded) StudioCyan else StudioTextSecondary
                            )
                        }
                    }
                }
            }

            // MULTI-TRACK PRO TIMELINE (EXPANDABLE)
            if (isProTimelineExpanded) {
                item {
                    ProTimelineView(
                        timeline = timeline,
                        currentPlaybackMs = playbackTimeMs,
                        onSeekTo = onSeekTo
                    )
                }
            }

            // LIVE PARAMETRIC OVERRIDES (SLIDERS FOR IMMEDIATE FEEDBACK)
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "LIVE EFFECT OVERRIDES",
                        color = StudioCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Zoom intensity
                    LiveSliderRow(label = "Zoom Scaling", value = zoomOverride, range = 0.5f..2.0f, onValueChange = onAdjustZoom)
                    // Color intensity
                    LiveSliderRow(label = "Color Grading Look", value = colorOverride, range = 0.0f..2.0f, onValueChange = onAdjustColor)
                    // Shake intensity
                    LiveSliderRow(label = "Camera Shake Impact", value = shakeOverride, range = 0.0f..2.0f, onValueChange = onAdjustShake)
                }
            }

            // ACTION BUTTONS ROW: EXPORT & SAVE AS TEMPLATE
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioButton(
                        text = "Export MP4 Video",
                        icon = Icons.Default.FileDownload,
                        onClick = onOpenExport,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_bottom_export_mp4"
                    )
                    StudioOutlinedButton(
                        text = "Save Template",
                        icon = Icons.Default.BookmarkAdd,
                        onClick = { showSaveTemplateDialog = true },
                        modifier = Modifier.weight(1f),
                        testTag = "btn_save_as_template"
                    )
                }
            }
        }
    }

    // SAVE AS TEMPLATE DIALOG
    if (showSaveTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showSaveTemplateDialog = false },
            containerColor = StudioSurface,
            title = { Text(text = "Save as Reusable Template", color = StudioTextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Save this decomposed edit blueprint so you can 1-tap apply it to any future footage.",
                        color = StudioTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = templateNameInput,
                        onValueChange = { templateNameInput = it },
                        label = { Text("Template Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSaveTemplate(templateNameInput)
                    showSaveTemplateDialog = false
                }) {
                    Text("Save", color = StudioCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveTemplateDialog = false }) {
                    Text("Cancel", color = StudioTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ModePill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) StudioCyan.copy(alpha = 0.2f) else StudioSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) StudioCyan else StudioBorder
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) StudioCyan else StudioTextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun LiveSliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = StudioTextSecondary, fontSize = 12.sp)
            Text(text = String.format("%.1fx", value), color = StudioCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = StudioCyan,
                activeTrackColor = StudioCyan,
                inactiveTrackColor = StudioSurfaceVariant
            )
        )
    }
}
