package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.BlueprintJsonHelper
import com.example.data.PresetTemplates
import com.example.data.ProjectRepository
import com.example.data.local.ProjectEntity
import com.example.data.local.TemplateEntity
import com.example.domain.model.*
import com.example.engine.adaptation.AdaptationEngine
import com.example.engine.adaptation.UserMediaAnalyzer
import com.example.engine.analysis.GeminiAnalysisProvider
import com.example.engine.analysis.ReferenceAnalyzer
import com.example.engine.render.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

enum class AppScreen {
    HOME,
    UPLOAD_REFERENCE,
    ANALYZING_REFERENCE,
    EDIT_BLUEPRINT,
    ADD_USER_VIDEO,
    TRANSFER_SETTINGS,
    GENERATING_EDIT,
    RESULT_PREVIEW,
    TEMPLATES,
    SETTINGS
}

enum class ComparisonViewMode {
    RESULT,
    ORIGINAL,
    REFERENCE,
    SPLIT_SLIDER
}

data class AhmedEditsUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    // Reference video
    val referenceVideoUri: String? = null,
    val referenceMetadata: MediaMetadata = MediaMetadata(),
    val analysisStage: AnalysisStage = AnalysisStage.QUEUED,
    val analysisProgress: Int = 0,
    val analysisMessage: String = "",
    val activeBlueprint: EditBlueprint? = null,

    // User video(s)
    val userVideoUris: List<String> = emptyList(),
    val userAnalyses: List<UserVideoAnalysis> = emptyList(),
    val isAutoSelectBestClips: Boolean = true,

    // Transfer settings
    val transferConfig: TransferConfig = TransferConfig(),
    val generationProgress: Int = 0,
    val generationMessage: String = "",

    // Adapted Result & Timeline
    val adaptedTimeline: AdaptedProjectTimeline? = null,
    val activeProjectId: String? = null,
    val activeProjectName: String = "Untitled Edit",

    // Live Player state
    val isPlaying: Boolean = true,
    val playbackTimeMs: Long = 0L,
    val comparisonMode: ComparisonViewMode = ComparisonViewMode.RESULT,
    val splitSliderPosition: Float = 0.5f, // 0.0 to 1.0
    val isSynchronizedDualPlay: Boolean = false,
    val isProTimelineExpanded: Boolean = false,

    // Quick adjustment overrides
    val zoomIntensityOverride: Float = 1.0f,
    val colorIntensityOverride: Float = 1.0f,
    val shakeIntensityOverride: Float = 1.0f,

    // Export dialog
    val isExportDialogOpen: Boolean = false,
    val exportProgress: Int = 0,
    val exportStageMessage: String = "",
    val isExporting: Boolean = false,
    val lastExportResult: ExportResult? = null,

    // Provider & Language
    val useGeminiCloud: Boolean = false,
    val geminiStatusMessage: String = "Local Engine Active",
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val toastMessage: String? = null,

    // Global AI Processing Loading Overlay
    val isAiProcessing: Boolean = false,
    val aiProcessingTitle: String = "AI Video Processing",
    val aiProcessingStatus: String = "Analyzing video footage...",
    val aiProcessingProgress: Int = 0,
    val canCancelAiProcessing: Boolean = false
)

class AhmedEditsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    private val referenceAnalyzer = ReferenceAnalyzer(application)
    private val userMediaAnalyzer = UserMediaAnalyzer(application)
    private val adaptationEngine = AdaptationEngine()
    private val exportEngine = ExportEngine(application)
    private val geminiProvider = GeminiAnalysisProvider()
    val timelineCalculator = RenderTimelineCalculator()

    private val _uiState = MutableStateFlow(AhmedEditsUiState())
    val uiState: StateFlow<AhmedEditsUiState> = _uiState.asStateFlow()

    val allProjects: StateFlow<List<ProjectEntity>>
    val allTemplates: StateFlow<List<TemplateEntity>>

    private var activeAnalysisJob: Job? = null
    private var activeGenerationJob: Job? = null
    private var playbackLoopJob: Job? = null

    // Undo / Redo history stack
    private val undoStack = mutableListOf<AdaptedProjectTimeline>()
    private val redoStack = mutableListOf<AdaptedProjectTimeline>()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProjectRepository(database.projectDao())

        allProjects = repository.allProjects.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allTemplates = repository.allTemplates.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Seed preset templates if empty
        viewModelScope.launch {
            PresetTemplates.getBuiltinTemplates().forEach { tpl ->
                repository.saveTemplate(tpl)
            }
        }

        // Start interactive playback ticker
        startPlaybackTicker()
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setAppLanguage(language: AppLanguage) {
        _uiState.update { it.copy(appLanguage = language) }
    }

    fun toggleGeminiCloud(enabled: Boolean) {
        val configured = geminiProvider.isConfigured()
        val msg = if (enabled && !configured) {
            "Gemini API key not found in secrets. Falling back to On-Device Analysis Engine."
        } else if (enabled) {
            "Gemini AI Cloud Analysis Enabled"
        } else {
            "Local On-Device Engine Active"
        }
        _uiState.update {
            it.copy(
                useGeminiCloud = enabled && configured,
                geminiStatusMessage = msg
            )
        }
    }

    // --- STEP 1: REFERENCE SELECTION ---
    fun selectReferenceVideo(uriString: String, metadata: MediaMetadata? = null) {
        _uiState.update {
            it.copy(
                referenceVideoUri = uriString,
                referenceMetadata = metadata ?: MediaMetadata(durationMs = 15000L, width = 1920, height = 1080)
            )
        }
    }

    // --- STEP 2: ANALYZE REFERENCE VIDEO ---
    fun startReferenceAnalysis() {
        val uri = _uiState.value.referenceVideoUri ?: return
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.ANALYZING_REFERENCE,
                analysisProgress = 0,
                analysisStage = AnalysisStage.QUEUED,
                analysisMessage = "Initializing Analysis Pipeline...",
                isAiProcessing = true,
                aiProcessingTitle = "Deconstructing Reference Video",
                aiProcessingStatus = "Initializing AI analysis pipeline...",
                aiProcessingProgress = 0,
                canCancelAiProcessing = true
            )
        }

        activeAnalysisJob?.cancel()
        activeAnalysisJob = viewModelScope.launch {
            try {
                val blueprint = referenceAnalyzer.analyzeReferenceVideo(uri) { stage, progress, message ->
                    _uiState.update {
                        it.copy(
                            analysisStage = stage,
                            analysisProgress = progress,
                            analysisMessage = message,
                            aiProcessingStatus = message,
                            aiProcessingProgress = progress
                        )
                    }
                }

                _uiState.update {
                    it.copy(
                        activeBlueprint = blueprint,
                        currentScreen = AppScreen.EDIT_BLUEPRINT,
                        referenceMetadata = blueprint.referenceMetadata,
                        isAiProcessing = false
                    )
                }
            } catch (e: CancellationException) {
                _uiState.update {
                    it.copy(
                        currentScreen = AppScreen.UPLOAD_REFERENCE,
                        analysisMessage = "Analysis cancelled.",
                        isAiProcessing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        currentScreen = AppScreen.UPLOAD_REFERENCE,
                        analysisMessage = "Analysis error: ${e.message}",
                        isAiProcessing = false
                    )
                }
            }
        }
    }

    fun cancelAnalysis() {
        activeAnalysisJob?.cancel()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.UPLOAD_REFERENCE,
                isAiProcessing = false
            )
        }
    }

    fun cancelAiProcessing() {
        activeAnalysisJob?.cancel()
        activeGenerationJob?.cancel()
        _uiState.update {
            it.copy(
                isAiProcessing = false,
                currentScreen = when (it.currentScreen) {
                    AppScreen.ANALYZING_REFERENCE -> AppScreen.UPLOAD_REFERENCE
                    AppScreen.GENERATING_EDIT -> AppScreen.TRANSFER_SETTINGS
                    else -> it.currentScreen
                }
            )
        }
    }

    // --- STEP 3: SELECT USER FOOTAGE ---
    fun addUserVideo(uriString: String) {
        _uiState.update {
            it.copy(userVideoUris = it.userVideoUris + uriString)
        }
    }

    fun removeUserVideo(index: Int) {
        _uiState.update {
            val updated = it.userVideoUris.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
            }
            it.copy(userVideoUris = updated)
        }
    }

    fun setUserVideos(uris: List<String>) {
        _uiState.update { it.copy(userVideoUris = uris) }
    }

    // --- STEP 4: TRANSFER SETTINGS & CONFIG ---
    fun setTransferMode(mode: TransferMode) {
        _uiState.update {
            val newConfig = when (mode) {
                TransferMode.FILTER_ONLY -> it.transferConfig.copy(
                    mode = mode,
                    transferMotion = false,
                    transferZoom = false,
                    transferTransitions = false,
                    transferColor = true,
                    transferFilterLook = true,
                    transferTiming = false
                )
                TransferMode.MOTION_ONLY -> it.transferConfig.copy(
                    mode = mode,
                    transferMotion = true,
                    transferZoom = true,
                    transferTransitions = false,
                    transferColor = false,
                    transferFilterLook = false,
                    transferTiming = true
                )
                TransferMode.TRANSITIONS_ONLY -> it.transferConfig.copy(
                    mode = mode,
                    transferMotion = false,
                    transferZoom = false,
                    transferTransitions = true,
                    transferColor = false,
                    transferFilterLook = false,
                    transferTiming = true
                )
                TransferMode.COLOR_ONLY -> it.transferConfig.copy(
                    mode = mode,
                    transferMotion = false,
                    transferZoom = false,
                    transferTransitions = false,
                    transferColor = true,
                    transferFilterLook = true,
                    transferTiming = false
                )
                TransferMode.BEAT_SYNC_ONLY -> it.transferConfig.copy(
                    mode = mode,
                    transferBeatSync = true,
                    transferCuts = true,
                    transferTiming = true
                )
                TransferMode.FULL_EDIT -> it.transferConfig.copy(
                    mode = mode,
                    transferTiming = true,
                    transferCuts = true,
                    transferMotion = true,
                    transferZoom = true,
                    transferRotation = true,
                    transferSpeed = true,
                    transferTransitions = true,
                    transferColor = true,
                    transferFilterLook = true,
                    transferBeatSync = true,
                    transferCameraMotion = true
                )
            }
            it.copy(transferConfig = newConfig)
        }
    }

    fun setMatchStrength(strength: MatchStrength) {
        _uiState.update {
            it.copy(transferConfig = it.transferConfig.copy(matchStrength = strength))
        }
    }

    fun toggleCategory(category: String, isEnabled: Boolean) {
        _uiState.update {
            val c = it.transferConfig
            val updated = when (category) {
                "Timing" -> c.copy(transferTiming = isEnabled)
                "Cuts" -> c.copy(transferCuts = isEnabled)
                "Motion" -> c.copy(transferMotion = isEnabled)
                "Zoom" -> c.copy(transferZoom = isEnabled)
                "Rotation" -> c.copy(transferRotation = isEnabled)
                "Speed" -> c.copy(transferSpeed = isEnabled)
                "Transitions" -> c.copy(transferTransitions = isEnabled)
                "Color" -> c.copy(transferColor = isEnabled)
                "FilterLook" -> c.copy(transferFilterLook = isEnabled)
                "Text" -> c.copy(transferText = isEnabled)
                "Overlays" -> c.copy(transferOverlays = isEnabled)
                "BeatSync" -> c.copy(transferBeatSync = isEnabled)
                "CameraMotion" -> c.copy(transferCameraMotion = isEnabled)
                "SmartCrop" -> c.copy(transferSmartCrop = isEnabled)
                else -> c
            }
            it.copy(transferConfig = updated)
        }
    }

    // --- STEP 5: GENERATE ADAPTED EDIT ---
    fun generateAdaptedEdit() {
        val blueprint = _uiState.value.activeBlueprint ?: return
        val userUris = _uiState.value.userVideoUris.ifEmpty {
            listOf("default_user_clip_1")
        }

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.GENERATING_EDIT,
                generationProgress = 0,
                generationMessage = "Starting User Footage Analysis...",
                isAiProcessing = true,
                aiProcessingTitle = "Synthesizing AI Video Edit",
                aiProcessingStatus = "Analyzing user footage & motion vectors...",
                aiProcessingProgress = 0,
                canCancelAiProcessing = true
            )
        }

        activeGenerationJob?.cancel()
        activeGenerationJob = viewModelScope.launch {
            try {
                // Analyze user media
                val userAnalyses = userMediaAnalyzer.analyzeUserMedia(userUris) { p, msg ->
                    val progress = (p * 0.45).toInt()
                    _uiState.update {
                        it.copy(
                            generationProgress = progress,
                            generationMessage = msg,
                            aiProcessingProgress = progress,
                            aiProcessingStatus = msg
                        )
                    }
                }

                _uiState.update {
                    it.copy(
                        userAnalyses = userAnalyses,
                        generationProgress = 55,
                        generationMessage = "Mapping subject-relative motion patterns...",
                        aiProcessingProgress = 55,
                        aiProcessingStatus = "Mapping subject-relative motion patterns..."
                    )
                }
                delay(180)

                _uiState.update {
                    it.copy(
                        generationProgress = 75,
                        generationMessage = "Synchronizing beats & color curves...",
                        aiProcessingProgress = 75,
                        aiProcessingStatus = "Synchronizing beats & color curves..."
                    )
                }
                delay(180)

                val projectId = UUID.randomUUID().toString()
                val timeline = adaptationEngine.adaptBlueprintToUserFootage(
                    blueprint = blueprint,
                    userAnalyses = userAnalyses,
                    config = _uiState.value.transferConfig,
                    projectId = projectId
                )

                _uiState.update {
                    it.copy(
                        generationProgress = 95,
                        generationMessage = "Building render preview cache...",
                        aiProcessingProgress = 95,
                        aiProcessingStatus = "Building render preview cache..."
                    )
                }
                delay(150)

                // Save to Room
                repository.createProject(
                    name = "Edit from ${blueprint.title}",
                    referenceVideoUri = _uiState.value.referenceVideoUri,
                    userVideoUris = userUris,
                    blueprint = blueprint,
                    transferConfig = _uiState.value.transferConfig,
                    durationMs = timeline.totalDurationMs
                )

                _uiState.update {
                    it.copy(
                        adaptedTimeline = timeline,
                        activeProjectId = projectId,
                        activeProjectName = "Edit from ${blueprint.title}",
                        currentScreen = AppScreen.RESULT_PREVIEW,
                        isPlaying = true,
                        playbackTimeMs = 0L,
                        generationProgress = 100,
                        isAiProcessing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        currentScreen = AppScreen.TRANSFER_SETTINGS,
                        generationMessage = "Error: ${e.message}",
                        isAiProcessing = false
                    )
                }
            }
        }
    }

    // --- STEP 6: PLAYER & COMPARISON CONTROLS ---
    fun togglePlayPause() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun seekTo(timeMs: Long) {
        val total = _uiState.value.adaptedTimeline?.totalDurationMs ?: 15000L
        _uiState.update { it.copy(playbackTimeMs = timeMs.coerceIn(0L, total)) }
    }

    fun stepFrame(forward: Boolean) {
        val frameMs = 33L // approx 1 frame at 30fps
        val current = _uiState.value.playbackTimeMs
        val total = _uiState.value.adaptedTimeline?.totalDurationMs ?: 15000L
        val newTime = if (forward) current + frameMs else current - frameMs
        _uiState.update { it.copy(playbackTimeMs = newTime.coerceIn(0L, total), isPlaying = false) }
    }

    fun setComparisonMode(mode: ComparisonViewMode) {
        _uiState.update { it.copy(comparisonMode = mode) }
    }

    fun setSplitSliderPosition(pos: Float) {
        _uiState.update { it.copy(splitSliderPosition = pos.coerceIn(0.05f, 0.95f)) }
    }

    fun toggleSynchronizedDualPlay() {
        _uiState.update { it.copy(isSynchronizedDualPlay = !it.isSynchronizedDualPlay) }
    }

    fun toggleProTimeline() {
        _uiState.update { it.copy(isProTimelineExpanded = !it.isProTimelineExpanded) }
    }

    // Quick Live Adjustment Sliders
    fun adjustLiveZoom(intensity: Float) {
        _uiState.update { it.copy(zoomIntensityOverride = intensity.coerceIn(0.2f, 2.0f)) }
    }

    fun adjustLiveColor(intensity: Float) {
        _uiState.update { it.copy(colorIntensityOverride = intensity.coerceIn(0.0f, 2.0f)) }
    }

    fun adjustLiveShake(intensity: Float) {
        _uiState.update { it.copy(shakeIntensityOverride = intensity.coerceIn(0.0f, 2.0f)) }
    }

    // --- STEP 7: EXPORT WORKFLOW ---
    fun openExportDialog() {
        _uiState.update { it.copy(isExportDialogOpen = true, isExporting = false, lastExportResult = null) }
    }

    fun closeExportDialog() {
        _uiState.update { it.copy(isExportDialogOpen = false, isExporting = false) }
    }

    fun executeExport(resolution: ExportResolution, fps: ExportFps, quality: ExportQuality) {
        val timeline = _uiState.value.adaptedTimeline ?: return
        _uiState.update {
            it.copy(
                isExporting = true,
                exportProgress = 0,
                exportStageMessage = "Validating timeline and preparing output encoder..."
            )
        }

        viewModelScope.launch {
            val result = exportEngine.renderAndExport(
                timeline = timeline,
                resolution = resolution,
                fps = fps,
                quality = quality
            ) { p, stageMsg ->
                _uiState.update {
                    it.copy(
                        exportProgress = p,
                        exportStageMessage = stageMsg
                    )
                }
            }

            if (result.success) {
                // Record in Room
                repository.recordExport(
                    com.example.data.local.ExportEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = timeline.projectId,
                        projectName = _uiState.value.activeProjectName,
                        filePath = result.outputFilePath ?: "",
                        resolution = result.resolution,
                        fps = result.fps,
                        quality = result.quality,
                        durationMs = result.durationMs,
                        sizeBytes = result.fileSizeBytes
                    )
                )
            }

            _uiState.update {
                it.copy(
                    isExporting = false,
                    lastExportResult = result
                )
            }
        }
    }

    // Save as Reusable Template
    fun saveAsTemplate(name: String, category: String = "Custom") {
        val blueprint = _uiState.value.activeBlueprint ?: return
        viewModelScope.launch {
            val entity = TemplateEntity(
                id = "tpl_${UUID.randomUUID()}",
                name = name,
                description = "Saved from project ${_uiState.value.activeProjectName}",
                category = category,
                blueprintJson = BlueprintJsonHelper.blueprintToJson(blueprint),
                thumbnailResName = "ic_custom_template"
            )
            repository.saveTemplate(entity)
            showToast("Template saved: $name")
        }
    }

    fun loadTemplate(template: TemplateEntity) {
        val bp = BlueprintJsonHelper.jsonToBlueprint(template.blueprintJson)
        _uiState.update {
            it.copy(
                activeBlueprint = bp,
                referenceVideoUri = null,
                referenceMetadata = bp.referenceMetadata,
                currentScreen = AppScreen.EDIT_BLUEPRINT
            )
        }
    }

    fun loadProject(project: ProjectEntity) {
        val bp = project.blueprintJson?.let { BlueprintJsonHelper.jsonToBlueprint(it) }
        val userUris = BlueprintJsonHelper.jsonToStringList(project.userVideoUrisJson)
        val config = BlueprintJsonHelper.jsonToTransferConfig(project.transferConfigJson)

        _uiState.update {
            it.copy(
                activeProjectId = project.id,
                activeProjectName = project.name,
                referenceVideoUri = project.referenceVideoUri,
                activeBlueprint = bp,
                userVideoUris = userUris,
                transferConfig = config,
                currentScreen = if (bp != null) AppScreen.EDIT_BLUEPRINT else AppScreen.UPLOAD_REFERENCE
            )
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            showToast("Project deleted")
        }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
        viewModelScope.launch {
            delay(2500)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }

    // Background playback ticker loop for interactive video preview
    private fun startPlaybackTicker() {
        playbackLoopJob?.cancel()
        playbackLoopJob = viewModelScope.launch(Dispatchers.Default) {
            val frameIntervalMs = 33L // ~30fps
            while (isActive) {
                delay(frameIntervalMs)
                if (_uiState.value.isPlaying && _uiState.value.adaptedTimeline != null) {
                    val total = _uiState.value.adaptedTimeline?.totalDurationMs ?: 15000L
                    _uiState.update { current ->
                        val next = current.playbackTimeMs + frameIntervalMs
                        val looped = if (next >= total) 0L else next
                        current.copy(playbackTimeMs = looped)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackLoopJob?.cancel()
        activeAnalysisJob?.cancel()
        activeGenerationJob?.cancel()
    }
}
