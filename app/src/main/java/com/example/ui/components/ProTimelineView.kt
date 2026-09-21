package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AdaptedProjectTimeline
import com.example.ui.theme.*

@Composable
fun ProTimelineView(
    timeline: AdaptedProjectTimeline,
    currentPlaybackMs: Long,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalMs = timeline.totalDurationMs.coerceAtLeast(1000L)

    Surface(
        color = StudioSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Track Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MULTI-TRACK TIMELINE",
                        color = StudioCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timeline.adaptedScenes.size} Scenes • ${timeline.adaptedKeyframes.size} Keyframes",
                        color = StudioTextTertiary,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Split */ }, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.ContentCut, contentDescription = "Split Clip", tint = StudioTextSecondary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { /* Add Keyframe */ }, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.AddLocation, contentDescription = "Add Keyframe", tint = StudioTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Tracks Container
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StudioBackground)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val ratio = (change.position.x / size.width).coerceIn(0f, 1f)
                            onSeekTo((ratio * totalMs).toLong())
                        }
                    }
                    .testTag("pro_timeline_track")
            ) {
                val trackWidth = maxWidth

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // TRACK 1: Video Scenes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    ) {
                        timeline.adaptedScenes.forEach { scene ->
                            val fraction = (scene.durationMs.toFloat() / totalMs.toFloat()).coerceAtLeast(0.02f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(fraction)
                                    .padding(horizontal = 1.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF223A4D))
                                    .border(1.dp, StudioCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .clickable { onSeekTo(scene.startMs) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S${scene.sceneIndex + 1}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // TRACK 2: Motion & Keyframes
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioSurfaceVariant)
                    ) {
                        timeline.adaptedKeyframes.forEach { kf ->
                            val kfRatio = (kf.timestampMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
                            val kfOffset = trackWidth * kfRatio
                            Box(
                                modifier = Modifier
                                    .offset(x = (kfOffset - 6.dp).coerceAtLeast(0.dp))
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(StudioAmber)
                                    .align(Alignment.CenterStart)
                            )
                        }
                    }

                    // TRACK 3: Transitions
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioSurfaceVariant.copy(alpha = 0.7f))
                    ) {
                        timeline.adaptedTransitions.forEach { tr ->
                            val trRatio = (tr.atTimestampMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
                            val trOffset = trackWidth * trRatio
                            Box(
                                modifier = Modifier
                                    .offset(x = (trOffset - 5.dp).coerceAtLeast(0.dp))
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(StudioPurple)
                                    .align(Alignment.CenterStart)
                            )
                        }
                    }

                    // TRACK 4: Audio Beat Map
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F1722)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val beatCount = 28
                        for (i in 0 until beatCount) {
                            val isMajor = (i % 4 == 0)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(if (isMajor) 0.9f else 0.45f)
                                    .padding(horizontal = 1.dp)
                                    .background(if (isMajor) StudioAmber else StudioCyan.copy(alpha = 0.6f))
                            )
                        }
                    }
                }

                // Playhead Needle
                val playheadFraction = (currentPlaybackMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
                val playheadOffset = trackWidth * playheadFraction

                Box(
                    modifier = Modifier
                        .offset(x = playheadOffset - 1.5.dp)
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(StudioCyan)
                )

                // Playhead Top Pointer
                Box(
                    modifier = Modifier
                        .offset(x = playheadOffset - 6.dp, y = 0.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(StudioCyan)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Time labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentSec = currentPlaybackMs / 1000f
                val totalSec = totalMs / 1000f
                Text(
                    text = String.format("%.1fs", currentSec),
                    color = StudioCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format("%.1fs", totalSec),
                    color = StudioTextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
