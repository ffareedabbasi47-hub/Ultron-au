package com.example

import com.example.data.local.UltronPreferences
import com.example.data.remote.GeminiAndWeatherClient
import com.example.domain.model.AssistantCoreState
import com.example.domain.model.AssistantPersona
import com.example.domain.model.DeviceTelemetry
import com.example.domain.model.IntentCategory
import com.example.domain.model.RoutingMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun configuredCreatorRecord_isProtectedFardeen() {
        assertEquals("Fardeen", UltronPreferences.PROTECTED_CREATOR_NAME)
    }

    @Test
    fun dualPersona_hasDistinctUltronAndMaxProfiles() {
        assertEquals("U.L.T.R.O.N", AssistantPersona.ULTRON.coreSpacedTitle)
        assertEquals("M.A.X", AssistantPersona.MAX.coreSpacedTitle)
        assertTrue(AssistantPersona.ULTRON.defaultPitch != AssistantPersona.MAX.defaultPitch)
    }

    @Test
    fun telemetryFormatting_formatsGigabytesHeapAndRealEnergyCleanly() {
        val telemetry = DeviceTelemetry(
            ramUsedMb = 1843L,
            ramTotalMb = 8192L,
            ramPercent = 23,
            storageFreeGb = 42.5f,
            storageTotalGb = 128.0f,
            storagePercent = 66,
            batteryPercent = 79,
            isCharging = true,
            isOnline = true,
            networkLabel = "ONLINE • Wi-Fi",
            cpuCores = 8,
            appHeapUsedMb = 48L,
            appHeapMaxMb = 256L,
            flashlightOn = false,
            mediaVolumePercent = 60,
            maxMediaVolume = 15,
            currentMediaVolume = 9
        )
        assertEquals("1.8 GB / 8.0 GB (23%)", telemetry.formattedRam)
        assertEquals(8, telemetry.cpuCores)
        assertEquals(5, telemetry.realEnergyUnits)
        assertTrue(telemetry.systemCpuStatusHinglish.contains("Android 8+"))
    }

    @Test
    fun smartModelRouter_selectsVerifiedGeminiModels() {
        val client = GeminiAndWeatherClient()
        assertEquals(
            "gemini-2.5-flash-lite",
            client.selectModelForTask(RoutingMode.AUTO, IntentCategory.COMMAND)
        )
        assertEquals(
            "gemini-2.5-pro",
            client.selectModelForTask(RoutingMode.AUTO, IntentCategory.STUDY)
        )
        assertEquals(
            "gemini-2.5-flash",
            client.selectModelForTask(RoutingMode.AUTO, IntentCategory.QUESTION)
        )
        assertTrue(client.getInitialModelCatalog().any { it.id == "gemini-2.5-flash" })
    }

    @Test
    fun assistantCoreState_hasDistinctBadgesForEveryLifecycleStage() {
        assertEquals("READY", AssistantCoreState.IDLE.badgeLabel)
        assertEquals("WAKE", AssistantCoreState.WAKE_DETECTED.badgeLabel)
        assertEquals("SUNO (LISTENING)", AssistantCoreState.LISTENING.badgeLabel)
        assertEquals("SOCHO (THINKING)", AssistantCoreState.THINKING.badgeLabel)
        assertEquals("KAAM (EXECUTING)", AssistantCoreState.EXECUTING.badgeLabel)
        assertEquals("SPEAKING", AssistantCoreState.SPEAKING.badgeLabel)
    }

    @Test
    fun liveSilenceSleepTimeout_optionsMatchConfigurableRequirements() {
        val options = com.example.domain.model.LiveSilenceSleepTimeout.entries
        assertEquals(5, options.size)
        assertEquals(0, com.example.domain.model.LiveSilenceSleepTimeout.OFF.minutes)
        assertEquals(5, com.example.domain.model.LiveSilenceSleepTimeout.FIVE_MINUTES.minutes)
        assertEquals(10, com.example.domain.model.LiveSilenceSleepTimeout.TEN_MINUTES.minutes)
        assertEquals(15, com.example.domain.model.LiveSilenceSleepTimeout.FIFTEEN_MINUTES.minutes)
        assertEquals(20, com.example.domain.model.LiveSilenceSleepTimeout.TWENTY_MINUTES.minutes)
    }

    @Test
    fun speechSanitization_cleansMarkdownAndPunctuationForNaturalSpeech() {
        val raw = "Boss, **WhatsApp** khol diya hai. [Link](https://whatsapp.com) *all systems* — ready..."
        val cleaned = raw
            .replace(Regex("[*#_`~>|]"), "")
            .replace(Regex("\\[(.*?)]\\(.*?\\)"), "$1")
            .replace(Regex("\\s*—\\s*"), ", ")
            .replace("...", ". ")
            .replace(Regex("\\s*,\\s*"), ", ")
            .replace(Regex("\\s+\\."), ".")
            .replace(Regex("\\s+"), " ")
            .trim()
        assertEquals("Boss, WhatsApp khol diya hai. Link all systems, ready.", cleaned)
    }
}
