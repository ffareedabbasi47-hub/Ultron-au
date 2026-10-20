package com.example.domain.model

import kotlinx.serialization.Serializable
import java.util.Locale

/**
 * Dual Assistant Persona Mode:
 * - ULTRON: Composed, analytical, strategic cyber-intelligence. Addresses user as Fardeen.
 * - MAX: Witty, energetic, proactive tactical companion. Addresses user as Boss.
 * Saying "Max ko bhej" or "Ultron ko bhej" hands off between the two personas and transforms the UI & voice!
 */
enum class AssistantPersona(
    val displayName: String,
    val coreSpacedTitle: String,
    val roleSubtitle: String,
    val defaultWakePhrase: String,
    val defaultPitch: Float,
    val defaultSpeed: Float,
    val defaultAddress: String
) {
    MAX(
        displayName = "Max",
        coreSpacedTitle = "M.A.X",
        roleSubtitle = "AI ASSISTANT • TACTICAL COMPANION",
        defaultWakePhrase = "Max",
        defaultPitch = 1.02f,
        defaultSpeed = 1.02f,
        defaultAddress = "Boss"
    ),
    ULTRON(
        displayName = "Ultron",
        coreSpacedTitle = "U.L.T.R.O.N",
        roleSubtitle = "AI ASSISTANT • CYBER CORE",
        defaultWakePhrase = "Hey Ultron",
        defaultPitch = 0.88f,
        defaultSpeed = 0.98f,
        defaultAddress = "Fardeen"
    )
}

enum class GenderVerbStyle(val code: String, val labelHinglish: String) {
    MALE("male", "Ladka"),
    FEMALE("female", "Ladki"),
    NEUTRAL("neutral", "Nahi batana")
}

enum class SurfaceThemeStyle(val code: String, val label: String, val alpha: Float) {
    FLAT("flat", "Flat", 0.96f),
    GLASS("glass", "Glass", 0.68f),
    SOFT("soft", "Soft", 0.82f),
    CLAY("clay", "Clay", 0.90f)
}

enum class CornerThemeStyle(val code: String, val label: String, val radiusDp: Int) {
    SHARP("sharp", "Nukeele", 8),
    SLIGHT("slight", "Halke gol", 14),
    ROUND("round", "Gol", 22),
    PILL("pill", "Takiye jaise", 30)
}

enum class SubAgentRoutingStrategy(val code: String, val titleHinglish: String, val descHinglish: String) {
    BEST_FIRST(
        "best_first",
        "Best pehle",
        "Hamesha #1 provider try karta hai, aur fail hone par hi neeche wala backup model use karta hai."
    ),
    RANKED(
        "ranked",
        "Ranked – sabko use karo",
        "Kaam har enabled model me task ke hisaab se baant-ta hai (Fast vs Balanced vs Pro)."
    ),
    PARALLEL(
        "parallel",
        "Sab ek saath",
        "Complex सवालों me primary aur reasoning engine ko verify karke best jawab deta hai."
    )
}

/**
 * Single source of truth for ULTRON / MAX assistant state machine:
 * IDLE -> WAKE_DETECTED -> LISTENING -> TRANSCRIBING -> THINKING -> EXECUTING -> SPEAKING -> IDLE
 */
enum class AssistantCoreState(
    val badgeLabel: String,
    val hinglishLabel: String,
    val hinglishSubtitle: String,
    val englishSubtitle: String
) {
    IDLE(
        badgeLabel = "READY",
        hinglishLabel = "Taiyaar",
        hinglishSubtitle = "BOLO, KAISE HELP KARUN?",
        englishSubtitle = "Systems ready. Awaiting your command."
    ),
    WAKE_DETECTED(
        badgeLabel = "WAKE",
        hinglishLabel = "Suno",
        hinglishSubtitle = "Haan, bolo. Main sun raha hoon...",
        englishSubtitle = "Wake phrase detected. Listening now..."
    ),
    LISTENING(
        badgeLabel = "SUNO (LISTENING)",
        hinglishLabel = "Sun raha hai...",
        hinglishSubtitle = "SUN RAHA HAI...",
        englishSubtitle = "Listening to your live microphone input..."
    ),
    TRANSCRIBING(
        badgeLabel = "TRANSCRIBING",
        hinglishLabel = "Samajh raha hai...",
        hinglishSubtitle = "AWAAZ SAMAJH RAHA HOON...",
        englishSubtitle = "Processing speech transcript..."
    ),
    THINKING(
        badgeLabel = "SOCHO (THINKING)",
        hinglishLabel = "Soch raha hai...",
        hinglishSubtitle = "SOCH RAHA HAI...",
        englishSubtitle = "Analyzing context and generating response..."
    ),
    EXECUTING(
        badgeLabel = "KAAM (EXECUTING)",
        hinglishLabel = "Kaam chalu hai...",
        hinglishSubtitle = "COMMAND EXECUTE HO RAHA HAI...",
        englishSubtitle = "Executing verified device action..."
    ),
    SPEAKING(
        badgeLabel = "SPEAKING",
        hinglishLabel = "Bol raha hai...",
        hinglishSubtitle = "BOL RAHA HAI...",
        englishSubtitle = "Speaking response (Tap Stop to interrupt)"
    ),
    PAUSED(
        badgeLabel = "PAUSED",
        hinglishLabel = "Ruka Hua",
        hinglishSubtitle = "SESSION PAUSED HAI",
        englishSubtitle = "Assistant session paused."
    ),
    PERMISSION_BLOCKED(
        badgeLabel = "MIC BLOCKED",
        hinglishLabel = "Permission Chahiye",
        hinglishSubtitle = "MIC PERMISSION ALLOW KAREIN",
        englishSubtitle = "Microphone permission is required for voice."
    ),
    OFFLINE(
        badgeLabel = "OFFLINE",
        hinglishLabel = "Offline",
        hinglishSubtitle = "OFFLINE • LOCAL ENGINE READY",
        englishSubtitle = "Offline mode. Local device tools remain active."
    ),
    SLEEP_ARMED(
        badgeLabel = "SLEEP (ARMED)",
        hinglishLabel = "So raha hai (Armed)",
        hinglishSubtitle = "SO RAHA HAI • \"HEY ULTRON\" YA TAP KAREIN",
        englishSubtitle = "Sleeping to save power. Tap mic or speak wake word to resume."
    ),
    ERROR(
        badgeLabel = "ALERT",
        hinglishLabel = "Error",
        hinglishSubtitle = "KUCH ERROR AAYA • TAP TO RETRY",
        englishSubtitle = "An error occurred. Tap retry or inspect."
    )
}

enum class LiveSilenceSleepTimeout(
    val minutes: Int,
    val labelHinglish: String,
    val descriptionHinglish: String
) {
    OFF(0, "Off (Kabhi band mat karo)", "Session manually Stop dabane tak live aur khula rahega."),
    FIVE_MINUTES(5, "5 minutes", "5 min lagatar chup rehne par low-power Sleep/Armed mode me chala jayega."),
    TEN_MINUTES(10, "10 minutes", "10 min lagatar chup rehne par low-power Sleep/Armed mode me chala jayega."),
    FIFTEEN_MINUTES(15, "15 minutes", "15 min lagatar chup rehne par low-power Sleep/Armed mode me chala jayega."),
    TWENTY_MINUTES(20, "20 minutes", "20 min lagatar chup rehne par low-power Sleep/Armed mode me chala jayega.")
}

data class VoiceDiagnosticsInfo(
    val audioSource: String = "VOICE_COMMUNICATION (AEC Active)",
    val sampleRateHz: Int = 16000,
    val isAecAvailable: Boolean = false,
    val isAecEnabled: Boolean = false,
    val isNoiseSuppressorAvailable: Boolean = false,
    val isNoiseSuppressorEnabled: Boolean = false,
    val isTtsSpeaking: Boolean = false,
    val isEchoGated: Boolean = false,
    val isMusicActive: Boolean = false,
    val currentVadState: String = "SILENCE",
    val sessionState: String = "STOPPED",
    val silenceRemainingSec: Int = 300,
    val silenceTimeoutLabel: String = "5 minutes",
    val sessionUptimeSec: Long = 0L,
    val lastEchoTestResult: String? = null,
    val wakeEngineName: String = "Local Acoustic KWS + Phoneme Verifier",
    val isPorcupineActive: Boolean = false,
    val wakeDetectionCount: Int = 0,
    val lastWakeWordDetected: String? = null
)

enum class WakeWordStatus(val label: String, val detailHinglish: String) {
    ACTIVE("ACTIVE (FOREGROUND)", "Foreground wake session active hai (Android OS mic rules ke mutabik)."),
    PAUSED("PAUSED", "Wake-word listener abhi paused hai."),
    MICROPHONE_BLOCKED("MIC BLOCKED", "Microphone permission disabled hai. Permission allow karein."),
    SERVICE_STOPPED("STOPPED", "Foreground wake session abhi band hai. Start dabayein.")
}

enum class LanguageMode(val code: String, val displayName: String) {
    HINGLISH("hinglish", "Roman Hinglish"),
    HINDI("hindi", "Hindi (हिंदी)"),
    ENGLISH("english", "English")
}

enum class RoutingMode(val title: String, val subtitle: String) {
    AUTO("Auto Smart Router", "Task ke hisaab se Fast, Balanced ya Advanced model chunta hai"),
    FAST("Fast (Low Latency)", "Quick commands aur chhote jawab (gemini-2.5-flash-lite)"),
    BALANCED("Balanced (Default)", "Hinglish baat-cheet, vision aur roz ke kaam (gemini-2.5-flash)"),
    ADVANCED("Advanced Reasoning", "Deep Padhai, STEM aur analytical logic (gemini-2.5-pro)")
}

enum class IntentCategory(val label: String) {
    QUESTION("QUESTION"),
    COMMAND("COMMAND"),
    CONVERSATION("CONVERSATION"),
    SEARCH("SEARCH"),
    DEVICE_CONTROL("DEVICE CONTROL"),
    FILE_TASK("FILE TASK"),
    SCREEN_TASK("SCREEN TASK"),
    STUDY("PADHAI / STUDY"),
    DREAM("DREAM JOURNAL"),
    PERSONA_SWITCH("PERSONA HANDOFF")
}

@Serializable
data class AIModelSpec(
    val id: String,
    val name: String,
    val provider: String,
    val isLocal: Boolean,
    val capabilities: List<String>,
    val descriptionHinglish: String,
    val isHealthy: Boolean = true,
    val averageLatencyMs: Long = 0L
)

data class VoiceProfileInfo(
    val id: String,
    val displayName: String,
    val localeTag: String,
    val engineName: String,
    val requiresNetwork: Boolean,
    val qualityLabel: String
)

data class DeviceTelemetry(
    val ramUsedMb: Long,
    val ramTotalMb: Long,
    val ramPercent: Int,
    val storageFreeGb: Float,
    val storageTotalGb: Float,
    val storagePercent: Int,
    val batteryPercent: Int,
    val isCharging: Boolean,
    val isOnline: Boolean,
    val networkLabel: String,
    val cpuCores: Int,
    val appHeapUsedMb: Long,
    val appHeapMaxMb: Long,
    val systemCpuStatusHinglish: String = "Available nahi (Android 8+ OS Restricted)",
    val flashlightOn: Boolean,
    val mediaVolumePercent: Int,
    val maxMediaVolume: Int,
    val currentMediaVolume: Int
) {
    val formattedRam: String
        get() {
            val usedGb = ramUsedMb / 1024f
            val totalGb = ramTotalMb / 1024f
            return if (ramTotalMb >= 1024) {
                String.format(Locale.US, "%.1f GB / %.1f GB (%d%%)", usedGb, totalGb, ramPercent)
            } else {
                "$ramUsedMb MB / $ramTotalMb MB ($ramPercent%)"
            }
        }

    /**
     * Real defined "Energy" score (1..5) shown in the top-right pill on the Home screen:
     * Calculated transparently from real battery level + network availability + active engines.
     */
    val realEnergyUnits: Int
        get() {
            var score = 1
            if (batteryPercent >= 25) score++
            if (batteryPercent >= 60) score++
            if (isOnline) score++
            if (ramPercent < 88) score++
            return score.coerceIn(1, 5)
        }
}

data class WeatherData(
    val city: String,
    val temperatureC: Double,
    val feelsLikeC: Double,
    val conditionText: String,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val updatedAt: String,
    val source: String = "Open-Meteo Live API"
)

data class DetectedScreenElement(
    val id: String,
    val label: String,
    val explanationHinglish: String,
    val normalizedX: Float,
    val normalizedY: Float,
    val normalizedW: Float,
    val normalizedH: Float
)

data class InstalledAppInfo(
    val appName: String,
    val packageName: String
)

data class MarketWatchItem(
    val symbol: String,
    val name: String,
    val noteHinglish: String,
    val addedAt: String
)

data class CodingProjectItem(
    val id: String,
    val title: String,
    val typeLabel: String,
    val filesCount: Int,
    val updatedAgo: String,
    val codeSnippet: String
)

enum class UltronDestination(val route: String, val titleHinglish: String, val section: String) {
    // HOME
    GHAR("ghar", "Home", "HOME"),
    PERSONA_SWITCHER("persona_switcher", "Ultron ↔ Max Dual Core", "HOME"),
    YAADEIN("yaadein", "Yaadein", "HOME"),
    CHAT("chat", "Chat", "HOME"),
    LIVE_VOICE("live_voice", "Suno (Live Voice)", "HOME"),

    // KAAM-KAAJ (From Hamburger Drawer in Reference Video)
    MARKETS("markets", "Markets", "KAAM-KAAJ"),
    MARKETING("marketing", "Marketing", "KAAM-KAAJ"),
    DOCUMENTS("documents", "Documents", "KAAM-KAAJ"),
    CODING("coding", "Website / Coding", "KAAM-KAAJ"),
    ACCESSIBILITY_GUIDE("accessibility_guide", "Accessibility", "KAAM-KAAJ"),

    // HOME QUICK ACTIONS & CARDS
    GAANE("gaane", "Gaane / Music", "ROZ KE KAAM"),
    PADHAI("padhai", "Padhai", "ROZ KE KAAM"),
    JOURNAL("journal", "Journal", "ROZ KE KAAM"),
    DREAM_JOURNAL("dream_journal", "Dream Journal", "ROZ KE KAAM"),
    MAUSAM("mausam", "Mausam", "ROZ KE KAAM"),
    AAJ("aaj", "Aaj / Reminders", "ROZ KE KAAM"),
    MOOD("mood", "Mood", "ROZ KE KAAM"),
    SCAN("scan", "Scanner", "ROZ KE KAAM"),

    // SYSTEM & AI STUDIO
    SETTINGS("settings", "Settings", "SYSTEM"),
    RULES("rules", "Ultron & Max ke rules", "SYSTEM"),
    PC_PHONE("pc_phone", "PC ⇄ Phone", "SYSTEM"),
    CONTROL_CENTER("control_center", "Phone Control & Telemetry", "SYSTEM"),
    VISION_CENTER("vision_center", "Screen Samjho (Vision)", "SYSTEM"),
    TOOLS("tools", "Tools Hub", "SYSTEM"),
    AI_MODELS("ai_models", "Sub-agents & AI Brain", "SYSTEM"),
    AWAAZ_STUDIO("awaaz_studio", "Voice Engine (Awaaz)", "SYSTEM"),
    AVATAR_STUDIO("avatar_studio", "Appearance & Avatar Core", "SYSTEM"),

    // SETTINGS SUB-SCREENS
    SETTINGS_PERSONAL("settings_personal", "Personal", "SETTINGS"),
    SETTINGS_SECRET("settings_secret", "Companion Tone Settings", "SETTINGS"),
    SETTINGS_SKILLS("settings_skills", "Skills & Playbooks", "SETTINGS"),
    SETTINGS_MESSAGES("settings_messages", "Email, WhatsApp & Social", "SETTINGS"),
    SETTINGS_CONNECTORS("settings_connectors", "Connectors", "SETTINGS"),
    SETTINGS_BACKUP("settings_backup", "Backup & Restore", "SETTINGS"),
    SETTINGS_ADVANCED("settings_advanced", "Advanced", "SETTINGS"),
    SETTINGS_THEME("settings_theme", "Theme & Corners", "SETTINGS"),
    SETTINGS_TYPING("settings_typing", "Realistic Typing", "SETTINGS"),
    SETTINGS_VOICE_GUARDIAN("settings_voice_guardian", "Voice Guardian", "SETTINGS"),
    DIAGNOSTICS("diagnostics", "Permissions & Diagnostics", "SETTINGS"),

    // AUR
    PRIVACY_POLICY("privacy_policy", "Privacy Policy", "AUR"),
    ABOUT("about", "About Ultron AI", "AUR")
}
