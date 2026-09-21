package com.example.engine.analysis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.domain.model.ColorProfile
import com.example.domain.model.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ColorAnalyzer(private val context: Context) {

    suspend fun analyzeColorProfile(
        uriString: String,
        metadata: MediaMetadata,
        onProgress: (Float) -> Unit = {}
    ): List<ColorProfile> = withContext(Dispatchers.IO) {
        val totalMs = metadata.durationMs.coerceAtLeast(2000L)
        val retriever = MediaMetadataRetriever()
        var hasLoadedSource = false

        try {
            if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                val uri = Uri.parse(uriString)
                context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                    retriever.setDataSource(pfd.fileDescriptor)
                    hasLoadedSource = true
                }
            } else if (uriString.startsWith("/")) {
                retriever.setDataSource(uriString)
                hasLoadedSource = true
            }
        } catch (_: Exception) {
            hasLoadedSource = false
        }

        onProgress(0.2f)

        // Sample 3 key representative timestamps (start, middle, late)
        val sampleTimestamps = listOf(totalMs / 4, totalMs / 2, (totalMs * 3) / 4)
        var sumExposure = 0.0
        var sumContrast = 0.0
        var sumSaturation = 0.0
        var sumTemperature = 0.0
        var sumTint = 0.0
        var sumVignette = 0.0
        var sumGrain = 0.0
        var validSamples = 0

        for ((idx, tMs) in sampleTimestamps.withIndex()) {
            onProgress(0.3f + (idx * 0.2f))
            if (hasLoadedSource) {
                try {
                    val frame = retriever.getFrameAtTime(tMs * 1000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    if (frame != null) {
                        val stats = analyzeFrameColors(frame)
                        sumExposure += stats.exposure
                        sumContrast += stats.contrast
                        sumSaturation += stats.saturation
                        sumTemperature += stats.temperature
                        sumTint += stats.tint
                        sumVignette += stats.vignette
                        sumGrain += stats.grain
                        validSamples++
                        frame.recycle()
                    }
                } catch (_: Exception) {}
            }
        }

        try {
            retriever.release()
        } catch (_: Exception) {}

        val profile = if (validSamples > 0) {
            ColorProfile(
                id = "ref_look_detected",
                name = "Reference Look Match",
                exposure = (sumExposure / validSamples).toFloat().coerceIn(-0.5f, 0.5f),
                contrast = (sumContrast / validSamples).toFloat().coerceIn(-0.4f, 0.6f),
                saturation = (sumSaturation / validSamples).toFloat().coerceIn(-0.5f, 0.8f),
                temperature = (sumTemperature / validSamples).toFloat().coerceIn(-0.4f, 0.4f),
                tint = (sumTint / validSamples).toFloat().coerceIn(-0.3f, 0.3f),
                highlights = -0.10f,
                shadows = 0.08f,
                vignette = (sumVignette / validSamples).toFloat().coerceIn(0.0f, 0.6f),
                grain = (sumGrain / validSamples).toFloat().coerceIn(0.0f, 0.3f),
                hueShiftDeg = 0.0f
            )
        } else {
            // High fidelity default cinematic profile
            ColorProfile(
                id = "ref_look_default",
                name = "Cinematic High Dynamic Look",
                exposure = 0.05f,
                contrast = 0.22f,
                saturation = 0.20f,
                temperature = 0.08f,
                tint = -0.02f,
                highlights = -0.12f,
                shadows = 0.09f,
                vignette = 0.28f,
                grain = 0.05f,
                hueShiftDeg = 0.0f
            )
        }

        onProgress(1.0f)
        return@withContext listOf(profile)
    }

    private data class FrameColorStats(
        val exposure: Float,
        val contrast: Float,
        val saturation: Float,
        val temperature: Float,
        val tint: Float,
        val vignette: Float,
        val grain: Float
    )

    private fun analyzeFrameColors(bitmap: Bitmap): FrameColorStats {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= 0 || h <= 0) {
            return FrameColorStats(0f, 0.15f, 0.2f, 0.05f, 0f, 0.2f, 0.05f)
        }

        val step = 10
        var totalLum = 0.0
        var totalR = 0.0
        var totalG = 0.0
        var totalB = 0.0
        var totalSat = 0.0
        var centerLum = 0.0
        var cornerLum = 0.0
        var centerCount = 0
        var cornerCount = 0
        var pixelCount = 0

        for (x in 0 until w step (w / step).coerceAtLeast(1)) {
            for (y in 0 until h step (h / step).coerceAtLeast(1)) {
                val color = bitmap.getPixel(x, y)
                val r = Color.red(color) / 255f
                val g = Color.green(color) / 255f
                val b = Color.blue(color) / 255f
                val lum = 0.299f * r + 0.587f * g + 0.114f * b

                val maxC = max(r, max(g, b))
                val minC = min(r, min(g, b))
                val sat = if (maxC > 0f) (maxC - minC) / maxC else 0f

                totalLum += lum
                totalR += r
                totalG += g
                totalB += b
                totalSat += sat
                pixelCount++

                val normX = x.toFloat() / w
                val normY = y.toFloat() / h
                val distFromCenter = abs(normX - 0.5f) + abs(normY - 0.5f)
                if (distFromCenter < 0.35f) {
                    centerLum += lum
                    centerCount++
                } else if (distFromCenter > 0.65f) {
                    cornerLum += lum
                    cornerCount++
                }
            }
        }

        val count = pixelCount.coerceAtLeast(1)
        val avgLum = (totalLum / count).toFloat()
        val avgR = (totalR / count).toFloat()
        val avgG = (totalG / count).toFloat()
        val avgB = (totalB / count).toFloat()
        val avgSat = (totalSat / count).toFloat()

        // Exposure: offset from 0.5 mid-gray
        val exposure = (avgLum - 0.5f) * 0.8f

        // Contrast: deviation from middle
        val contrast = (abs(avgLum - 0.5f) * 0.5f + 0.15f)

        // Saturation adjustment: difference from standard ~0.35 saturation
        val satAdjustment = (avgSat - 0.35f) * 0.7f

        // Temperature: warm (R > B) vs cool (B > R)
        val temperature = ((avgR - avgB) * 0.6f)

        // Tint: green vs magenta
        val tint = ((avgG - (avgR + avgB) / 2f) * -0.5f)

        // Vignette: center brightness vs corner darkness
        val avgCenter = if (centerCount > 0) (centerLum / centerCount).toFloat() else avgLum
        val avgCorner = if (cornerCount > 0) (cornerLum / cornerCount).toFloat() else avgLum
        val vignette = ((avgCenter - avgCorner).coerceAtLeast(0f) * 1.2f).coerceIn(0f, 0.6f)

        return FrameColorStats(
            exposure = exposure,
            contrast = contrast,
            saturation = satAdjustment,
            temperature = temperature,
            tint = tint,
            vignette = vignette,
            grain = 0.05f
        )
    }
}
