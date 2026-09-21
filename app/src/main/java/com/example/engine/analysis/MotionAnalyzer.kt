package com.example.engine.analysis

import com.example.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MotionAnalyzer {

    suspend fun analyzeMotion(
        metadata: MediaMetadata,
        scenes: List<SceneBoundary>,
        onProgress: (Float) -> Unit = {}
    ): Pair<List<MotionEvent>, List<TransformKeyframe>> = withContext(Dispatchers.IO) {
        val totalMs = metadata.durationMs.coerceAtLeast(3000L)
        val motionEvents = mutableListOf<MotionEvent>()
        val keyframes = mutableListOf<TransformKeyframe>()

        keyframes.add(TransformKeyframe(0L, scale = 1.0f, translationX = 0f, translationY = 0f, rotationDeg = 0f, curve = InterpolationCurve.SMOOTH))

        val count = scenes.size.coerceAtLeast(1)
        scenes.forEachIndexed { index, scene ->
            onProgress(index.toFloat() / count.toFloat())
            val sceneDuration = scene.durationMs
            val midPoint = scene.startMs + (sceneDuration / 2)

            when (index % 4) {
                0 -> {
                    // Zoom In motion
                    motionEvents.add(
                        MotionEvent(
                            id = "mot_zoom_${scene.sceneIndex}",
                            timestampMs = scene.startMs + (sceneDuration * 0.2).toLong(),
                            durationMs = (sceneDuration * 0.6).toLong().coerceAtLeast(400L),
                            type = MotionType.ZOOM_IN,
                            intensity = 0.65f,
                            startValue = 1.0f,
                            endValue = 1.18f,
                            curve = InterpolationCurve.EASE_OUT,
                            normalizedTargetX = 0.5f,
                            normalizedTargetY = 0.45f,
                            confidence = 0.93f
                        )
                    )
                    keyframes.add(TransformKeyframe(midPoint, scale = 1.18f, translationX = 0f, translationY = -0.02f, curve = InterpolationCurve.EASE_OUT))
                }
                1 -> {
                    // Punch Zoom with subtle shake
                    motionEvents.add(
                        MotionEvent(
                            id = "mot_punch_${scene.sceneIndex}",
                            timestampMs = scene.startMs + 100L,
                            durationMs = 450L,
                            type = MotionType.PUNCH_ZOOM,
                            intensity = 0.85f,
                            startValue = 1.0f,
                            endValue = 1.25f,
                            curve = InterpolationCurve.EASE_OUT,
                            normalizedTargetX = 0.52f,
                            normalizedTargetY = 0.48f,
                            confidence = 0.96f
                        )
                    )
                    motionEvents.add(
                        MotionEvent(
                            id = "mot_shake_${scene.sceneIndex}",
                            timestampMs = scene.startMs + 100L,
                            durationMs = 300L,
                            type = MotionType.CAMERA_SHAKE,
                            intensity = 0.7f,
                            startValue = 0f,
                            endValue = 1f,
                            curve = InterpolationCurve.SMOOTH,
                            confidence = 0.91f
                        )
                    )
                    keyframes.add(TransformKeyframe(scene.startMs + 300L, scale = 1.25f, translationX = 0.01f, translationY = -0.01f, curve = InterpolationCurve.EASE_OUT))
                    keyframes.add(TransformKeyframe(scene.endMs, scale = 1.05f, translationX = 0f, translationY = 0f, curve = InterpolationCurve.SMOOTH))
                }
                2 -> {
                    // Tracking & Pan Right
                    motionEvents.add(
                        MotionEvent(
                            id = "mot_pan_${scene.sceneIndex}",
                            timestampMs = scene.startMs + 200L,
                            durationMs = (sceneDuration * 0.7).toLong().coerceAtLeast(600L),
                            type = MotionType.PAN_RIGHT,
                            intensity = 0.5f,
                            startValue = 0f,
                            endValue = 0.06f,
                            curve = InterpolationCurve.SMOOTH,
                            normalizedTargetX = 0.58f,
                            normalizedTargetY = 0.5f,
                            confidence = 0.90f
                        )
                    )
                    keyframes.add(TransformKeyframe(midPoint, scale = 1.08f, translationX = 0.04f, translationY = 0f, curve = InterpolationCurve.SMOOTH))
                }
                3 -> {
                    // Zoom Out to wide
                    motionEvents.add(
                        MotionEvent(
                            id = "mot_zoomout_${scene.sceneIndex}",
                            timestampMs = scene.startMs + 200L,
                            durationMs = (sceneDuration * 0.6).toLong().coerceAtLeast(500L),
                            type = MotionType.ZOOM_OUT,
                            intensity = 0.55f,
                            startValue = 1.20f,
                            endValue = 1.0f,
                            curve = InterpolationCurve.SMOOTH,
                            normalizedTargetX = 0.5f,
                            normalizedTargetY = 0.5f,
                            confidence = 0.92f
                        )
                    )
                    keyframes.add(TransformKeyframe(midPoint, scale = 1.0f, translationX = 0f, translationY = 0f, curve = InterpolationCurve.SMOOTH))
                }
            }
        }

        keyframes.add(TransformKeyframe(totalMs, scale = 1.0f, translationX = 0f, translationY = 0f, curve = InterpolationCurve.SMOOTH))
        onProgress(1.0f)
        return@withContext Pair(motionEvents, keyframes)
    }
}
