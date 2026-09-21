package com.example.engine.analysis

import com.example.domain.model.InterpolationCurve
import com.example.domain.model.SceneBoundary
import com.example.domain.model.SpeedEvent
import com.example.domain.model.SpeedType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpeedAnalyzer {

    suspend fun analyzeSpeed(
        scenes: List<SceneBoundary>,
        onProgress: (Float) -> Unit = {}
    ): List<SpeedEvent> = withContext(Dispatchers.IO) {
        val speedEvents = mutableListOf<SpeedEvent>()
        val count = scenes.size.coerceAtLeast(1)

        scenes.forEachIndexed { index, scene ->
            onProgress(index.toFloat() / count.toFloat())
            val duration = scene.durationMs

            if (index % 3 == 1 && duration > 1800L) {
                // Section with speed ramp / slow-mo drop
                val rampPoint = scene.startMs + (duration * 0.4).toLong()
                speedEvents.add(SpeedEvent(scene.startMs, rampPoint, speedMultiplier = 1.4f, type = SpeedType.SPEED_RAMP))
                speedEvents.add(SpeedEvent(rampPoint, scene.endMs, speedMultiplier = 0.5f, type = SpeedType.SLOW_MO, curve = InterpolationCurve.EASE_OUT))
            } else {
                speedEvents.add(SpeedEvent(scene.startMs, scene.endMs, speedMultiplier = 1.0f, type = SpeedType.NORMAL))
            }
        }

        onProgress(1.0f)
        return@withContext speedEvents
    }
}
