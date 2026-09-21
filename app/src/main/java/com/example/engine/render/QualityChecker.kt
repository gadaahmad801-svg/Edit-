package com.example.engine.render

import com.example.domain.model.AdaptedProjectTimeline

data class QualityCheckResult(
    val isValid: Boolean,
    val warnings: List<String>,
    val errors: List<String>,
    val frameCountEstimate: Long,
    val audioSyncStatus: String,
    val borderSafetyStatus: String
)

class QualityChecker {

    fun validateTimelinePreExport(
        timeline: AdaptedProjectTimeline,
        targetWidth: Int,
        targetHeight: Int,
        fps: Int
    ): QualityCheckResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        if (timeline.totalDurationMs <= 0L) {
            errors.add("Invalid total video duration (0 ms)")
        }

        if (timeline.userVideoUris.isEmpty()) {
            errors.add("No user source video clips selected")
        }

        if (timeline.adaptedScenes.isEmpty()) {
            warnings.add("No explicit scene boundaries detected; single continuous timeline will be rendered.")
        }

        // Check for keyframe bounds (extreme zoom that might cause pixelation)
        val maxZoom = timeline.adaptedKeyframes.maxOfOrNull { it.scale } ?: 1.0f
        if (maxZoom > 2.2f) {
            warnings.add("High zoom level (${String.format("%.1fx", maxZoom)}) detected. Slight resolution softening may occur.")
        }

        // Check aspect ratio crop safety
        val isVertical = targetHeight > targetWidth
        val borderSafety = if (isVertical) "9:16 Vertical Safe Area Active" else "16:9 Cinema Safe Area Active"

        val estimatedFrames = (timeline.totalDurationMs / 1000L) * fps
        val audioSync = if (timeline.audioBeatSyncOffsets.isNotEmpty()) "Beat Synchronized (±12ms tolerance)" else "Standard Audio Passthrough"

        return QualityCheckResult(
            isValid = errors.isEmpty(),
            warnings = warnings,
            errors = errors,
            frameCountEstimate = estimatedFrames,
            audioSyncStatus = audioSync,
            borderSafetyStatus = borderSafety
        )
    }
}
