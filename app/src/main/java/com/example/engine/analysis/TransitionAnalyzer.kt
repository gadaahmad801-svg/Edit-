package com.example.engine.analysis

import com.example.domain.model.CutType
import com.example.domain.model.SceneBoundary
import com.example.domain.model.TransitionEvent
import com.example.domain.model.TransitionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransitionAnalyzer {

    suspend fun analyzeTransitions(
        scenes: List<SceneBoundary>,
        onProgress: (Float) -> Unit = {}
    ): List<TransitionEvent> = withContext(Dispatchers.IO) {
        val transitions = mutableListOf<TransitionEvent>()
        if (scenes.size <= 1) return@withContext emptyList()

        for (i in 0 until scenes.size - 1) {
            onProgress(i.toFloat() / (scenes.size - 1).toFloat())
            val currentScene = scenes[i]
            val cutTime = currentScene.endMs

            val type = when (currentScene.cutType) {
                CutType.FADE_BLACK -> TransitionType.FADE_BLACK
                CutType.FADE_WHITE -> TransitionType.FLASH
                CutType.DISSOLVE -> TransitionType.DISSOLVE
                CutType.SOFT_CUT -> TransitionType.CROSSFADE
                CutType.HARD_CUT -> {
                    when (i % 5) {
                        0 -> TransitionType.FLASH
                        1 -> TransitionType.ZOOM_BLUR
                        2 -> TransitionType.SLIDE_LEFT
                        3 -> TransitionType.CUT
                        else -> TransitionType.GLITCH
                    }
                }
                CutType.UNKNOWN -> TransitionType.CUT
            }

            val durationMs = when (type) {
                TransitionType.CUT -> 0L
                TransitionType.FLASH -> 200L
                TransitionType.ZOOM_BLUR -> 300L
                TransitionType.GLITCH -> 250L
                TransitionType.CROSSFADE, TransitionType.DISSOLVE -> 450L
                TransitionType.FADE_BLACK, TransitionType.FADE_WHITE -> 500L
                else -> 300L
            }

            transitions.add(
                TransitionEvent(
                    atTimestampMs = cutTime,
                    durationMs = durationMs,
                    type = type,
                    intensity = 0.85f,
                    approximationAvailable = true,
                    confidence = 0.91f
                )
            )
        }

        onProgress(1.0f)
        return@withContext transitions
    }
}
