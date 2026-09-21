package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlueprintJsonHelper
import com.example.data.PresetTemplates
import com.example.domain.model.*
import com.example.engine.adaptation.AdaptationEngine
import com.example.engine.render.QualityChecker
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ahmed Edits", appName)
    }

    @Test
    fun `test preset templates generation and json serialization`() {
        val templates = PresetTemplates.getBuiltinTemplates()
        assertTrue("Preset templates should not be empty", templates.isNotEmpty())

        val first = templates.first()
        assertNotNull(first.name)
        assertNotNull(first.blueprintJson)

        val parsedBlueprint = BlueprintJsonHelper.jsonToBlueprint(first.blueprintJson)
        assertEquals(first.name, parsedBlueprint.title)
        assertTrue(parsedBlueprint.scenes.isNotEmpty())
        assertTrue(parsedBlueprint.keyframes.isNotEmpty())
    }

    @Test
    fun `test adaptation engine scales keyframes and quality checks`() = runBlocking {
        val templates = PresetTemplates.getBuiltinTemplates()
        val bp = BlueprintJsonHelper.jsonToBlueprint(templates.first().blueprintJson)

        val userAnalysis = UserVideoAnalysis(
            mediaUri = "test_user_clip_1",
            metadata = MediaMetadata(durationMs = 10000L, width = 1080, height = 1920),
            detectedScenes = bp.scenes,
            trackedSubjects = emptyList(),
            averageSubjectCenter = Pair(0.42f, 0.55f),
            motionVariance = 0.5f,
            orientation = "9:16"
        )

        val engine = AdaptationEngine()
        val timeline = engine.adaptBlueprintToUserFootage(
            blueprint = bp,
            userAnalyses = listOf(userAnalysis),
            config = TransferConfig(matchStrength = MatchStrength.STRONG),
            projectId = "test_proj_1"
        )

        assertNotNull(timeline)
        assertEquals(10000L, timeline.totalDurationMs)
        assertTrue(timeline.adaptedKeyframes.isNotEmpty())

        val checker = QualityChecker()
        val check = checker.validateTimelinePreExport(timeline, 1080, 1920, 30)
        assertTrue("Timeline should pass quality check", check.isValid)
        assertEquals(0, check.errors.size)
    }
}
