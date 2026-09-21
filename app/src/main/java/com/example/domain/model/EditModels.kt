package com.example.domain.model

enum class CutType {
    HARD_CUT,
    SOFT_CUT,
    DISSOLVE,
    FADE_BLACK,
    FADE_WHITE,
    UNKNOWN
}

enum class FramingType {
    WIDE,
    MEDIUM,
    CLOSE_UP,
    EXTREME_CLOSE_UP
}

enum class MotionType {
    ZOOM_IN,
    ZOOM_OUT,
    PUNCH_ZOOM,
    PAN_LEFT,
    PAN_RIGHT,
    TILT_UP,
    TILT_DOWN,
    CAMERA_SHAKE,
    ROTATION_CW,
    ROTATION_CCW,
    SUBJECT_TRACKING
}

enum class InterpolationCurve {
    LINEAR,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT,
    SMOOTH
}

enum class TransitionType {
    CUT,
    FADE_BLACK,
    FADE_WHITE,
    CROSSFADE,
    DISSOLVE,
    FLASH,
    ZOOM_BLUR,
    SLIDE_LEFT,
    SLIDE_RIGHT,
    GLITCH,
    ROTATE
}

enum class SpeedType {
    NORMAL,
    SLOW_MO,
    SPEED_RAMP,
    FREEZE_FRAME
}

enum class TransferMode(val displayName: String) {
    FULL_EDIT("Full Edit Recreate"),
    FILTER_ONLY("Apply Filter / Look Only"),
    MOTION_ONLY("Motion & Zoom Only"),
    TRANSITIONS_ONLY("Transitions Only"),
    COLOR_ONLY("Color Grade Only"),
    BEAT_SYNC_ONLY("Beat Sync Only")
}

enum class MatchStrength(val multiplier: Float, val label: String) {
    LIGHT(0.25f, "Light 25%"),
    BALANCED(0.50f, "Balanced 50%"),
    STRONG(0.75f, "Strong 75%"),
    MAXIMUM(1.00f, "Maximum 100%")
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी (Hindi)"),
    HINGLISH("hinglish", "Hinglish")
}

data class MediaMetadata(
    val durationMs: Long = 0L,
    val width: Int = 1920,
    val height: Int = 1080,
    val fps: Int = 30,
    val bitRate: Long = 0L,
    val hasAudio: Boolean = true,
    val mimeType: String = "video/mp4",
    val orientation: Int = 0,
    val fileSize: Long = 0L
) {
    val aspectRatio: String
        get() = when {
            height <= 0 -> "16:9"
            width * 9 == height * 16 -> "16:9"
            width * 16 == height * 9 -> "9:16"
            width == height -> "1:1"
            width * 5 == height * 4 -> "4:5"
            width < height -> "9:16"
            else -> "16:9"
        }

    val durationFormatted: String
        get() {
            val totalSec = (durationMs / 1000).toInt()
            val m = totalSec / 60
            val s = totalSec % 60
            val ms = (durationMs % 1000) / 100
            return String.format("%02d:%02d.%d", m, s, ms)
        }
}

data class SceneBoundary(
    val sceneIndex: Int,
    val startMs: Long,
    val endMs: Long,
    val cutType: CutType = CutType.HARD_CUT,
    val cutConfidence: Float = 0.92f,
    val framingType: FramingType = FramingType.MEDIUM,
    val dominantMotionDirection: String = "Static",
    val brightness: Float = 0.5f,
    val colorProfileId: String = "default"
) {
    val durationMs: Long get() = (endMs - startMs).coerceAtLeast(0L)
}

data class MotionEvent(
    val id: String,
    val timestampMs: Long,
    val durationMs: Long,
    val type: MotionType,
    val intensity: Float = 0.5f,
    val startValue: Float = 1.0f,
    val endValue: Float = 1.2f,
    val curve: InterpolationCurve = InterpolationCurve.SMOOTH,
    val normalizedTargetX: Float = 0.5f,
    val normalizedTargetY: Float = 0.5f,
    val confidence: Float = 0.90f
)

data class TransformKeyframe(
    val timestampMs: Long,
    val scale: Float = 1.0f,
    val translationX: Float = 0.0f,
    val translationY: Float = 0.0f,
    val rotationDeg: Float = 0.0f,
    val opacity: Float = 1.0f,
    val curve: InterpolationCurve = InterpolationCurve.SMOOTH
)

data class SpeedEvent(
    val startMs: Long,
    val endMs: Long,
    val speedMultiplier: Float = 1.0f,
    val type: SpeedType = SpeedType.NORMAL,
    val curve: InterpolationCurve = InterpolationCurve.SMOOTH
)

data class TransitionEvent(
    val atTimestampMs: Long,
    val durationMs: Long = 400L,
    val type: TransitionType = TransitionType.CUT,
    val intensity: Float = 0.8f,
    val approximationAvailable: Boolean = true,
    val confidence: Float = 0.88f
)

data class ColorProfile(
    val id: String = "look_1",
    val name: String = "Reference Grade",
    val exposure: Float = 0.0f,       // -1.0 to 1.0
    val contrast: Float = 0.15f,      // -1.0 to 1.0
    val saturation: Float = 0.20f,    // -1.0 to 1.0
    val temperature: Float = 0.05f,   // -1.0 (cool/blue) to 1.0 (warm/amber)
    val tint: Float = 0.0f,           // -1.0 (green) to 1.0 (magenta)
    val highlights: Float = -0.10f,   // -1.0 to 1.0
    val shadows: Float = 0.08f,       // -1.0 to 1.0
    val vignette: Float = 0.25f,      // 0.0 to 1.0
    val grain: Float = 0.06f,         // 0.0 to 1.0
    val hueShiftDeg: Float = 0.0f
)

data class TextEvent(
    val id: String,
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val normalizedX: Float = 0.5f,
    val normalizedY: Float = 0.8f,
    val scale: Float = 1.0f,
    val rotationDeg: Float = 0.0f,
    val colorHex: String = "#FFFFFF",
    val animationType: String = "Fade In",
    val isEditable: Boolean = true
)

data class OverlayEvent(
    val id: String,
    val label: String,
    val startMs: Long,
    val endMs: Long,
    val normalizedX: Float = 0.5f,
    val normalizedY: Float = 0.5f,
    val scale: Float = 1.0f,
    val opacity: Float = 0.8f,
    val blendMode: String = "Screen",
    val extractionQualityPercent: Int = 85
)

data class AudioBeat(
    val timestampMs: Long,
    val energy: Float = 0.8f,
    val isMajorDrop: Boolean = false,
    val isKick: Boolean = true
)

data class AudioMap(
    val bpm: Int = 124,
    val beats: List<AudioBeat> = emptyList(),
    val drops: List<Long> = emptyList(),
    val isAudioPresent: Boolean = true
)

data class EditStyleProfile(
    val pace: String = "Fast Dynamic",
    val motionIntensity: String = "High Motion",
    val zoomIntensity: String = "Medium Punch",
    val transitionAggression: String = "Aggressive",
    val colorStyle: String = "High Contrast Cinematic",
    val beatSyncStrength: String = "Strong Locked"
)

data class UnsupportedEffectInfo(
    val effectName: String,
    val reason: String,
    val approximationAvailable: Boolean,
    val userActionChosen: String = "Approximate" // "Approximate", "Omit", "KeepOriginal"
)

data class EditBlueprint(
    val id: String,
    val version: String = "1.0",
    val title: String = "Untitled Blueprint",
    val createdAt: Long = System.currentTimeMillis(),
    val referenceMetadata: MediaMetadata = MediaMetadata(),
    val scenes: List<SceneBoundary> = emptyList(),
    val motionEvents: List<MotionEvent> = emptyList(),
    val keyframes: List<TransformKeyframe> = emptyList(),
    val speedEvents: List<SpeedEvent> = emptyList(),
    val transitions: List<TransitionEvent> = emptyList(),
    val colorEvents: List<ColorProfile> = emptyList(),
    val textEvents: List<TextEvent> = emptyList(),
    val overlayEvents: List<OverlayEvent> = emptyList(),
    val audioMap: AudioMap = AudioMap(),
    val overallStyle: EditStyleProfile = EditStyleProfile(),
    val confidences: Map<String, Float> = emptyMap(),
    val unsupportedEffects: List<UnsupportedEffectInfo> = emptyList()
) {
    val totalScenes: Int get() = scenes.size
    val totalMotionEvents: Int get() = motionEvents.size
    val totalZooms: Int get() = motionEvents.count { it.type == MotionType.ZOOM_IN || it.type == MotionType.ZOOM_OUT || it.type == MotionType.PUNCH_ZOOM }
    val totalTransitions: Int get() = transitions.size
    val totalSpeedEvents: Int get() = speedEvents.size
    val totalColorProfiles: Int get() = colorEvents.size
    val totalTextEvents: Int get() = textEvents.size
    val totalAudioEvents: Int get() = audioMap.beats.size

    val overallMatchScore: Int
        get() {
            val scores = confidences.values
            if (scores.isEmpty()) return 88
            return (scores.average() * 100).toInt().coerceIn(60, 99)
        }
}

data class SubjectTrackingPoint(
    val timestampMs: Long,
    val centerX: Float,
    val centerY: Float,
    val width: Float,
    val height: Float,
    val subjectLabel: String = "Primary Subject",
    val confidence: Float = 0.94f
)

data class UserVideoAnalysis(
    val mediaUri: String,
    val metadata: MediaMetadata,
    val detectedScenes: List<SceneBoundary>,
    val trackedSubjects: List<SubjectTrackingPoint>,
    val averageSubjectCenter: Pair<Float, Float>,
    val motionVariance: Float,
    val orientation: String
)

data class TransferConfig(
    val transferTiming: Boolean = true,
    val transferCuts: Boolean = true,
    val transferMotion: Boolean = true,
    val transferZoom: Boolean = true,
    val transferRotation: Boolean = true,
    val transferSpeed: Boolean = true,
    val transferTransitions: Boolean = true,
    val transferColor: Boolean = true,
    val transferFilterLook: Boolean = true,
    val transferText: Boolean = false,
    val transferOverlays: Boolean = false,
    val transferBeatSync: Boolean = true,
    val transferCameraMotion: Boolean = true,
    val transferSmartCrop: Boolean = true,
    val matchStrength: MatchStrength = MatchStrength.BALANCED,
    val mode: TransferMode = TransferMode.FULL_EDIT
)

data class AdaptedProjectTimeline(
    val projectId: String,
    val sourceBlueprintId: String,
    val userVideoUris: List<String>,
    val totalDurationMs: Long,
    val adaptedScenes: List<SceneBoundary>,
    val adaptedKeyframes: List<TransformKeyframe>,
    val adaptedTransitions: List<TransitionEvent>,
    val adaptedColorProfile: ColorProfile,
    val adaptedSpeedEvents: List<SpeedEvent>,
    val adaptedTextEvents: List<TextEvent>,
    val subjectOffset: Pair<Float, Float> = Pair(0f, 0f),
    val audioBeatSyncOffsets: List<Long> = emptyList(),
    val transferConfig: TransferConfig
)

data class AnalysisJob(
    val jobId: String,
    val videoUri: String,
    val currentStage: AnalysisStage = AnalysisStage.QUEUED,
    val progressPercent: Int = 0,
    val isCompleted: Boolean = false,
    val error: String? = null,
    val stagesFinished: List<String> = emptyList()
)

enum class AnalysisStage(val label: String) {
    QUEUED("In Queue"),
    VALIDATING_MEDIA("Validating Media & Generating Proxy"),
    METADATA("Analyzing Metadata & Streams"),
    SCENE_DETECTION("Detecting Scene Boundaries & Cuts"),
    SUBJECT_TRACKING("Tracking Primary Subject"),
    MOTION_ANALYSIS("Extracting Camera Motion & Keyframes"),
    SPEED_RAMPS("Detecting Speed Changes & Ramps"),
    COLOR_PROFILING("Analyzing Color Grading & Look"),
    TRANSITION_RECREATION("Classifying Transitions"),
    BEAT_SYNCHRONIZATION("Analyzing Audio Beats & Visual Rhythm"),
    BLUEPRINT_GENERATION("Generating Structured Edit Blueprint"),
    DONE("Analysis Complete")
}
