package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.MatchStrength
import com.example.domain.model.TransferConfig
import com.example.domain.model.TransferMode
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TransferSettingsScreen(
    config: TransferConfig,
    onSetMode: (TransferMode) -> Unit,
    onSetStrength: (MatchStrength) -> Unit,
    onToggleCategory: (String, Boolean) -> Unit,
    onGenerateEdit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            StudioTopBar(
                title = "Transfer Settings",
                subtitle = "Fine-tune which edit characteristics to recreate",
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
                    text = "Generate AI Edit",
                    icon = Icons.Default.AutoAwesome,
                    onClick = onGenerateEdit,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_generate_ai_edit"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // TRANSFER MODE PICKER
            item {
                Text(
                    text = "RECREATION MODE",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    TransferMode.values().forEach { mode ->
                        val isSelected = config.mode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSetMode(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = StudioCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = mode.displayName,
                                    color = if (isSelected) StudioTextPrimary else StudioTextSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = when (mode) {
                                        TransferMode.FULL_EDIT -> "Recreate full cuts, zooms, camera shake, transitions, and grade"
                                        TransferMode.FILTER_ONLY -> "Extract and apply only parametric color grading & look"
                                        TransferMode.MOTION_ONLY -> "Recreate subject-relative tracking, punch zoom, and camera motion"
                                        TransferMode.TRANSITIONS_ONLY -> "Recreate flashes, zoom blurs, and cut transitions"
                                        TransferMode.COLOR_ONLY -> "Natural tone mapping and color curves"
                                        TransferMode.BEAT_SYNC_ONLY -> "Align user cuts strictly to reference audio beat drops"
                                    },
                                    color = StudioTextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // MATCH INTENSITY SLIDER
            item {
                Text(
                    text = "MATCH INTENSITY",
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
                            text = config.matchStrength.label,
                            color = StudioTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        BadgeChip(text = "${(config.matchStrength.multiplier * 100).toInt()}%", color = StudioAmber)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MatchStrength.values().forEach { strength ->
                            val isSel = config.matchStrength == strength
                            OutlinedButton(
                                onClick = { onSetStrength(strength) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSel) StudioAmber.copy(alpha = 0.2f) else StudioSurfaceVariant,
                                    contentColor = if (isSel) StudioAmber else StudioTextSecondary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) StudioAmber else StudioBorder
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(text = strength.name.take(4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // TRANSFER CATEGORY TOGGLES
            item {
                Text(
                    text = "FEATURE CATEGORY TOGGLES",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    CategoryToggleRow("Timing", "Scene duration and pacing", config.transferTiming) { onToggleCategory("Timing", it) }
                    CategoryToggleRow("Cuts", "Hard cuts, soft dissolves & dip-to-black", config.transferCuts) { onToggleCategory("Cuts", it) }
                    CategoryToggleRow("Motion", "Camera motion, pans and tilts", config.transferMotion) { onToggleCategory("Motion", it) }
                    CategoryToggleRow("Zoom", "Punch zooms & gradual zooms", config.transferZoom) { onToggleCategory("Zoom", it) }
                    CategoryToggleRow("Transitions", "White flash, zoom blur, slide & glitch", config.transferTransitions) { onToggleCategory("Transitions", it) }
                    CategoryToggleRow("Color", "Exposure, contrast & saturation curve", config.transferColor) { onToggleCategory("Color", it) }
                    CategoryToggleRow("FilterLook", "Vignette, film grain & temperature", config.transferFilterLook) { onToggleCategory("FilterLook", it) }
                    CategoryToggleRow("BeatSync", "Synchronize cuts to musical audio beats", config.transferBeatSync) { onToggleCategory("BeatSync", it) }
                    CategoryToggleRow("CameraMotion", "Micro camera shake & dynamic energy", config.transferCameraMotion) { onToggleCategory("CameraMotion", it) }
                    CategoryToggleRow("SmartCrop", "Subject-centering aspect ratio auto reframe", config.transferSmartCrop) { onToggleCategory("SmartCrop", it) }
                }
            }
        }
    }
}

@Composable
fun CategoryToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = StudioTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = description, color = StudioTextTertiary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = StudioCyan,
                checkedTrackColor = StudioCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = StudioTextTertiary,
                uncheckedTrackColor = StudioSurfaceVariant
            )
        )
    }
}
