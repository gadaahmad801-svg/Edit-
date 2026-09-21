package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AnalysisStage
import com.example.ui.components.BadgeChip
import com.example.ui.components.StudioOutlinedButton
import com.example.ui.theme.*

@Composable
fun AnalysisProgressScreen(
    currentStage: AnalysisStage,
    progressPercent: Int,
    statusMessage: String,
    onCancel: () -> Unit
) {
    val allStages = listOf(
        Pair(AnalysisStage.VALIDATING_MEDIA, "Validating Container & Media"),
        Pair(AnalysisStage.METADATA, "Metadata & Resolution Extraction"),
        Pair(AnalysisStage.SCENE_DETECTION, "Scene Cut & Boundary Detection"),
        Pair(AnalysisStage.SUBJECT_TRACKING, "Focal Subject Trajectory Tracking"),
        Pair(AnalysisStage.MOTION_ANALYSIS, "Camera Motion, Zooms & Shake"),
        Pair(AnalysisStage.SPEED_RAMPS, "Speed Ramps & Slow-Mo Mapping"),
        Pair(AnalysisStage.COLOR_PROFILING, "Color Grade & Look Extraction"),
        Pair(AnalysisStage.TRANSITION_RECREATION, "Transition Classification & Dissolves"),
        Pair(AnalysisStage.BEAT_SYNCHRONIZATION, "Beat Map & Drop Synchronization"),
        Pair(AnalysisStage.BLUEPRINT_GENERATION, "Edit Blueprint Synthesis")
    )

    Scaffold(
        containerColor = StudioBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            BadgeChip(text = "AI DECONSTRUCTION PIPELINE", color = StudioCyan)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Analyzing Reference Video",
                color = StudioTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = statusMessage,
                color = StudioTextSecondary,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Percentage & Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROGRESS",
                    color = StudioTextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$progressPercent%",
                    color = StudioCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progressPercent / 100f },
                color = StudioCyan,
                trackColor = StudioSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("analysis_linear_progress")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Staged Checklist
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allStages.size) { index ->
                    val stage = allStages[index]
                    val isDone = currentStage.ordinal > stage.first.ordinal
                    val isCurrent = currentStage == stage.first

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) StudioSurfaceVariant else StudioSurface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isDone -> StudioGreen
                                        isCurrent -> StudioCyan
                                        else -> StudioBorder
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            } else if (isCurrent) {
                                CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = stage.second,
                            color = if (isDone || isCurrent) StudioTextPrimary else StudioTextTertiary,
                            fontSize = 13.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StudioOutlinedButton(
                text = "Cancel Analysis",
                icon = Icons.Default.Close,
                onClick = onCancel,
                color = StudioRed,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_cancel_analysis"
            )
        }
    }
}
