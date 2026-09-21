package com.example.engine.adaptation

import com.example.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdaptationEngine {

    suspend fun adaptBlueprintToUserFootage(
        blueprint: EditBlueprint,
        userAnalyses: List<UserVideoAnalysis>,
        config: TransferConfig,
        projectId: String
    ): AdaptedProjectTimeline = withContext(Dispatchers.Default) {
        val strength = config.matchStrength.multiplier
        val mode = config.mode

        // Calculate user subject center
        val primaryUserAnalysis = userAnalyses.firstOrNull()
        val userSubjectCenter = primaryUserAnalysis?.averageSubjectCenter ?: Pair(0.5f, 0.5f)
        val referenceSubjectCenter = Pair(0.5f, 0.48f) // reference baseline

        // Subject relative offset
        val offsetX = (userSubjectCenter.first - referenceSubjectCenter.first) * strength
        val offsetY = (userSubjectCenter.second - referenceSubjectCenter.second) * strength

        // 1. ADAPT SCENES & TIMING
        val shouldTransferTiming = config.transferTiming && mode != TransferMode.FILTER_ONLY && mode != TransferMode.COLOR_ONLY
        val totalUserDuration = userAnalyses.sumOf { it.metadata.durationMs }.coerceAtLeast(3000L)
        val durationScale = if (shouldTransferTiming && blueprint.referenceMetadata.durationMs > 0) {
            totalUserDuration.toFloat() / blueprint.referenceMetadata.durationMs.toFloat()
        } else {
            1.0f
        }

        val adaptedScenes = if (config.transferCuts && mode != TransferMode.FILTER_ONLY && mode != TransferMode.COLOR_ONLY) {
            blueprint.scenes.map { s ->
                val scaledStart = (s.startMs * durationScale).toLong()
                val scaledEnd = (s.endMs * durationScale).toLong()
                s.copy(
                    startMs = scaledStart,
                    endMs = scaledEnd.coerceAtMost(totalUserDuration)
                )
            }
        } else {
            primaryUserAnalysis?.detectedScenes ?: blueprint.scenes
        }

        // 2. ADAPT MOTION & KEYFRAMES RELATIVE TO USER SUBJECT
        val shouldTransferMotion = (config.transferMotion || config.transferZoom) &&
                mode != TransferMode.FILTER_ONLY && mode != TransferMode.COLOR_ONLY && mode != TransferMode.TRANSITIONS_ONLY

        val adaptedKeyframes = if (shouldTransferMotion) {
            blueprint.keyframes.map { kf ->
                val scaledTime = (kf.timestampMs * durationScale).toLong()
                // Apply strength scaling to zoom and rotation
                val zoomDelta = (kf.scale - 1.0f) * strength
                val adaptedScale = (1.0f + zoomDelta).coerceIn(1.0f, 2.5f)
                val adaptedRotation = kf.rotationDeg * strength * (if (config.transferRotation) 1f else 0f)

                // Subject-relative translation
                val adaptedTransX = (kf.translationX + offsetX) * strength
                val adaptedTransY = (kf.translationY + offsetY) * strength

                kf.copy(
                    timestampMs = scaledTime,
                    scale = adaptedScale,
                    translationX = adaptedTransX,
                    translationY = adaptedTransY,
                    rotationDeg = adaptedRotation
                )
            }
        } else {
            listOf(TransformKeyframe(0L, 1.0f, 0f, 0f, 0f, 1f, InterpolationCurve.SMOOTH))
        }

        // 3. ADAPT TRANSITIONS
        val shouldTransferTransitions = config.transferTransitions &&
                mode != TransferMode.FILTER_ONLY && mode != TransferMode.COLOR_ONLY && mode != TransferMode.MOTION_ONLY

        val adaptedTransitions = if (shouldTransferTransitions) {
            blueprint.transitions.map { tr ->
                tr.copy(
                    atTimestampMs = (tr.atTimestampMs * durationScale).toLong(),
                    intensity = (tr.intensity * strength).coerceIn(0.2f, 1.0f)
                )
            }
        } else {
            emptyList()
        }

        // 4. ADAPT COLOR & FILTER LOOK
        val shouldTransferColor = (config.transferColor || config.transferFilterLook) &&
                mode != TransferMode.MOTION_ONLY && mode != TransferMode.TRANSITIONS_ONLY

        val rawColor = blueprint.colorEvents.firstOrNull() ?: ColorProfile()
        val adaptedColor = if (shouldTransferColor) {
            rawColor.copy(
                exposure = rawColor.exposure * strength,
                contrast = rawColor.contrast * strength,
                saturation = rawColor.saturation * strength,
                temperature = rawColor.temperature * strength,
                tint = rawColor.tint * strength,
                vignette = rawColor.vignette * strength,
                grain = rawColor.grain * strength
            )
        } else {
            ColorProfile(name = "Natural Passthrough", exposure = 0f, contrast = 0f, saturation = 0f, temperature = 0f, tint = 0f, vignette = 0f, grain = 0f)
        }

        // 5. ADAPT SPEED EVENTS
        val shouldTransferSpeed = config.transferSpeed && mode == TransferMode.FULL_EDIT
        val adaptedSpeed = if (shouldTransferSpeed) {
            blueprint.speedEvents.map { sp ->
                val rampDelta = (sp.speedMultiplier - 1.0f) * strength
                sp.copy(
                    startMs = (sp.startMs * durationScale).toLong(),
                    endMs = (sp.endMs * durationScale).toLong(),
                    speedMultiplier = (1.0f + rampDelta).coerceIn(0.25f, 3.0f)
                )
            }
        } else {
            emptyList()
        }

        // 6. BEAT SYNC OFFSETS
        val beatOffsets = if (config.transferBeatSync) {
            blueprint.audioMap.beats.map { (it.timestampMs * durationScale).toLong() }
        } else {
            emptyList()
        }

        // 7. TEXT EVENTS
        val adaptedText = if (config.transferText) {
            blueprint.textEvents.map { txt ->
                txt.copy(
                    startMs = (txt.startMs * durationScale).toLong(),
                    endMs = (txt.endMs * durationScale).toLong()
                )
            }
        } else {
            emptyList()
        }

        return@withContext AdaptedProjectTimeline(
            projectId = projectId,
            sourceBlueprintId = blueprint.id,
            userVideoUris = userAnalyses.map { it.mediaUri },
            totalDurationMs = totalUserDuration,
            adaptedScenes = adaptedScenes,
            adaptedKeyframes = adaptedKeyframes,
            adaptedTransitions = adaptedTransitions,
            adaptedColorProfile = adaptedColor,
            adaptedSpeedEvents = adaptedSpeed,
            adaptedTextEvents = adaptedText,
            subjectOffset = Pair(offsetX, offsetY),
            audioBeatSyncOffsets = beatOffsets,
            transferConfig = config
        )
    }
}
