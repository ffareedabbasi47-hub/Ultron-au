package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w412dp-h915dp")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreen_rendersCoreAndNavigatesToModules() {
        composeTestRule.onNodeWithTag("top_bar_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("central_ultron_core").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test_wake_word_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("quick_action_control").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("quick_action_vision").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("quick_action_files").performScrollTo().assertIsDisplayed()

        // Test navigation to CONTROL tab
        composeTestRule.onNodeWithTag("nav_tab_control").performClick()
        composeTestRule.onNodeWithTag("refresh_telemetry_button").assertIsDisplayed()

        // Test navigation to TOOLS tab
        composeTestRule.onNodeWithTag("nav_tab_tools").performClick()
        composeTestRule.onNodeWithTag("tool_files_docs").assertIsDisplayed()

        // Test navigation to VISION tab
        composeTestRule.onNodeWithTag("nav_tab_vision").performClick()

        // Return to HOME tab
        composeTestRule.onNodeWithTag("nav_tab_home").performClick()
        composeTestRule.onNodeWithTag("central_ultron_core").assertIsDisplayed()
    }

    @Test
    fun router_handlesConversationalQuestionsAndCommandsDirectly() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val router = com.example.platform.DeviceControlAndToolRouter(context)

        // 1. Capital question
        val capitalResult = router.classifyAndRouteInput("Max, India ki capital kya hai?", "Boss", com.example.domain.model.AssistantPersona.MAX)
        org.junit.Assert.assertTrue(capitalResult.handledDirectly)
        org.junit.Assert.assertEquals("New Delhi, Boss.", capitalResult.responseTextHinglish)

        // 2. Gravity question
        val gravityResult = router.classifyAndRouteInput("Max, gravity kya hoti hai?", "Boss", com.example.domain.model.AssistantPersona.MAX)
        org.junit.Assert.assertTrue(gravityResult.handledDirectly)
        org.junit.Assert.assertTrue(gravityResult.responseTextHinglish.contains("natural force"))

        // 3. Current date question
        val dateResult = router.classifyAndRouteInput("Max, aaj kya date hai?", "Boss", com.example.domain.model.AssistantPersona.MAX)
        org.junit.Assert.assertTrue(dateResult.handledDirectly)
        org.junit.Assert.assertTrue(dateResult.responseTextHinglish.contains("Aaj"))

        // 4. CPU usage question
        val cpuResult = router.classifyAndRouteInput("Max, CPU kitna use ho raha hai?", "Boss", com.example.domain.model.AssistantPersona.MAX)
        org.junit.Assert.assertTrue(cpuResult.handledDirectly)
        org.junit.Assert.assertTrue(cpuResult.responseTextHinglish.contains("cores"))

        // 5. System status question
        val statusResult = router.classifyAndRouteInput("Max, system status?", "Boss", com.example.domain.model.AssistantPersona.MAX)
        org.junit.Assert.assertTrue(statusResult.handledDirectly)
        org.junit.Assert.assertTrue(statusResult.responseTextHinglish.contains("systems stable hain") && statusResult.responseTextHinglish.contains("MAX fully operational"))

        // 6. Note prompt question
        val noteResult = router.classifyAndRouteInput("Max ek note bana", "Boss", com.example.domain.model.AssistantPersona.MAX)
        org.junit.Assert.assertTrue(noteResult.handledDirectly)
        org.junit.Assert.assertEquals("Kis topic par, Boss?", noteResult.responseTextHinglish)

        // 7. App launch command
        val youtubeResult = router.classifyAndRouteInput("YouTube kholo", "Boss", com.example.domain.model.AssistantPersona.MAX)
        org.junit.Assert.assertTrue(youtubeResult.handledDirectly)
        org.junit.Assert.assertTrue(youtubeResult.responseTextHinglish.contains("YouTube khol raha hoon"))
    }

    @Test
    fun wakeWordEngine_detectsStandaloneInlineAndAcousticWakePatterns() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val wakeEngine = com.example.platform.UltronWakeWordEngine(context)

        // 1. Standalone wake word ("Max")
        val exactMax = wakeEngine.evaluateTranscriptForWakeWord("Max")
        org.junit.Assert.assertTrue(exactMax.detected)
        org.junit.Assert.assertEquals("MAX", exactMax.wakePhrase)
        org.junit.Assert.assertEquals("", exactMax.inlineCommandRemainder)

        // 2. Standalone wake word ("Hey Ultron")
        val exactUltron = wakeEngine.evaluateTranscriptForWakeWord("Hey Ultron")
        org.junit.Assert.assertTrue(exactUltron.detected)
        org.junit.Assert.assertEquals("HEY ULTRON", exactUltron.wakePhrase)

        // 3. Inline wake + command ("Max WhatsApp kholo")
        val inlineWake = wakeEngine.evaluateTranscriptForWakeWord("Max WhatsApp kholo")
        org.junit.Assert.assertTrue(inlineWake.detected)
        org.junit.Assert.assertEquals("MAX", inlineWake.wakePhrase)
        org.junit.Assert.assertEquals("WhatsApp kholo", inlineWake.inlineCommandRemainder)

        // 4. Non-wake regular utterance
        val normalSpeech = wakeEngine.evaluateTranscriptForWakeWord("What is the weather today")
        org.junit.Assert.assertFalse(normalSpeech.detected)
    }
}
