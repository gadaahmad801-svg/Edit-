package com.example.engine.analysis

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.domain.model.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MetadataAnalyzer(private val context: Context) {

    suspend fun extractMetadata(uriString: String): MediaMetadata = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                val uri = Uri.parse(uriString)
                context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                    retriever.setDataSource(pfd.fileDescriptor)
                }
            } else if (uriString.startsWith("/")) {
                retriever.setDataSource(uriString)
            } else {
                // Preset or demo resource
                return@withContext MediaMetadata(
                    durationMs = 15000L,
                    width = 1920,
                    height = 1080,
                    fps = 30,
                    hasAudio = true,
                    mimeType = "video/mp4"
                )
            }

            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
            val hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
            val mimeStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)

            val durationMs = durationStr?.toLongOrNull() ?: 15000L
            var width = widthStr?.toIntOrNull() ?: 1920
            var height = heightStr?.toIntOrNull() ?: 1080
            val rotation = rotationStr?.toIntOrNull() ?: 0

            // If video is rotated 90 or 270 deg (vertical portrait from phone camera), swap width and height
            if (rotation == 90 || rotation == 270) {
                val temp = width
                width = height
                height = temp
            }

            MediaMetadata(
                durationMs = durationMs.coerceAtLeast(1000L),
                width = width,
                height = height,
                fps = 30, // Default baseline or estimated
                bitRate = bitrateStr?.toLongOrNull() ?: 0L,
                hasAudio = hasAudioStr == "yes",
                mimeType = mimeStr ?: "video/mp4",
                orientation = rotation
            )
        } catch (_: Exception) {
            // Robust fallback if retriever fails on virtual uri
            MediaMetadata(
                durationMs = 15000L,
                width = 1920,
                height = 1080,
                fps = 30,
                hasAudio = true,
                mimeType = "video/mp4"
            )
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }
    }
}
