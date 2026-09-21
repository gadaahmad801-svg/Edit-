package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.domain.model.MediaMetadata
import com.example.ui.components.BadgeChip
import com.example.ui.components.StatPill
import com.example.ui.components.StudioButton
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioTopBar
import com.example.ui.theme.*

@Composable
fun UploadReferenceScreen(
    selectedUri: String?,
    metadata: MediaMetadata,
    onVideoSelected: (String, MediaMetadata) -> Unit,
    onStartAnalysis: () -> Unit,
    onBack: () -> Unit
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onVideoSelected(uri.toString(), MediaMetadata(durationMs = 15000L, width = 1920, height = 1080))
        }
    }

    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            StudioTopBar(
                title = "Upload Reference Video",
                subtitle = "Deconstruct cuts, motion, zoom, and color grade",
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text(
                    text = "SELECT VIDEO SOURCE",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SourceButton(
                        label = "Device Storage",
                        icon = Icons.Default.VideoLibrary,
                        modifier = Modifier.weight(1f),
                        onClick = { filePickerLauncher.launch("video/*") },
                        testTag = "btn_pick_gallery_video"
                    )
                    SourceButton(
                        label = "Film Camera",
                        icon = Icons.Default.Videocam,
                        modifier = Modifier.weight(1f),
                        onClick = { filePickerLauncher.launch("video/*") },
                        testTag = "btn_open_camera_video"
                    )
                }
            }

            // PRESET REFERENCE SAMPLES
            item {
                Text(
                    text = "OR CHOOSE A SAMPLE REFERENCE CLIP",
                    color = StudioAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SampleReferenceTile(
                        title = "Cinematic Drone & Tracking Reel",
                        meta = "16s • 4K 24fps • Punch Zoom + Teal Grade",
                        isSelected = selectedUri == "preset_cinematic_1",
                        onClick = {
                            onVideoSelected("preset_cinematic_1", MediaMetadata(durationMs = 16000L, width = 1920, height = 1080, fps = 24))
                        }
                    )
                    SampleReferenceTile(
                        title = "128 BPM Hyper Speed Ramp Reel",
                        meta = "12s • 1080x1920 • Slow-mo Drops & Flash",
                        isSelected = selectedUri == "preset_hyper_beat",
                        onClick = {
                            onVideoSelected("preset_hyper_beat", MediaMetadata(durationMs = 12000L, width = 1080, height = 1920, fps = 60))
                        }
                    )
                    SampleReferenceTile(
                        title = "35mm Vintage Obsidian Film Edit",
                        meta = "15s • 1920x1080 • Soft Grain + Dissolves",
                        isSelected = selectedUri == "preset_vintage_look",
                        onClick = {
                            onVideoSelected("preset_vintage_look", MediaMetadata(durationMs = 15000L, width = 1920, height = 1080, fps = 24))
                        }
                    )
                }
            }

            // DETECTED METADATA CARD
            if (selectedUri != null) {
                item {
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "REFERENCE LOADED",
                                    color = StudioCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (selectedUri.startsWith("preset_")) selectedUri.replace("preset_", "").replace("_", " ").uppercase() else "User Video File",
                                    color = StudioTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            BadgeChip(text = "READY", color = StudioGreen)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatPill(label = "Duration", value = "${metadata.durationMs / 1000}s", icon = Icons.Default.Timer)
                            StatPill(label = "Resolution", value = "${metadata.width}x${metadata.height}", icon = Icons.Default.AspectRatio)
                            StatPill(label = "Framerate", value = "${metadata.fps} fps", icon = Icons.Default.Speed)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    StudioButton(
                        text = "Analyze Reference Video",
                        icon = Icons.Default.AutoFixHigh,
                        onClick = onStartAnalysis,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_start_reference_analysis"
                    )
                }
            }
        }
    }
}

@Composable
fun SourceButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = StudioSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(StudioCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                color = StudioTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SampleReferenceTile(
    title: String,
    meta: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) StudioCyan.copy(alpha = 0.12f) else StudioSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) StudioCyan else StudioBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = StudioCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = StudioTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = meta,
                    color = StudioTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
