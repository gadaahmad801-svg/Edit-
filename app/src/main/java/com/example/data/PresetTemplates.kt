package com.example.data

import com.example.data.local.TemplateEntity
import com.example.domain.model.*

object PresetTemplates {

    fun getBuiltinTemplates(): List<TemplateEntity> {
        val t1 = createCinematicDriftBlueprint()
        val t2 = createHyperBeatSpeedRampBlueprint()
        val t3 = createObsidianVintageFilmBlueprint()
        val t4 = createPunchZoomReelBlueprint()
        val t5 = createGlitchFlashActionBlueprint()

        return listOf(
            TemplateEntity(
                id = "tpl_cinematic_drift",
                name = "Ahmed Cinematic Drift",
                description = "Smooth 2.39:1 letterbox motion with slow punch zoom, teal-orange grade, and soft dissolve cuts.",
                category = "Cinematic",
                blueprintJson = BlueprintJsonHelper.blueprintToJson(t1),
                thumbnailResName = "ic_cinematic"
            ),
            TemplateEntity(
                id = "tpl_hyper_beat_ramp",
                name = "Hyper Beat Speed Ramp",
                description = "128 BPM beat-synced fast ramps, 0.5x slow-mo on drop, flash transitions, and high contrast vibrance.",
                category = "Reel / Beat",
                blueprintJson = BlueprintJsonHelper.blueprintToJson(t2),
                thumbnailResName = "ic_speed_ramp"
            ),
            TemplateEntity(
                id = "tpl_obsidian_film",
                name = "Obsidian 35mm Vintage Look",
                description = "Rich shadows, subtle organic grain, vignette, warm film curve, and smooth slide transitions.",
                category = "Vintage / Film",
                blueprintJson = BlueprintJsonHelper.blueprintToJson(t3),
                thumbnailResName = "ic_vintage"
            ),
            TemplateEntity(
                id = "tpl_punch_zoom_reel",
                name = "Punch Zoom Viral Reel",
                description = "Vertical 9:16 optimized subject tracking with 1.25x micro-zooms, camera shake impact, and quick snap cuts.",
                category = "Reel / Beat",
                blueprintJson = BlueprintJsonHelper.blueprintToJson(t4),
                thumbnailResName = "ic_punch_zoom"
            ),
            TemplateEntity(
                id = "tpl_glitch_flash_action",
                name = "Electric Flash & Glitch",
                description = "Dynamic action edit with RGB glitch approximations, high-frequency white flashes, and fast panning.",
                category = "Action / Sport",
                blueprintJson = BlueprintJsonHelper.blueprintToJson(t5),
                thumbnailResName = "ic_glitch"
            )
        )
    }

    private fun createCinematicDriftBlueprint(): EditBlueprint {
        return EditBlueprint(
            id = "bp_cinematic_drift",
            title = "Ahmed Cinematic Drift",
            referenceMetadata = MediaMetadata(durationMs = 16000L, width = 1920, height = 1080, fps = 24),
            scenes = listOf(
                SceneBoundary(0, 0L, 3500L, CutType.SOFT_CUT, 0.94f, FramingType.WIDE, "Slow Pan Right", 0.48f),
                SceneBoundary(1, 3500L, 7800L, CutType.DISSOLVE, 0.91f, FramingType.MEDIUM, "Push In", 0.52f),
                SceneBoundary(2, 7800L, 11500L, CutType.SOFT_CUT, 0.95f, FramingType.CLOSE_UP, "Tracking Subject", 0.55f),
                SceneBoundary(3, 11500L, 16000L, CutType.FADE_BLACK, 0.96f, FramingType.WIDE, "Slow Zoom Out", 0.45f)
            ),
            motionEvents = listOf(
                MotionEvent("m1", 500L, 2500L, MotionType.PAN_RIGHT, 0.4f, 0.0f, 0.15f, InterpolationCurve.SMOOTH, 0.5f, 0.5f, 0.92f),
                MotionEvent("m2", 3600L, 3000L, MotionType.ZOOM_IN, 0.5f, 1.0f, 1.14f, InterpolationCurve.EASE_IN_OUT, 0.5f, 0.45f, 0.94f),
                MotionEvent("m3", 8000L, 2500L, MotionType.SUBJECT_TRACKING, 0.6f, 1.0f, 1.08f, InterpolationCurve.SMOOTH, 0.48f, 0.52f, 0.89f),
                MotionEvent("m4", 12000L, 3500L, MotionType.ZOOM_OUT, 0.4f, 1.15f, 1.0f, InterpolationCurve.SMOOTH, 0.5f, 0.5f, 0.91f)
            ),
            keyframes = listOf(
                TransformKeyframe(0L, 1.0f, 0f, 0f, 0f, 1f, InterpolationCurve.SMOOTH),
                TransformKeyframe(3500L, 1.05f, 0.03f, 0f, 0f, 1f, InterpolationCurve.SMOOTH),
                TransformKeyframe(7800L, 1.15f, 0f, -0.02f, 0f, 1f, InterpolationCurve.SMOOTH),
                TransformKeyframe(16000L, 1.0f, 0f, 0f, 0f, 1f, InterpolationCurve.SMOOTH)
            ),
            speedEvents = listOf(
                SpeedEvent(0L, 7800L, 1.0f, SpeedType.NORMAL),
                SpeedEvent(7800L, 11500L, 0.8f, SpeedType.SLOW_MO),
                SpeedEvent(11500L, 16000L, 1.0f, SpeedType.NORMAL)
            ),
            transitions = listOf(
                TransitionEvent(3500L, 500L, TransitionType.CROSSFADE, 0.7f),
                TransitionEvent(7800L, 400L, TransitionType.DISSOLVE, 0.8f),
                TransitionEvent(11500L, 600L, TransitionType.FADE_BLACK, 0.9f)
            ),
            colorEvents = listOf(
                ColorProfile("cinematic_teal_orange", "Teal & Warm Amber", exposure = 0.05f, contrast = 0.22f, saturation = 0.18f, temperature = 0.12f, tint = -0.04f, highlights = -0.15f, shadows = 0.10f, vignette = 0.30f, grain = 0.05f)
            ),
            textEvents = listOf(
                TextEvent("txt1", "AHMED EDITS CINEMATIC", 1000L, 3500L, 0.5f, 0.82f, 1.0f, 0f, "#FFFFFF", "Fade In", true)
            ),
            audioMap = AudioMap(
                bpm = 95,
                beats = listOf(
                    AudioBeat(0L, 0.6f), AudioBeat(630L, 0.7f), AudioBeat(1260L, 0.8f), AudioBeat(1890L, 0.6f),
                    AudioBeat(3500L, 0.9f, isMajorDrop = true), AudioBeat(7800L, 0.85f), AudioBeat(11500L, 0.7f)
                ),
                drops = listOf(3500L, 7800L)
            ),
            overallStyle = EditStyleProfile("Slow Cinematic", "Smooth Subtle", "Slow Creep Zoom", "Soft Dissolve", "Teal & Amber Film", "Fluid Ambient"),
            confidences = mapOf("Motion" to 0.94f, "Color" to 0.96f, "Transitions" to 0.92f, "Timing" to 0.95f, "Audio" to 0.88f)
        )
    }

    private fun createHyperBeatSpeedRampBlueprint(): EditBlueprint {
        return EditBlueprint(
            id = "bp_hyper_beat",
            title = "Hyper Beat Speed Ramp",
            referenceMetadata = MediaMetadata(durationMs = 12000L, width = 1080, height = 1920, fps = 60),
            scenes = listOf(
                SceneBoundary(0, 0L, 1875L, CutType.HARD_CUT, 0.96f, FramingType.WIDE, "Fast Approach"),
                SceneBoundary(1, 1875L, 3750L, CutType.HARD_CUT, 0.97f, FramingType.CLOSE_UP, "Rapid Zoom"),
                SceneBoundary(2, 3750L, 6560L, CutType.HARD_CUT, 0.95f, FramingType.MEDIUM, "Slow Motion Drop"),
                SceneBoundary(3, 6560L, 9375L, CutType.HARD_CUT, 0.98f, FramingType.CLOSE_UP, "Fast Ramped Action"),
                SceneBoundary(4, 9375L, 12000L, CutType.HARD_CUT, 0.94f, FramingType.WIDE, "Outro Pan")
            ),
            motionEvents = listOf(
                MotionEvent("m1", 1200L, 675L, MotionType.PUNCH_ZOOM, 0.8f, 1.0f, 1.30f, InterpolationCurve.EASE_OUT, 0.5f, 0.5f, 0.96f),
                MotionEvent("m2", 1875L, 400L, MotionType.CAMERA_SHAKE, 0.7f, 0f, 1.0f, InterpolationCurve.SMOOTH, 0.5f, 0.5f, 0.94f),
                MotionEvent("m3", 3750L, 1200L, MotionType.ZOOM_OUT, 0.6f, 1.25f, 1.0f, InterpolationCurve.SMOOTH, 0.5f, 0.5f, 0.92f),
                MotionEvent("m4", 6560L, 800L, MotionType.PUNCH_ZOOM, 0.85f, 1.0f, 1.35f, InterpolationCurve.EASE_OUT, 0.48f, 0.52f, 0.95f),
                MotionEvent("m5", 9375L, 500L, MotionType.ROTATION_CW, 0.5f, 0f, 4.0f, InterpolationCurve.EASE_IN_OUT, 0.5f, 0.5f, 0.89f)
            ),
            keyframes = listOf(
                TransformKeyframe(0L, 1.0f),
                TransformKeyframe(1875L, 1.28f),
                TransformKeyframe(3750L, 1.0f),
                TransformKeyframe(6560L, 1.32f),
                TransformKeyframe(9375L, 1.05f),
                TransformKeyframe(12000L, 1.0f)
            ),
            speedEvents = listOf(
                SpeedEvent(0L, 1500L, 1.5f, SpeedType.SPEED_RAMP),
                SpeedEvent(1500L, 1875L, 0.4f, SpeedType.SLOW_MO),
                SpeedEvent(1875L, 3750L, 1.8f, SpeedType.SPEED_RAMP),
                SpeedEvent(3750L, 6560L, 0.35f, SpeedType.SLOW_MO), // Bass drop slow-mo
                SpeedEvent(6560L, 9375L, 2.0f, SpeedType.SPEED_RAMP),
                SpeedEvent(9375L, 12000L, 1.0f, SpeedType.NORMAL)
            ),
            transitions = listOf(
                TransitionEvent(1875L, 200L, TransitionType.FLASH, 0.9f),
                TransitionEvent(3750L, 250L, TransitionType.ZOOM_BLUR, 0.85f),
                TransitionEvent(6560L, 200L, TransitionType.FLASH, 0.95f),
                TransitionEvent(9375L, 300L, TransitionType.SLIDE_LEFT, 0.8f)
            ),
            colorEvents = listOf(
                ColorProfile("hyper_neon", "Vibrant High Contrast", exposure = 0.10f, contrast = 0.35f, saturation = 0.38f, temperature = -0.05f, highlights = -0.05f, shadows = 0.05f, vignette = 0.20f, grain = 0.02f)
            ),
            audioMap = AudioMap(
                bpm = 128,
                beats = (0..24).map { i -> AudioBeat(i * 468L, if (i % 4 == 0) 0.95f else 0.7f, isMajorDrop = (i == 8 || i == 14)) },
                drops = listOf(3750L, 6560L)
            ),
            overallStyle = EditStyleProfile("Ultra Fast Ramp", "High Kinetic", "Aggressive Punch", "White Flash & Snaps", "Vibrant Contrast", "128 BPM Strict Lock"),
            confidences = mapOf("Motion" to 0.96f, "Speed" to 0.94f, "Transitions" to 0.95f, "Color" to 0.91f, "Beat" to 0.97f)
        )
    }

    private fun createObsidianVintageFilmBlueprint(): EditBlueprint {
        return EditBlueprint(
            id = "bp_obsidian_vintage",
            title = "Obsidian 35mm Vintage Look",
            referenceMetadata = MediaMetadata(durationMs = 15000L, width = 1920, height = 1080, fps = 24),
            scenes = listOf(
                SceneBoundary(0, 0L, 4000L, CutType.SOFT_CUT, 0.93f, FramingType.MEDIUM, "Gentle Sway"),
                SceneBoundary(1, 4000L, 9000L, CutType.DISSOLVE, 0.90f, FramingType.CLOSE_UP, "Slow Push"),
                SceneBoundary(2, 9000L, 15000L, CutType.SOFT_CUT, 0.94f, FramingType.WIDE, "Static Frame")
            ),
            motionEvents = listOf(
                MotionEvent("m1", 0L, 4000L, MotionType.CAMERA_SHAKE, 0.25f, 0f, 0.5f, InterpolationCurve.SMOOTH, 0.5f, 0.5f, 0.90f),
                MotionEvent("m2", 4500L, 4000L, MotionType.ZOOM_IN, 0.35f, 1.0f, 1.09f, InterpolationCurve.SMOOTH, 0.5f, 0.48f, 0.92f)
            ),
            keyframes = listOf(
                TransformKeyframe(0L, 1.0f),
                TransformKeyframe(8000L, 1.08f),
                TransformKeyframe(15000L, 1.0f)
            ),
            speedEvents = listOf(SpeedEvent(0L, 15000L, 1.0f, SpeedType.NORMAL)),
            transitions = listOf(
                TransitionEvent(4000L, 600L, TransitionType.DISSOLVE, 0.75f),
                TransitionEvent(9000L, 700L, TransitionType.CROSSFADE, 0.8f)
            ),
            colorEvents = listOf(
                ColorProfile("obsidian_film", "Kodak 35mm Emulsion", exposure = -0.05f, contrast = 0.20f, saturation = -0.08f, temperature = 0.16f, tint = 0.05f, highlights = -0.22f, shadows = 0.14f, vignette = 0.42f, grain = 0.15f)
            ),
            overallStyle = EditStyleProfile("Nostalgic Pace", "Subtle Organic", "Soft Zoom", "Gentle Dissolve", "Moody Warm Film", "Relaxed Rhythm"),
            confidences = mapOf("Color" to 0.97f, "Motion" to 0.88f, "Transitions" to 0.91f, "Timing" to 0.93f)
        )
    }

    private fun createPunchZoomReelBlueprint(): EditBlueprint {
        return EditBlueprint(
            id = "bp_punch_zoom_reel",
            title = "Punch Zoom Viral Reel",
            referenceMetadata = MediaMetadata(durationMs = 14000L, width = 1080, height = 1920, fps = 30),
            scenes = listOf(
                SceneBoundary(0, 0L, 2500L, CutType.HARD_CUT, 0.95f, FramingType.MEDIUM, "Center Subject"),
                SceneBoundary(1, 2500L, 5500L, CutType.HARD_CUT, 0.96f, FramingType.CLOSE_UP, "Punch In"),
                SceneBoundary(2, 5500L, 9000L, CutType.HARD_CUT, 0.94f, FramingType.MEDIUM, "Side Walk"),
                SceneBoundary(3, 9000L, 14000L, CutType.HARD_CUT, 0.97f, FramingType.EXTREME_CLOSE_UP, "Hero Punch")
            ),
            motionEvents = listOf(
                MotionEvent("pz1", 2300L, 400L, MotionType.PUNCH_ZOOM, 0.75f, 1.0f, 1.25f, InterpolationCurve.EASE_OUT, 0.5f, 0.45f, 0.95f),
                MotionEvent("pz2", 5300L, 400L, MotionType.PUNCH_ZOOM, 0.70f, 1.25f, 1.0f, InterpolationCurve.EASE_IN, 0.5f, 0.5f, 0.93f),
                MotionEvent("pz3", 8800L, 400L, MotionType.PUNCH_ZOOM, 0.85f, 1.0f, 1.35f, InterpolationCurve.EASE_OUT, 0.52f, 0.42f, 0.96f),
                MotionEvent("sh1", 2500L, 300L, MotionType.CAMERA_SHAKE, 0.6f, 0f, 1.0f, InterpolationCurve.SMOOTH, 0.5f, 0.5f, 0.91f)
            ),
            keyframes = listOf(
                TransformKeyframe(0L, 1.0f),
                TransformKeyframe(2500L, 1.25f),
                TransformKeyframe(5500L, 1.0f),
                TransformKeyframe(9000L, 1.35f),
                TransformKeyframe(14000L, 1.10f)
            ),
            speedEvents = listOf(
                SpeedEvent(0L, 2500L, 1.0f),
                SpeedEvent(2500L, 3200L, 0.5f, SpeedType.SLOW_MO),
                SpeedEvent(3200L, 5500L, 1.2f, SpeedType.SPEED_RAMP),
                SpeedEvent(5500L, 9000L, 1.0f),
                SpeedEvent(9000L, 10500L, 0.4f, SpeedType.SLOW_MO),
                SpeedEvent(10500L, 14000L, 1.0f)
            ),
            transitions = listOf(
                TransitionEvent(2500L, 250L, TransitionType.FLASH, 0.8f),
                TransitionEvent(5500L, 300L, TransitionType.ZOOM_BLUR, 0.75f),
                TransitionEvent(9000L, 250L, TransitionType.FLASH, 0.9f)
            ),
            colorEvents = listOf(
                ColorProfile("punch_clean", "Modern Crisp Clean", exposure = 0.08f, contrast = 0.25f, saturation = 0.22f, temperature = 0.0f, highlights = -0.08f, shadows = 0.05f, vignette = 0.15f, grain = 0.01f)
            ),
            overallStyle = EditStyleProfile("Fast Punch", "High Energy Zooms", "1.35x Rapid Snap", "Bright Flashes", "Crisp Clean Pop", "Viral Social Beat"),
            confidences = mapOf("Motion" to 0.97f, "Zoom" to 0.98f, "Transitions" to 0.92f, "Timing" to 0.95f)
        )
    }

    private fun createGlitchFlashActionBlueprint(): EditBlueprint {
        return EditBlueprint(
            id = "bp_glitch_flash",
            title = "Electric Flash & Glitch",
            referenceMetadata = MediaMetadata(durationMs = 10000L, width = 1920, height = 1080, fps = 30),
            scenes = listOf(
                SceneBoundary(0, 0L, 2000L, CutType.HARD_CUT, 0.94f, FramingType.WIDE, "Action Pan"),
                SceneBoundary(1, 2000L, 4500L, CutType.HARD_CUT, 0.96f, FramingType.CLOSE_UP, "Impact Shot"),
                SceneBoundary(2, 4500L, 7500L, CutType.HARD_CUT, 0.95f, FramingType.MEDIUM, "Fast Rotation"),
                SceneBoundary(3, 7500L, 10000L, CutType.HARD_CUT, 0.97f, FramingType.WIDE, "Climax Flash")
            ),
            motionEvents = listOf(
                MotionEvent("gf1", 1900L, 300L, MotionType.CAMERA_SHAKE, 0.9f, 0f, 1.0f, InterpolationCurve.SMOOTH, 0.5f, 0.5f, 0.94f),
                MotionEvent("gf2", 4500L, 400L, MotionType.ROTATION_CW, 0.7f, 0f, 6.0f, InterpolationCurve.EASE_OUT, 0.5f, 0.5f, 0.90f),
                MotionEvent("gf3", 7400L, 400L, MotionType.PUNCH_ZOOM, 0.8f, 1.0f, 1.30f, InterpolationCurve.EASE_OUT, 0.5f, 0.5f, 0.93f)
            ),
            keyframes = listOf(
                TransformKeyframe(0L, 1.0f),
                TransformKeyframe(2000L, 1.20f),
                TransformKeyframe(4500L, 1.15f, rotationDeg = 4f),
                TransformKeyframe(7500L, 1.30f),
                TransformKeyframe(10000L, 1.0f)
            ),
            speedEvents = listOf(
                SpeedEvent(0L, 2000L, 1.2f),
                SpeedEvent(2000L, 3000L, 0.4f, SpeedType.SLOW_MO),
                SpeedEvent(3000L, 4500L, 1.6f, SpeedType.SPEED_RAMP),
                SpeedEvent(4500L, 7500L, 1.0f),
                SpeedEvent(7500L, 10000L, 1.8f, SpeedType.SPEED_RAMP)
            ),
            transitions = listOf(
                TransitionEvent(2000L, 200L, TransitionType.GLITCH, 0.9f, approximationAvailable = true),
                TransitionEvent(4500L, 250L, TransitionType.FLASH, 0.95f),
                TransitionEvent(7500L, 200L, TransitionType.GLITCH, 0.85f, approximationAvailable = true)
            ),
            colorEvents = listOf(
                ColorProfile("electric_cool", "Electric Cyan & Carbon", exposure = 0.05f, contrast = 0.30f, saturation = 0.25f, temperature = -0.15f, tint = 0.08f, highlights = 0.10f, shadows = -0.05f, vignette = 0.35f, grain = 0.08f)
            ),
            unsupportedEffects = listOf(
                UnsupportedEffectInfo("Optical Ray Burst", "Proprietary particle lighting", approximationAvailable = true, userActionChosen = "Approximate")
            ),
            overallStyle = EditStyleProfile("Violent Kinetic", "Aggressive Shake", "Quick Ramped Snaps", "Glitch & Flash", "Electric Blue High Contrast", "Impact Driven"),
            confidences = mapOf("Motion" to 0.94f, "Transitions" to 0.89f, "Speed" to 0.93f, "Color" to 0.95f)
        )
    }
}
