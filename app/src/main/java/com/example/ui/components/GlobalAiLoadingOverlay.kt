package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioPurple
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

/**
 * Global loading animation overlay featuring Material3 CircularProgressIndicator
 * to display real-time status during AI video processing (analysis, recreation, neural adaptation).
 */
@Composable
fun GlobalAiLoadingOverlay(
    visible: Boolean,
    title: String = "AI Video Processing",
    statusMessage: String = "Analyzing video footage...",
    progress: Int = 0,
    canCancel: Boolean = false,
    onCancel: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(220)) + scaleIn(
            initialScale = 0.93f,
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(180)) + scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(180)
        )
    ) {
        // Fullscreen scrim backdrop capturing input to prevent accidental clicks behind
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* Consume taps */ }
                .testTag("global_ai_loading_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing glow background decoration
            val infiniteTransition = rememberInfiniteTransition(label = "glow_transition")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 0.55f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow_alpha"
            )

            // Centered Obsidian Studio modal card
            Surface(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .fillMaxWidth(0.88f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = StudioSurface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                border = BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(
                            StudioCyan.copy(alpha = glowAlpha),
                            StudioPurple.copy(alpha = 0.35f)
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioCyan.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(StudioCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI NEURAL ENGINE",
                            color = StudioCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Material 3 CircularProgressIndicator with center status
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(108.dp)
                    ) {
                        // Background track circle
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = StudioSurfaceVariant,
                            strokeWidth = 7.dp,
                            trackColor = Color.Transparent
                        )

                        // Material3 active indicator (determinate or indeterminate)
                        if (progress in 1..99) {
                            CircularProgressIndicator(
                                progress = { (progress / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("ai_circular_progress_indicator"),
                                color = StudioCyan,
                                strokeWidth = 7.dp,
                                trackColor = Color.Transparent
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("ai_circular_progress_indicator"),
                                color = StudioCyan,
                                strokeWidth = 7.dp,
                                trackColor = Color.Transparent
                            )
                        }

                        // Center content: percentage or AI icon
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (progress in 1..99) {
                                Text(
                                    text = "$progress%",
                                    color = StudioTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Processing",
                                    tint = StudioCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Main Title
                    Text(
                        text = title,
                        color = StudioTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("ai_loading_title")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dynamic status message
                    Text(
                        text = statusMessage,
                        color = StudioTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.testTag("ai_loading_status_text")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Linear progress gauge
                    if (progress in 1..99) {
                        LinearProgressIndicator(
                            progress = { (progress / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = StudioCyan,
                            trackColor = StudioSurfaceVariant
                        )
                    } else {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = StudioCyan,
                            trackColor = StudioSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stage pipeline chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PipelineMiniStep(label = "Deconstruct", active = progress <= 40)
                        PipelineMiniStep(label = "Synthesize", active = progress in 41..80)
                        PipelineMiniStep(label = "Finalize", active = progress > 80)
                    }

                    // Optional Cancel Button
                    if (canCancel) {
                        Spacer(modifier = Modifier.height(20.dp))
                        StudioOutlinedButton(
                            text = "Cancel Processing",
                            icon = Icons.Default.Close,
                            onClick = onCancel,
                            testTag = "btn_cancel_ai_processing",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PipelineMiniStep(label: String, active: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (active) StudioCyan.copy(alpha = 0.15f) else StudioSurfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (active) StudioCyan else StudioTextSecondary,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
