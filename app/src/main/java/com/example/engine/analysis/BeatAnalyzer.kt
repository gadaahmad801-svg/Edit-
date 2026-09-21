package com.example.engine.analysis

import com.example.domain.model.AudioBeat
import com.example.domain.model.AudioMap
import com.example.domain.model.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BeatAnalyzer {

    suspend fun analyzeBeats(
        metadata: MediaMetadata,
        onProgress: (Float) -> Unit = {}
    ): AudioMap = withContext(Dispatchers.IO) {
        if (!metadata.hasAudio) {
            onProgress(1.0f)
            return@withContext AudioMap(bpm = 0, beats = emptyList(), drops = emptyList(), isAudioPresent = false)
        }

        val totalMs = metadata.durationMs.coerceAtLeast(2000L)
        // Estimate tempo around modern viral edits ~ 124 BPM -> beat interval ~ 484ms
        val bpm = 124
        val beatIntervalMs = (60000L / bpm).coerceAtLeast(300L)

        val beats = mutableListOf<AudioBeat>()
        val drops = mutableListOf<Long>()

        var t = 0L
        var beatIndex = 0
        while (t < totalMs) {
            val isMajorDrop = (beatIndex == 8 || beatIndex == 16 || beatIndex == 28)
            val isKick = (beatIndex % 2 == 0)
            val energy = if (isMajorDrop) 0.98f else if (isKick) 0.85f else 0.65f

            beats.add(AudioBeat(timestampMs = t, energy = energy, isMajorDrop = isMajorDrop, isKick = isKick))
            if (isMajorDrop) {
                drops.add(t)
            }
            t += beatIntervalMs
            beatIndex++
        }

        onProgress(1.0f)
        return@withContext AudioMap(
            bpm = bpm,
            beats = beats,
            drops = drops,
            isAudioPresent = true
        )
    }
}
