package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import com.example.domain.model.EditBlueprint
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun EditBlueprintScreen(
    blueprint: EditBlueprint,
    onApplyToMyVideo: () -> Unit,
    onBack: () -> Unit
) {
    val meta = blueprint.referenceMetadata
    val color = blueprint.colorEvents.firstOrNull()

    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            StudioTopBar(
                title = "Edit Blueprint Generated",
                subtitle = blueprint.title,
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = StudioBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                StudioButton(
                    text = "Apply To My Video Footage",
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    onClick = onApplyToMyVideo,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_apply_blueprint_to_my_video"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // SUMMARY CARD
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            BadgeChip(text = "STRUCTURED BLUEPRINT V1.0", color = StudioCyan)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = blueprint.title,
                                color = StudioTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = StudioGreen, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatPill(label = "Duration", value = "${meta.durationMs / 1000}s", icon = Icons.Default.Timer)
                        StatPill(label = "Resolution", value = "${meta.width}x${meta.height}", icon = Icons.Default.AspectRatio)
                        StatPill(label = "Pace", value = blueprint.overallStyle.pace, icon = Icons.Default.Speed)
                    }
                }
            }

            // STATS BREAKDOWN GRID
            item {
                Text(
                    text = "DECONSTRUCTED EDIT CHARACTERISTICS",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBox(
                            title = "Scene Cuts",
                            count = "${blueprint.scenes.size} cuts",
                            desc = blueprint.scenes.map { it.cutType.name }.distinct().joinToString(", "),
                            icon = Icons.Default.ContentCut,
                            color = StudioCyan,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            title = "Motion Events",
                            count = "${blueprint.motionEvents.size} moves",
                            desc = "${blueprint.keyframes.size} interpolated keyframes",
                            icon = Icons.Default.ZoomIn,
                            color = StudioAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBox(
                            title = "Transitions",
                            count = "${blueprint.transitions.size} fx",
                            desc = "Flashes, dissolves & zooms",
                            icon = Icons.Default.Transform,
                            color = StudioPurple,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            title = "Audio Beat Map",
                            count = "${blueprint.audioMap.bpm} BPM",
                            desc = "${blueprint.audioMap.beats.size} beats, ${blueprint.audioMap.drops.size} drops",
                            icon = Icons.Default.Audiotrack,
                            color = StudioGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // EXTRACTED COLOR GRADE & FILTER LOOK
            if (color != null) {
                item {
                    Text(
                        text = "EXTRACTED COLOR PROFILE (PARAMETRIC)",
                        color = StudioAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = color.name,
                                color = StudioTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            BadgeChip(text = "RECREATION PARAMETERS", color = StudioAmber)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Parametric sliders / values
                        ColorParamRow(label = "Exposure", value = String.format("%+.2f", color.exposure))
                        ColorParamRow(label = "Contrast", value = String.format("%+.2f", color.contrast))
                        ColorParamRow(label = "Saturation", value = String.format("%+.2f", color.saturation))
                        ColorParamRow(label = "Temperature", value = String.format("%+.2f", color.temperature))
                        ColorParamRow(label = "Tint", value = String.format("%+.2f", color.tint))
                        ColorParamRow(label = "Vignette", value = String.format("%.2f", color.vignette))
                        ColorParamRow(label = "Film Grain", value = String.format("%.2f", color.grain))
                    }
                }
            }

            // RECONSTRUCTION CONFIDENCES
            item {
                Text(
                    text = "RECONSTRUCTION FIDELITY",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    blueprint.confidences.forEach { (cat, conf) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = cat, color = StudioTextSecondary, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(
                                    progress = { conf },
                                    color = StudioCyan,
                                    trackColor = StudioSurfaceVariant,
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${(conf * 100).toInt()}%",
                                    color = StudioTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    count: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = StudioSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, color = StudioTextTertiary, fontSize = 11.sp)
            Text(text = count, color = StudioTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, color = StudioTextSecondary, fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
fun ColorParamRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = StudioTextSecondary, fontSize = 12.sp)
        Text(text = value, color = StudioCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
