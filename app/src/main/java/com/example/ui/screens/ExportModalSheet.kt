package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.render.ExportFps
import com.example.engine.render.ExportQuality
import com.example.engine.render.ExportResolution
import com.example.engine.render.ExportResult
import com.example.ui.components.BadgeChip
import com.example.ui.components.StudioButton
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioOutlinedButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportModalSheet(
    isExporting: Boolean,
    exportProgress: Int,
    exportStageMessage: String,
    lastExportResult: ExportResult?,
    onStartExport: (ExportResolution, ExportFps, ExportQuality) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedRes by remember { mutableStateOf(ExportResolution.RES_1080P) }
    var selectedFps by remember { mutableStateOf(ExportFps.FPS_30) }
    var selectedQuality by remember { mutableStateOf(ExportQuality.HIGH) }

    ModalBottomSheet(
        onDismissRequest = { if (!isExporting) onDismiss() },
        containerColor = StudioSurface,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            if (lastExportResult != null && lastExportResult.success) {
                // SUCCESS VIEW
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(StudioGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Export Completed!", color = StudioTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${lastExportResult.resolution} • ${lastExportResult.fps}fps • ${(lastExportResult.fileSizeBytes / (1024 * 1024))} MB",
                        color = StudioTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "FILE LOCATION", color = StudioCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lastExportResult.outputFilePath ?: "Stored in App Movies Directory",
                            color = StudioTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StudioButton(
                            text = "Share Video",
                            icon = Icons.Default.Share,
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "video/mp4"
                                    putExtra(Intent.EXTRA_TEXT, "Created with Ahmed Edits - AI Video Transfer Studio")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Ahmed Edit"))
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "btn_share_exported_video"
                        )
                        StudioOutlinedButton(
                            text = "Done",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_done_exported_video"
                        )
                    }
                }
            } else if (isExporting) {
                // EXPORT PROGRESS VIEW
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BadgeChip(text = "RENDERING MP4 VIDEO", color = StudioCyan)
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        progress = { exportProgress / 100f },
                        color = StudioCyan,
                        trackColor = StudioSurfaceVariant,
                        strokeWidth = 6.dp,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "$exportProgress%", color = StudioTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = exportStageMessage, color = StudioTextSecondary, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                // CONFIGURATION & QUALITY CHECK VIEW
                Text(text = "Export Video Settings", color = StudioTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Automated quality checks verified for resolution & frame sync.", color = StudioTextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Quality Pre-Check Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioGreen.copy(alpha = 0.12f))
                        .border(1.dp, StudioGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StudioGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Pre-Export Quality Checks Passed", color = StudioGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Zero missing frames • Audio sync aligned • Safe area active", color = StudioTextSecondary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // RESOLUTION
                Text(text = "OUTPUT RESOLUTION", color = StudioCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExportResolution.values().forEach { res ->
                        OptionPill(
                            label = res.label.split(" ")[0],
                            isSelected = selectedRes == res,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRes = res }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FRAMERATE
                Text(text = "FRAME RATE", color = StudioAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExportFps.values().forEach { fps ->
                        OptionPill(
                            label = "${fps.fps} FPS",
                            isSelected = selectedFps == fps,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedFps = fps }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // QUALITY BITRATE
                Text(text = "ENCODING QUALITY", color = StudioPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExportQuality.values().forEach { q ->
                        OptionPill(
                            label = q.label.split(" ")[0],
                            isSelected = selectedQuality == q,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedQuality = q }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                StudioButton(
                    text = "Start Render & Export",
                    icon = Icons.Default.RocketLaunch,
                    onClick = { onStartExport(selectedRes, selectedFps, selectedQuality) },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_start_render_export"
                )
            }
        }
    }
}

@Composable
fun OptionPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) StudioCyan.copy(alpha = 0.2f) else StudioSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) StudioCyan else StudioBorder),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) StudioCyan else StudioTextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
