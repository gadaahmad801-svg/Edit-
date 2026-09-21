package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TransitionType
import com.example.engine.render.CurrentRenderFrameState
import com.example.ui.theme.*
import com.example.ui.viewmodel.ComparisonViewMode
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun VideoCanvasRenderer(
    frameState: CurrentRenderFrameState,
    comparisonMode: ComparisonViewMode,
    splitSliderPosition: Float,
    onSplitSliderChange: (Float) -> Unit,
    zoomOverride: Float = 1.0f,
    colorOverride: Float = 1.0f,
    shakeOverride: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .testTag("video_canvas_renderer")
    ) {
        val effectiveScale = (frameState.currentScale * zoomOverride).coerceIn(1.0f, 2.5f)
        val effectiveShakeX = frameState.currentShakeOffsetX * shakeOverride
        val effectiveShakeY = frameState.currentShakeOffsetY * shakeOverride
        val color = frameState.activeColorProfile

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (comparisonMode == ComparisonViewMode.ORIGINAL) 1.0f else effectiveScale
                    scaleY = if (comparisonMode == ComparisonViewMode.ORIGINAL) 1.0f else effectiveScale
                    translationX = if (comparisonMode == ComparisonViewMode.ORIGINAL) 0f else (frameState.currentTranslationX * size.width + effectiveShakeX)
                    translationY = if (comparisonMode == ComparisonViewMode.ORIGINAL) 0f else (frameState.currentTranslationY * size.height + effectiveShakeY)
                    rotationZ = if (comparisonMode == ComparisonViewMode.ORIGINAL) 0f else frameState.currentRotationDeg
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw base synthetic cinematic background representing video frames
            val baseGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A1C29),
                    Color(0xFF0E1019),
                    Color(0xFF08090D)
                )
            )
            drawRect(brush = baseGradient, size = size)

            // Draw visual cinematic elements (simulating dynamic footage motion)
            val tSec = frameState.currentTimestampMs / 1000f
            val focalCenterX = w * (0.5f + (sin(tSec * 0.8f) * 0.08f))
            val focalCenterY = h * 0.48f

            // Subject halo / glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        StudioCyan.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(focalCenterX, focalCenterY),
                    radius = w * 0.35f
                ),
                radius = w * 0.35f,
                center = Offset(focalCenterX, focalCenterY)
            )

            // Dynamic horizon & subject silhouette
            drawLine(
                color = StudioCyan.copy(alpha = 0.15f),
                start = Offset(0f, h * 0.72f),
                end = Offset(w, h * 0.72f),
                strokeWidth = 2f
            )

            // Beat pulse wave ring
            if (frameState.isBeatPulse) {
                drawCircle(
                    color = StudioCyan.copy(alpha = 0.4f),
                    radius = w * 0.22f,
                    center = Offset(focalCenterX, focalCenterY),
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            // 2. Color grading overlay matrix simulation
            if (comparisonMode != ComparisonViewMode.ORIGINAL && colorOverride > 0.05f) {
                // Temperature tint (warm amber or cool cyan)
                val tempColor = if (color.temperature >= 0f) {
                    StudioAmber.copy(alpha = (color.temperature * 0.25f * colorOverride).coerceIn(0f, 0.4f))
                } else {
                    StudioCyan.copy(alpha = (abs(color.temperature) * 0.25f * colorOverride).coerceIn(0f, 0.4f))
                }
                drawRect(color = tempColor, size = size)

                // Contrast / Shadows tint
                if (color.contrast > 0f) {
                    val shadowAlpha = (color.contrast * 0.20f * colorOverride).coerceIn(0f, 0.35f)
                    drawRect(color = Color.Black.copy(alpha = shadowAlpha), size = size)
                }

                // Vignette
                if (color.vignette > 0.05f) {
                    val vigRadius = (w * 0.85f).coerceAtLeast(10f)
                    val vigAlpha = (color.vignette * 0.85f * colorOverride).coerceIn(0f, 0.85f)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = vigAlpha)),
                            center = Offset(w / 2f, h / 2f),
                            radius = vigRadius
                        ),
                        size = size
                    )
                }
            }

            // 3. Active Transitions Overlay (Flash, Dip to black, etc.)
            val trans = frameState.activeTransition
            if (trans != null && comparisonMode != ComparisonViewMode.ORIGINAL) {
                when (trans.type) {
                    TransitionType.FLASH -> {
                        val flashAlpha = (sin(frameState.transitionProgress * Math.PI) * 0.85f * trans.intensity).toFloat()
                        drawRect(color = Color.White.copy(alpha = flashAlpha.coerceIn(0f, 1f)), size = size)
                    }
                    TransitionType.FADE_BLACK -> {
                        val blackAlpha = (sin(frameState.transitionProgress * Math.PI) * 0.95f * trans.intensity).toFloat()
                        drawRect(color = Color.Black.copy(alpha = blackAlpha.coerceIn(0f, 1f)), size = size)
                    }
                    TransitionType.ZOOM_BLUR -> {
                        val blurAlpha = (sin(frameState.transitionProgress * Math.PI) * 0.45f * trans.intensity).toFloat()
                        drawCircle(
                            color = StudioCyan.copy(alpha = blurAlpha),
                            radius = w * 0.4f,
                            center = Offset(w / 2f, h / 2f),
                            style = Stroke(width = 16.dp.toPx())
                        )
                    }
                    TransitionType.GLITCH -> {
                        val glitchSliceHeight = h * 0.12f
                        val glitchAlpha = 0.5f * trans.intensity
                        drawRect(
                            color = StudioCyan.copy(alpha = glitchAlpha),
                            topLeft = Offset(0f, h * 0.3f),
                            size = Size(w, glitchSliceHeight)
                        )
                        drawRect(
                            color = StudioRed.copy(alpha = glitchAlpha),
                            topLeft = Offset(10f, h * 0.55f),
                            size = Size(w, glitchSliceHeight)
                        )
                    }
                    else -> {}
                }
            }
        }

        // 4. Split-Screen Comparison Mode Divider & Handle
        if (comparisonMode == ComparisonViewMode.SPLIT_SLIDER) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalWidth = maxWidth
                val dividerOffset = totalWidth * splitSliderPosition

                // Divider Line
                Box(
                    modifier = Modifier
                        .offset(x = dividerOffset - 1.dp)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(StudioCyan)
                )

                // Draggable Thumb Knob
                Box(
                    modifier = Modifier
                        .offset(x = dividerOffset - 16.dp, y = (maxHeight / 2) - 16.dp)
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioCyan)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newPos = (splitSliderPosition + (dragAmount.x / size.width)).coerceIn(0.05f, 0.95f)
                                onSplitSliderChange(newPos)
                            }
                        }
                        .testTag("split_slider_handle"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↔",
                        color = Color(0xFF00363D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Left Label: ORIGINAL
                Text(
                    text = "ORIGINAL",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                // Right Label: RESULT
                Text(
                    text = "RESULT",
                    color = StudioCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Active Mode Tag (Top-left pill)
        if (comparisonMode != ComparisonViewMode.SPLIT_SLIDER) {
            Text(
                text = when (comparisonMode) {
                    ComparisonViewMode.ORIGINAL -> "ORIGINAL SOURCE"
                    ComparisonViewMode.REFERENCE -> "REFERENCE VIDEO"
                    ComparisonViewMode.RESULT -> "RESULT (AI ADAPTED)"
                    else -> ""
                },
                color = if (comparisonMode == ComparisonViewMode.RESULT) StudioCyan else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        // Scene boundary indicator (Top-right pill)
        if (frameState.currentScene != null) {
            Text(
                text = "Scene #${frameState.currentScene.sceneIndex + 1} (${frameState.currentScene.dominantMotionDirection})",
                color = StudioAmber,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
