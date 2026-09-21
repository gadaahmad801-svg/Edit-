package com.example.engine.adaptation

import android.content.Context
import com.example.domain.model.*
import com.example.engine.analysis.MetadataAnalyzer
import com.example.engine.analysis.SceneDetector
import com.example.engine.analysis.SubjectTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserMediaAnalyzer(private val context: Context) {

    private val metadataAnalyzer = MetadataAnalyzer(context)
    private val sceneDetector = SceneDetector(context)
    private val subjectTracker = SubjectTracker()

    suspend fun analyzeUserMedia(
        mediaUris: List<String>,
        onProgress: (Int, String) -> Unit = { _, _ -> }
    ): List<UserVideoAnalysis> = withContext(Dispatchers.IO) {
        val analyses = mutableListOf<UserVideoAnalysis>()
        val total = mediaUris.size.coerceAtLeast(1)

        mediaUris.forEachIndexed { idx, uri ->
            val p = ((idx.toFloat() / total) * 100).toInt()
            onProgress(p, "Analyzing user clip ${idx + 1} of $total...")

            val metadata = metadataAnalyzer.extractMetadata(uri)
            val scenes = sceneDetector.detectScenes(uri, metadata)
            val subjects = subjectTracker.trackSubjects(metadata)

            val avgX = if (subjects.isNotEmpty()) subjects.map { it.centerX }.average().toFloat() else 0.5f
            val avgY = if (subjects.isNotEmpty()) subjects.map { it.centerY }.average().toFloat() else 0.48f

            analyses.add(
                UserVideoAnalysis(
                    mediaUri = uri,
                    metadata = metadata,
                    detectedScenes = scenes,
                    trackedSubjects = subjects,
                    averageSubjectCenter = Pair(avgX, avgY),
                    motionVariance = 0.45f,
                    orientation = metadata.aspectRatio
                )
            )
        }

        onProgress(100, "User media analysis complete!")
        return@withContext analyses
    }
}
