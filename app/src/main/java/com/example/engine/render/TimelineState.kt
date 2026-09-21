package com.example.engine.render

import com.example.domain.model.*
import kotlin.math.abs
import kotlin.math.sin

data class CurrentRenderFrameState(
    val currentTimestampMs: Long,
    val totalDurationMs: Long,
    val currentScale: Float = 1.0f,
    val currentTranslationX: Float = 0.0f,
    val currentTranslationY: Float = 0.0f,
    val currentRotationDeg: Float = 0.0f,
    val currentShakeOffsetX: Float = 0.0f,
    val currentShakeOffsetY: Float = 0.0f,
    val activeTransition: TransitionEvent? = null,
    val transitionProgress: Float = 0.0f, // 0.0 -> 1.0
    val activeColorProfile: ColorProfile = ColorProfile(),
    val isBeatPulse: Boolean = false,
    val activeText: TextEvent? = null,
    val currentScene: SceneBoundary? = null
)

class RenderTimelineCalculator {

    fun calculateFrameState(
        timeline: AdaptedProjectTimeline,
        currentMs: Long
    ): CurrentRenderFrameState {
        val totalMs = timeline.totalDurationMs.coerceAtLeast(1000L)
        val clampedTime = currentMs.coerceIn(0L, totalMs)

        // 1. Find active scene
        val scene = timeline.adaptedScenes.find { clampedTime in it.startMs..it.endMs }
            ?: timeline.adaptedScenes.firstOrNull()

        // 2. Interpolate Keyframes
        val kfs = timeline.adaptedKeyframes.sortedBy { it.timestampMs }
        var scale = 1.0f
        var transX = 0.0f
        var transY = 0.0f
        var rot = 0.0f

        if (kfs.isNotEmpty()) {
            if (clampedTime <= kfs.first().timestampMs) {
                scale = kfs.first().scale
                transX = kfs.first().translationX
                transY = kfs.first().translationY
                rot = kfs.first().rotationDeg
            } else if (clampedTime >= kfs.last().timestampMs) {
                scale = kfs.last().scale
                transX = kfs.last().translationX
                transY = kfs.last().translationY
                rot = kfs.last().rotationDeg
            } else {
                for (i in 0 until kfs.size - 1) {
                    val k1 = kfs[i]
                    val k2 = kfs[i + 1]
                    if (clampedTime in k1.timestampMs..k2.timestampMs) {
                        val duration = (k2.timestampMs - k1.timestampMs).coerceAtLeast(1L).toFloat()
                        val linearT = ((clampedTime - k1.timestampMs).toFloat() / duration).coerceIn(0f, 1f)
                        // Apply ease-in-out curve
                        val easedT = (sin((linearT - 0.5) * Math.PI) / 2.0 + 0.5).toFloat()

                        scale = k1.scale + (k2.scale - k1.scale) * easedT
                        transX = k1.translationX + (k2.translationX - k1.translationX) * easedT
                        transY = k1.translationY + (k2.translationY - k1.translationY) * easedT
                        rot = k1.rotationDeg + (k2.rotationDeg - k1.rotationDeg) * easedT
                        break
                    }
                }
            }
        }

        // 3. Camera Shake calculation
        var shakeX = 0f
        var shakeY = 0f
        // Check if near any transition or cut
        val nearCut = timeline.adaptedScenes.any { abs(clampedTime - it.startMs) < 300L }
        if (nearCut && timeline.transferConfig.transferCameraMotion) {
            val freq = (clampedTime * 0.05).toFloat()
            shakeX = (sin(freq * 1.5) * 8f * timeline.transferConfig.matchStrength.multiplier).toFloat()
            shakeY = (sin(freq * 2.1) * 6f * timeline.transferConfig.matchStrength.multiplier).toFloat()
        }

        // 4. Transitions
        var activeTrans: TransitionEvent? = null
        var transProgress = 0f
        timeline.adaptedTransitions.forEach { tr ->
            val halfDur = tr.durationMs / 2
            val start = tr.atTimestampMs - halfDur
            val end = tr.atTimestampMs + halfDur
            if (clampedTime in start..end) {
                activeTrans = tr
                transProgress = ((clampedTime - start).toFloat() / tr.durationMs.toFloat()).coerceIn(0f, 1f)
            }
        }

        // 5. Beat pulse
        val isBeat = timeline.audioBeatSyncOffsets.any { abs(clampedTime - it) < 90L }

        // 6. Text
        val activeTxt = timeline.adaptedTextEvents.find { clampedTime in it.startMs..it.endMs }

        return CurrentRenderFrameState(
            currentTimestampMs = clampedTime,
            totalDurationMs = totalMs,
            currentScale = scale,
            currentTranslationX = transX,
            currentTranslationY = transY,
            currentRotationDeg = rot,
            currentShakeOffsetX = shakeX,
            currentShakeOffsetY = shakeY,
            activeTransition = activeTrans,
            transitionProgress = transProgress,
            activeColorProfile = timeline.adaptedColorProfile,
            isBeatPulse = isBeat,
            activeText = activeTxt,
            currentScene = scene
        )
    }
}
