package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.components.GlobalAiLoadingOverlay
import com.example.ui.screens.*
import com.example.ui.theme.AhmedEditsTheme
import com.example.ui.viewmodel.AhmedEditsViewModel
import com.example.ui.viewmodel.AppScreen

class MainActivity : ComponentActivity() {

    private val viewModel: AhmedEditsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AhmedEditsTheme {
                val state by viewModel.uiState.collectAsState()
                val projects by viewModel.allProjects.collectAsState()
                val templates by viewModel.allTemplates.collectAsState()

                // Display toast notifications
                LaunchedEffect(state.toastMessage) {
                    state.toastMessage?.let {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                    }
                }

                // Handle system back navigation
                BackHandler(enabled = state.isAiProcessing || state.currentScreen != AppScreen.HOME) {
                    if (state.isAiProcessing && state.canCancelAiProcessing) {
                        viewModel.cancelAiProcessing()
                        return@BackHandler
                    }
                    when (state.currentScreen) {
                        AppScreen.ANALYZING_REFERENCE -> viewModel.cancelAnalysis()
                        AppScreen.EDIT_BLUEPRINT -> viewModel.navigateTo(AppScreen.UPLOAD_REFERENCE)
                        AppScreen.ADD_USER_VIDEO -> viewModel.navigateTo(AppScreen.EDIT_BLUEPRINT)
                        AppScreen.TRANSFER_SETTINGS -> viewModel.navigateTo(AppScreen.ADD_USER_VIDEO)
                        AppScreen.RESULT_PREVIEW -> viewModel.navigateTo(AppScreen.TRANSFER_SETTINGS)
                        AppScreen.TEMPLATES, AppScreen.SETTINGS, AppScreen.UPLOAD_REFERENCE -> viewModel.navigateTo(AppScreen.HOME)
                        else -> viewModel.navigateTo(AppScreen.HOME)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = state.currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "screen_navigation"
                    ) { targetScreen ->
                        when (targetScreen) {
                            AppScreen.HOME -> {
                                HomeScreen(
                                    projects = projects,
                                    templates = templates,
                                    onStartNewEdit = { viewModel.navigateTo(AppScreen.UPLOAD_REFERENCE) },
                                    onSelectMode = { mode ->
                                        viewModel.setTransferMode(mode)
                                        viewModel.navigateTo(AppScreen.UPLOAD_REFERENCE)
                                    },
                                    onSelectTemplate = { template ->
                                        viewModel.loadTemplate(template)
                                    },
                                    onSelectProject = { project ->
                                        viewModel.loadProject(project)
                                    },
                                    onDeleteProject = { id ->
                                        viewModel.deleteProject(id)
                                    },
                                    onOpenTemplates = { viewModel.navigateTo(AppScreen.TEMPLATES) },
                                    onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) }
                                )
                            }

                            AppScreen.UPLOAD_REFERENCE -> {
                                UploadReferenceScreen(
                                    selectedUri = state.referenceVideoUri,
                                    metadata = state.referenceMetadata,
                                    onVideoSelected = { uri, meta ->
                                        viewModel.selectReferenceVideo(uri, meta)
                                    },
                                    onStartAnalysis = {
                                        viewModel.startReferenceAnalysis()
                                    },
                                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                                )
                            }

                            AppScreen.ANALYZING_REFERENCE -> {
                                AnalysisProgressScreen(
                                    currentStage = state.analysisStage,
                                    progressPercent = state.analysisProgress,
                                    statusMessage = state.analysisMessage,
                                    onCancel = { viewModel.cancelAnalysis() }
                                )
                            }

                            AppScreen.EDIT_BLUEPRINT -> {
                                state.activeBlueprint?.let { blueprint ->
                                    EditBlueprintScreen(
                                        blueprint = blueprint,
                                        onApplyToMyVideo = {
                                            viewModel.navigateTo(AppScreen.ADD_USER_VIDEO)
                                        },
                                        onBack = { viewModel.navigateTo(AppScreen.UPLOAD_REFERENCE) }
                                    )
                                } ?: viewModel.navigateTo(AppScreen.UPLOAD_REFERENCE)
                            }

                            AppScreen.ADD_USER_VIDEO -> {
                                AddUserVideoScreen(
                                    userVideoUris = state.userVideoUris,
                                    onAddVideo = { uri -> viewModel.addUserVideo(uri) },
                                    onRemoveVideo = { idx -> viewModel.removeUserVideo(idx) },
                                    onContinue = { viewModel.navigateTo(AppScreen.TRANSFER_SETTINGS) },
                                    onBack = { viewModel.navigateTo(AppScreen.EDIT_BLUEPRINT) }
                                )
                            }

                            AppScreen.TRANSFER_SETTINGS -> {
                                TransferSettingsScreen(
                                    config = state.transferConfig,
                                    onSetMode = { mode -> viewModel.setTransferMode(mode) },
                                    onSetStrength = { str -> viewModel.setMatchStrength(str) },
                                    onToggleCategory = { cat, enabled -> viewModel.toggleCategory(cat, enabled) },
                                    onGenerateEdit = { viewModel.generateAdaptedEdit() },
                                    onBack = { viewModel.navigateTo(AppScreen.ADD_USER_VIDEO) }
                                )
                            }

                            AppScreen.GENERATING_EDIT -> {
                                GeneratingEditScreen(
                                    progressPercent = state.generationProgress,
                                    statusMessage = state.generationMessage
                                )
                            }

                            AppScreen.RESULT_PREVIEW -> {
                                state.adaptedTimeline?.let { timeline ->
                                    ResultPreviewScreen(
                                        timeline = timeline,
                                        projectName = state.activeProjectName,
                                        isPlaying = state.isPlaying,
                                        playbackTimeMs = state.playbackTimeMs,
                                        comparisonMode = state.comparisonMode,
                                        splitSliderPosition = state.splitSliderPosition,
                                        isSynchronizedDualPlay = state.isSynchronizedDualPlay,
                                        isProTimelineExpanded = state.isProTimelineExpanded,
                                        zoomOverride = state.zoomIntensityOverride,
                                        colorOverride = state.colorIntensityOverride,
                                        shakeOverride = state.shakeIntensityOverride,
                                        timelineCalculator = viewModel.timelineCalculator,
                                        onTogglePlayPause = { viewModel.togglePlayPause() },
                                        onSeekTo = { time -> viewModel.seekTo(time) },
                                        onStepFrame = { forward -> viewModel.stepFrame(forward) },
                                        onSetComparisonMode = { mode -> viewModel.setComparisonMode(mode) },
                                        onSetSplitSliderPosition = { pos -> viewModel.setSplitSliderPosition(pos) },
                                        onToggleSynchronizedDualPlay = { viewModel.toggleSynchronizedDualPlay() },
                                        onToggleProTimeline = { viewModel.toggleProTimeline() },
                                        onAdjustZoom = { z -> viewModel.adjustLiveZoom(z) },
                                        onAdjustColor = { c -> viewModel.adjustLiveColor(c) },
                                        onAdjustShake = { s -> viewModel.adjustLiveShake(s) },
                                        onOpenExport = { viewModel.openExportDialog() },
                                        onSaveTemplate = { name -> viewModel.saveAsTemplate(name) },
                                        onBack = { viewModel.navigateTo(AppScreen.TRANSFER_SETTINGS) }
                                    )
                                } ?: viewModel.navigateTo(AppScreen.HOME)
                            }

                            AppScreen.TEMPLATES -> {
                                TemplatesScreen(
                                    templates = templates,
                                    onSelectTemplate = { tpl -> viewModel.loadTemplate(tpl) },
                                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                                )
                            }

                            AppScreen.SETTINGS -> {
                                SettingsScreen(
                                    useGeminiCloud = state.useGeminiCloud,
                                    geminiStatusMessage = state.geminiStatusMessage,
                                    currentLanguage = state.appLanguage,
                                    onToggleGemini = { viewModel.toggleGeminiCloud(it) },
                                    onSelectLanguage = { viewModel.setAppLanguage(it) },
                                    onClearCache = { viewModel.showToast("Proxy frame cache cleared") },
                                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                                )
                            }
                        }
                    }

                    // Export BottomSheet Modal
                    if (state.isExportDialogOpen) {
                        ExportModalSheet(
                            isExporting = state.isExporting,
                            exportProgress = state.exportProgress,
                            exportStageMessage = state.exportStageMessage,
                            lastExportResult = state.lastExportResult,
                            onStartExport = { res, fps, q -> viewModel.executeExport(res, fps, q) },
                            onDismiss = { viewModel.closeExportDialog() }
                        )
                    }

                    // Global AI Video Processing Loading Animation Overlay
                    GlobalAiLoadingOverlay(
                        visible = state.isAiProcessing,
                        title = state.aiProcessingTitle,
                        statusMessage = state.aiProcessingStatus,
                        progress = state.aiProcessingProgress,
                        canCancel = state.canCancelAiProcessing,
                        onCancel = { viewModel.cancelAiProcessing() }
                    )
                }
            }
        }
    }
}
