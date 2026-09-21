package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.components.GlobalAiLoadingOverlay
import com.example.ui.theme.AhmedEditsTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GlobalAiLoadingOverlayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGlobalAiLoadingOverlay_visibleDisplaysIndicatorAndText() {
        var cancelClicked = false

        composeTestRule.setContent {
            AhmedEditsTheme {
                GlobalAiLoadingOverlay(
                    visible = true,
                    title = "Deconstructing Reference Video",
                    statusMessage = "Tracking focal subjects...",
                    progress = 45,
                    canCancel = true,
                    onCancel = { cancelClicked = true }
                )
            }
        }

        composeTestRule.mainClock.advanceTimeBy(400)

        composeTestRule.onNodeWithTag("global_ai_loading_overlay", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("ai_circular_progress_indicator", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("ai_loading_title", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("ai_loading_status_text", useUnmergedTree = true).assertTextEquals("Tracking focal subjects...")

        composeTestRule.onNodeWithTag("btn_cancel_ai_processing", useUnmergedTree = true).assertExists().performClick()
        assertTrue("Cancel callback should be invoked", cancelClicked)
    }

    @Test
    fun testGlobalAiLoadingOverlay_indeterminateWhenProgressZero() {
        composeTestRule.setContent {
            AhmedEditsTheme {
                GlobalAiLoadingOverlay(
                    visible = true,
                    title = "AI Video Processing",
                    statusMessage = "Initializing Neural Pipeline...",
                    progress = 0,
                    canCancel = false
                )
            }
        }

        composeTestRule.mainClock.advanceTimeBy(400)

        composeTestRule.onNodeWithTag("global_ai_loading_overlay", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("ai_circular_progress_indicator", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("btn_cancel_ai_processing", useUnmergedTree = true).assertDoesNotExist()
    }
}
