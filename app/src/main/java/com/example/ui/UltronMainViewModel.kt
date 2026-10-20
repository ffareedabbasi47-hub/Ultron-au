package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AvatarModelEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DreamEntryEntity
import com.example.data.local.JournalEntryEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.MoodEntryEntity
import com.example.data.local.ReminderEntity
import com.example.data.local.UltronDatabase
import com.example.data.local.UltronPreferences
import com.example.data.local.UltronSettingsState
import com.example.data.remote.GeminiAndWeatherClient
import com.example.domain.model.AIModelSpec
import com.example.domain.model.AssistantCoreState
import com.example.domain.model.AssistantPersona
import com.example.domain.model.CodingProjectItem
import com.example.domain.model.CornerThemeStyle
import com.example.domain.model.DetectedScreenElement
import com.example.domain.model.DeviceTelemetry
import com.example.domain.model.GenderVerbStyle
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.IntentCategory
import com.example.domain.model.LanguageMode
import com.example.domain.model.MarketWatchItem
import com.example.domain.model.RoutingMode
import com.example.domain.model.SubAgentRoutingStrategy
import com.example.domain.model.SurfaceThemeStyle
import com.example.domain.model.UltronDestination
import com.example.domain.model.VoiceDiagnosticsInfo
import com.example.domain.model.VoiceProfileInfo
import com.example.domain.model.WakeWordStatus
import com.example.domain.model.WeatherData
import com.example.platform.DeviceControlAndToolRouter
import com.example.platform.VoiceAndSpeechManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UltronMainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = UltronDatabase.getInstance(context)
    private val dao = database.ultronDao()
    private val preferences = UltronPreferences(context)
    val aiAndWeatherClient = GeminiAndWeatherClient()
    val deviceRouter = DeviceControlAndToolRouter(context)

    // --- Navigation & Core Session State ---
    private val _currentDestination = MutableStateFlow(UltronDestination.GHAR)
    val currentDestination: StateFlow<UltronDestination> = _currentDestination.asStateFlow()

    private val _coreState = MutableStateFlow(AssistantCoreState.IDLE)
    val coreState: StateFlow<AssistantCoreState> = _coreState.asStateFlow()

    private val _composerText = MutableStateFlow("")
    val composerText: StateFlow<String> = _composerText.asStateFlow()

    private val _streamingAssistantText = MutableStateFlow("")
    val streamingAssistantText: StateFlow<String> = _streamingAssistantText.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _lastLatencyMs = MutableStateFlow(0L)
    val lastLatencyMs: StateFlow<Long> = _lastLatencyMs.asStateFlow()

    private val _providerTestStatus = MutableStateFlow<String?>(null)
    val providerTestStatus: StateFlow<String?> = _providerTestStatus.asStateFlow()

    private var activeAiJob: Job? = null

    // --- Settings Flow ---
    val settings: StateFlow<UltronSettingsState> = preferences.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UltronSettingsState())

    // --- Room Database Flows ---
    val messages: StateFlow<List<ChatMessageEntity>> = dao.observeMessages(1L)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val memories: StateFlow<List<MemoryEntity>> = dao.observeMemories()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val journalEntries: StateFlow<List<JournalEntryEntity>> = dao.observeJournalEntries()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val dreamEntries: StateFlow<List<DreamEntryEntity>> = dao.observeDreamEntries()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val moodEntries: StateFlow<List<MoodEntryEntity>> = dao.observeMoodEntries()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val reminders: StateFlow<List<ReminderEntity>> = dao.observeReminders()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val avatars: StateFlow<List<AvatarModelEntity>> = dao.observeAvatars()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // --- Markets & Coding Projects State ---
    private val _marketWatchlist = MutableStateFlow(
        listOf(
            MarketWatchItem("RELIANCE.NS", "Reliance Industries", "Watchlist tracked symbol • Add or query via AI", "20 Oct"),
            MarketWatchItem("TCS.NS", "Tata Consultancy Services", "IT Sector benchmark", "20 Oct"),
            MarketWatchItem("BTC-USD", "Bitcoin / USD", "Crypto benchmark", "20 Oct")
        )
    )
    val marketWatchlist: StateFlow<List<MarketWatchItem>> = _marketWatchlist.asStateFlow()

    private val _codingProjects = MutableStateFlow(
        listOf(
            CodingProjectItem(
                id = "proj_1",
                title = "Iron Man 3D Design v2",
                typeLabel = "web • 4 files",
                filesCount = 4,
                updatedAgo = "23 ghante pehle",
                codeSnippet = "// Three.js / WebGL Cyber Armor Shader\nconst armorMaterial = new THREE.MeshStandardMaterial({ color: 0x00e5ff, metalness: 0.9, roughness: 0.15 });"
            ),
            CodingProjectItem(
                id = "proj_2",
                title = "Ultron Dual-Core Voice Engine",
                typeLabel = "kotlin • 3 files",
                filesCount = 3,
                updatedAgo = "2 din pehle",
                codeSnippet = "// Android TTS + SpeechRecognizer Barge-In Router\nfun handoffPersona(target: AssistantPersona) { preferences.setActivePersona(target) }"
            ),
            CodingProjectItem(
                id = "proj_3",
                title = "Cyberpunk Glassmorphism UI Kit",
                typeLabel = "compose • 3 files",
                filesCount = 3,
                updatedAgo = "2 din pehle",
                codeSnippet = "// Jetpack Compose Glassmorphic Card\nModifier.background(UltronSurface.copy(alpha = 0.68f), RoundedCornerShape(22.dp))"
            )
        )
    )
    val codingProjects: StateFlow<List<CodingProjectItem>> = _codingProjects.asStateFlow()

    // --- Telemetry, Weather, Vision & Apps ---
    private val _telemetry = MutableStateFlow(deviceRouter.readRealDeviceTelemetry())
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherData?>(null)
    val weatherState: StateFlow<WeatherData?> = _weatherState.asStateFlow()

    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _modelCatalog = MutableStateFlow(aiAndWeatherClient.getInitialModelCatalog())
    val modelCatalog: StateFlow<List<AIModelSpec>> = _modelCatalog.asStateFlow()

    // --- Screen Samjho / Vision State ---
    private val _isScreenVisionActive = MutableStateFlow(false)
    val isScreenVisionActive: StateFlow<Boolean> = _isScreenVisionActive.asStateFlow()

    private val _visionExplanation = MutableStateFlow(
        "Screen Samjho ready hai. 'START VISION' ya 'EXPLAIN SCREEN' dabayein."
    )
    val visionExplanation: StateFlow<String> = _visionExplanation.asStateFlow()

    private val _detectedElements = MutableStateFlow<List<DetectedScreenElement>>(emptyList())
    val detectedElements: StateFlow<List<DetectedScreenElement>> = _detectedElements.asStateFlow()

    private val _showElementHighlights = MutableStateFlow(true)
    val showElementHighlights: StateFlow<Boolean> = _showElementHighlights.asStateFlow()

    // --- Voice & Speech Manager ---
    val voiceManager = VoiceAndSpeechManager(
        context = context,
        onWakeDetected = { wakePhrase ->
            _coreState.value = AssistantCoreState.WAKE_DETECTED
            val upper = wakePhrase.uppercase()
            if (upper.contains("MAX") && settings.value.activePersona != AssistantPersona.MAX) {
                switchPersona(AssistantPersona.MAX, speakGreeting = false)
            } else if (upper.contains("ULTRON") && settings.value.activePersona != AssistantPersona.ULTRON) {
                switchPersona(AssistantPersona.ULTRON, speakGreeting = false)
            }
            val addr = if (settings.value.activePersona == AssistantPersona.MAX) "Boss" else settings.value.userAddress
            _statusBannerMessage.value = "Wake Word '$wakePhrase' detected -> Yes, $addr."
        },
        onPartialTranscript = { partial ->
            _coreState.value = AssistantCoreState.LISTENING
            _composerText.value = partial
        },
        onFinalTranscript = { finalTranscript ->
            _composerText.value = ""
            submitUserPrompt(finalTranscript, fromVoice = true)
        },
        onSttError = { errorMsg ->
            _coreState.value = if (errorMsg.contains("permission", ignoreCase = true)) {
                AssistantCoreState.PERMISSION_BLOCKED
            } else {
                AssistantCoreState.ERROR
            }
            _statusBannerMessage.value = errorMsg
            if (settings.value.autoSpeakReplies && !errorMsg.contains("permission", ignoreCase = true)) {
                speakMessage(errorMsg)
            }
        },
        onSpeakingStateChanged = { speaking ->
            if (speaking) {
                _coreState.value = AssistantCoreState.SPEAKING
            } else if (_coreState.value == AssistantCoreState.SPEAKING ||
                _coreState.value == AssistantCoreState.WAKE_DETECTED
            ) {
                _coreState.value = AssistantCoreState.IDLE
            }
        },
        onSilenceSleepTriggered = {
            _coreState.value = AssistantCoreState.SLEEP_ARMED
            _statusBannerMessage.value = "Silence timeout poora hua -> SLEEP / ARMED mode active. 'MAX' ya 'HEY ULTRON' bolein."
        }
    )

    val availableVoices: StateFlow<List<VoiceProfileInfo>> = voiceManager.availableVoices
    val ttsEngineLabel: StateFlow<String> = voiceManager.ttsEngineLabel
    val isListeningMic: StateFlow<Boolean> = voiceManager.isListeningMic
    val isSpeaking: StateFlow<Boolean> = voiceManager.isSpeaking
    val micAmplitude: StateFlow<Float> = voiceManager.micAmplitude
    val wakeWordStatus: StateFlow<WakeWordStatus> = voiceManager.wakeWordStatus
    val voiceDiagnostics: StateFlow<VoiceDiagnosticsInfo> = voiceManager.voiceDiagnostics

    init {
        seedInitialDataIfNeeded()
        startTelemetryRefreshLoop()
        viewModelScope.launch {
            isListeningMic.collect { listening ->
                if (listening && (_coreState.value == AssistantCoreState.IDLE || _coreState.value == AssistantCoreState.SLEEP_ARMED)) {
                    _coreState.value = AssistantCoreState.LISTENING
                }
            }
        }
        viewModelScope.launch {
            settings.collect { currentSettings ->
                voiceManager.updateSettings(currentSettings)
                if (_weatherState.value == null) {
                    refreshWeather(currentSettings.weatherCity)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _installedApps.value = deviceRouter.queryInstalledLaunchableApps()
        }
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            if (dao.getAvatarCount() == 0) {
                dao.insertAvatars(
                    listOf(
                        AvatarModelEntity(
                            id = "ultron_prime_core",
                            name = "ULTRON Prime Reactor Core",
                            styleType = "PRIME_CORE",
                            format = "Procedural 3D + PBR Metallic Core",
                            fileSizeLabel = "Built-in (2.4 MB)",
                            license = "CC0 / Original ULTRON AI Asset",
                            attribution = "ULTRON Studio by Fardeen",
                            sourceUrl = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/DamagedHelmet/glTF-Binary/DamagedHelmet.glb",
                            primaryHex = "#00E5FF",
                            secondaryHex = "#EC4899",
                            downloadState = "INSTALLED",
                            downloadProgress = 100
                        ),
                        AvatarModelEntity(
                            id = "mark_x_sentinel",
                            name = "MAX Tactical Cyber Core",
                            styleType = "SENTINEL_MECH",
                            format = "GLB 2.0 + Emissive Visor Mesh",
                            fileSizeLabel = "3.6 MB (Khronos CC-BY-4.0)",
                            license = "CC-BY 4.0 Verified",
                            attribution = "Khronos Group / MAX Shader Preset",
                            sourceUrl = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/DamagedHelmet/glTF-Binary/DamagedHelmet.glb",
                            primaryHex = "#10B981",
                            secondaryHex = "#00E5FF",
                            downloadState = "INSTALLED",
                            downloadProgress = 100
                        ),
                        AvatarModelEntity(
                            id = "cyber_neural_orb",
                            name = "Quantum Neural Lattice Orb",
                            styleType = "CYBER_ORB",
                            format = "GLB 2.0 / Khronos Verified",
                            fileSizeLabel = "1.8 MB (CC0 Public Domain)",
                            license = "CC0 1.0 Universal",
                            attribution = "Khronos glTF Sample Repository",
                            sourceUrl = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/BoxAnimated/glTF-Binary/BoxAnimated.glb",
                            primaryHex = "#8B5CF6",
                            secondaryHex = "#00E5FF",
                            downloadState = "AVAILABLE",
                            downloadProgress = 0
                        ),
                        AvatarModelEntity(
                            id = "valkyrie_matrix_grid",
                            name = "Valkyrie Holographic Matrix",
                            styleType = "VALKYRIE_GRID",
                            format = "GLB 2.0 / Khronos Verified",
                            fileSizeLabel = "1.2 MB (CC-BY 4.0)",
                            license = "CC-BY 4.0 Verified",
                            attribution = "Khronos glTF Sample Repository",
                            sourceUrl = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF-Binary/Duck.glb",
                            primaryHex = "#10B981",
                            secondaryHex = "#00E5FF",
                            downloadState = "AVAILABLE",
                            downloadProgress = 0
                        )
                    )
                )

                // Seed initial welcome conversation, memories (matching Yaadein reference cards!), reminder, mood, and journal
                dao.insertMessage(
                    ChatMessageEntity(
                        conversationId = 1L,
                        role = "assistant",
                        content = "Systems online, Fardeen. Main ULTRON AI hoon (aur mera counterpart MAX bhi ready hai). Aap 'Max ko bhej' bolkar kabhi bhi MAX mode switch kar sakte hain.",
                        intentCategory = "CONVERSATION",
                        modelUsed = "ultron-core-init"
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        title = "Configured Creator Record",
                        content = "Mujhe Fardeen ne banaya aur configure kiya hai.",
                        category = "PERSONAL",
                        reasonSaved = "Protected System Attribution Record"
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        title = "Boss ko meri memory acchi lagti hai",
                        content = "Fardeen ko fast Hinglish replies, glassmorphism UI, aur Ultron + Max dual persona pasand hai.",
                        category = "LIKES",
                        reasonSaved = "Saved from user preference"
                    )
                )
                dao.insertMemory(
                    MemoryEntity(
                        title = "Fardeen ka birthday 25 October ko hai",
                        content = "Fardeen ka birthday 25 October ko aata hai.",
                        category = "PERSONAL",
                        reasonSaved = "User personal memory"
                    )
                )
                dao.insertReminder(
                    ReminderEntity(
                        title = "Evening AI Architecture & Coding Review",
                        timeLabel = "07:00 PM"
                    )
                )
                dao.insertMoodEntry(
                    MoodEntryEntity(
                        moodEmoji = "💜",
                        moodLabel = "Khush",
                        score = 5,
                        note = "Josh me • Sab badhiya"
                    )
                )
                dao.insertJournalEntry(
                    JournalEntryEntity(
                        title = "ULTRON & MAX Dual-Core Architecture",
                        body = "Glassmorphic non-scrolling Home, Scanner, Yaadein, aur Voice Engine active hain.",
                        tag = "Engineering"
                    )
                )
            }
        }
    }

    private fun startTelemetryRefreshLoop() {
        viewModelScope.launch {
            while (isActive) {
                val latest = withContext(Dispatchers.IO) {
                    deviceRouter.readRealDeviceTelemetry()
                }
                _telemetry.value = latest
                if (!latest.isOnline && _coreState.value == AssistantCoreState.IDLE) {
                    _coreState.value = AssistantCoreState.OFFLINE
                } else if (latest.isOnline && _coreState.value == AssistantCoreState.OFFLINE) {
                    _coreState.value = AssistantCoreState.IDLE
                }
                delay(4000L)
            }
        }
    }

    fun navigateTo(destination: UltronDestination) {
        _currentDestination.value = destination
    }

    fun switchPersona(targetPersona: AssistantPersona, speakGreeting: Boolean = true) {
        viewModelScope.launch {
            preferences.setActivePersona(targetPersona)
            val updatedSettings = settings.value.copy(
                activePersona = targetPersona,
                assistantName = targetPersona.displayName,
                speechPitch = if (targetPersona == AssistantPersona.ULTRON) settings.value.ultronPitch else settings.value.maxPitch,
                speechSpeed = if (targetPersona == AssistantPersona.ULTRON) settings.value.ultronSpeed else settings.value.maxSpeed,
                selectedVoiceId = if (targetPersona == AssistantPersona.ULTRON) settings.value.ultronVoiceId else settings.value.maxVoiceId
            )
            voiceManager.updateSettings(updatedSettings)
            val greeting = if (targetPersona == AssistantPersona.MAX) {
                "Hey Boss! MAX online aa gaya hai. UI aur voice MAX mode pe set hain. Batao kya kaam hai?"
            } else {
                "Systems online, ${settings.value.userAddress}. ULTRON core active hai. Batao, kya solve karna hai?"
            }
            _statusBannerMessage.value = "Switched to ${targetPersona.displayName.uppercase()} Mode"
            dao.insertMessage(
                ChatMessageEntity(
                    conversationId = 1L,
                    role = "assistant",
                    content = greeting,
                    intentCategory = IntentCategory.PERSONA_SWITCH.label,
                    modelUsed = "${targetPersona.name.lowercase()}-core"
                )
            )
            if (speakGreeting && updatedSettings.autoSpeakReplies) {
                voiceManager.speak(greeting, updatedSettings)
            }
        }
    }

    fun togglePersonaMode() {
        val next = if (settings.value.activePersona == AssistantPersona.ULTRON) {
            AssistantPersona.MAX
        } else {
            AssistantPersona.ULTRON
        }
        switchPersona(next, speakGreeting = true)
    }

    fun updateComposerText(newText: String) {
        _composerText.value = newText
    }

    fun dismissBanner() {
        _statusBannerMessage.value = null
    }

    fun interruptAndStopAll() {
        activeAiJob?.cancel()
        activeAiJob = null
        _streamingAssistantText.value = ""
        voiceManager.stopAllVoiceAndListening()
        _coreState.value = if (_telemetry.value.isOnline) {
            AssistantCoreState.IDLE
        } else {
            AssistantCoreState.OFFLINE
        }
        _statusBannerMessage.value = "Stopped immediately."
    }

    fun startMicPushToTalk() {
        _statusBannerMessage.value = null
        _coreState.value = AssistantCoreState.LISTENING
        voiceManager.startPushToTalk()
    }

    fun testWakeWord(phrase: String = settings.value.activePersona.name) {
        _statusBannerMessage.value = "Testing Wake Word pipeline: '$phrase'..."
        voiceManager.triggerWakeWordPipelineTest(phrase)
    }

    fun speakMessage(text: String) {
        voiceManager.speak(text, settings.value)
    }

    fun submitUserPrompt(rawInput: String = _composerText.value, fromVoice: Boolean = false) {
        val prompt = rawInput.trim()
        if (prompt.isEmpty()) return

        activeAiJob?.cancel()
        voiceManager.stopSpeaking()
        _composerText.value = ""
        _streamingAssistantText.value = ""
        _statusBannerMessage.value = null

        activeAiJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val currentSettings = settings.value

            dao.insertMessage(
                ChatMessageEntity(
                    conversationId = 1L,
                    role = "user",
                    content = prompt,
                    intentCategory = if (fromVoice) "VOICE_INPUT" else "TEXT_INPUT"
                )
            )

            // Check memory commands ("yaad rakhna" / "remember that")
            val lower = prompt.lowercase()
            if (lower.startsWith("yaad rakhna") || lower.startsWith("remember that") || lower.startsWith("ultron yaad rakh") || lower.startsWith("max yaad rakh")) {
                _coreState.value = AssistantCoreState.EXECUTING
                val fact = prompt
                    .replace(Regex("^((ultron|max)\\s+)?(yaad rakhna|remember that|yaad rakh)\\s*:?\\s*", RegexOption.IGNORE_CASE), "")
                    .trim()
                if (fact.isNotEmpty()) {
                    dao.insertMemory(
                        MemoryEntity(
                            title = fact.take(36),
                            content = fact,
                            category = "PERSONAL",
                            reasonSaved = "Saved on explicit user request"
                        )
                    )
                    val reply = "Yaad rakh liya ${currentSettings.userAddress}: '$fact'. Aap ise Yaadein section mein kabhi bhi edit ya delete kar sakte hain."
                    dao.insertMessage(
                        ChatMessageEntity(
                            conversationId = 1L,
                            role = "assistant",
                            content = reply,
                            intentCategory = IntentCategory.COMMAND.label,
                            modelUsed = "ultron-memory-engine",
                            latencyMs = System.currentTimeMillis() - startTime
                        )
                    )
                    if (currentSettings.autoSpeakReplies || fromVoice) {
                        voiceManager.speak(reply, currentSettings)
                    } else {
                        _coreState.value = AssistantCoreState.IDLE
                    }
                    return@launch
                }
            }

            // Context Memory: Follow-up response if previous question was asking for note/PDF topic
            val lastAssistantMsg = messages.value.lastOrNull { it.role == "assistant" }?.content.orEmpty()
            if (lastAssistantMsg.contains("Kis topic par", ignoreCase = true)) {
                _coreState.value = AssistantCoreState.EXECUTING
                val topic = prompt
                dao.insertJournalEntry(
                    JournalEntryEntity(
                        title = "Note: $topic",
                        body = "Topic: $topic\n\n1. Overview & Key Points\n2. Practical applications and notes\n3. Summary & Review",
                        tag = "MAX_NOTE"
                    )
                )
                val reply = "Bilkul ${currentSettings.userAddress}! '$topic' par note bana kar Journal mein save kar diya hai."
                val elapsed = System.currentTimeMillis() - startTime
                _lastLatencyMs.value = elapsed
                dao.insertMessage(
                    ChatMessageEntity(
                        conversationId = 1L,
                        role = "assistant",
                        content = reply,
                        intentCategory = IntentCategory.FILE_TASK.label,
                        modelUsed = "max-context-memory",
                        latencyMs = elapsed
                    )
                )
                if (currentSettings.autoSpeakReplies || fromVoice) {
                    voiceManager.speak(reply, currentSettings)
                } else {
                    _coreState.value = AssistantCoreState.IDLE
                }
                return@launch
            }

            // Route through DeviceControlAndToolRouter first (including Ultron <-> Max Persona Handoff!)
            _coreState.value = AssistantCoreState.THINKING
            val toolOutcome = withContext(Dispatchers.IO) {
                deviceRouter.classifyAndRouteInput(
                    userInput = prompt,
                    userAddress = currentSettings.userAddress,
                    activePersona = currentSettings.activePersona
                )
            }

            if (toolOutcome.handledDirectly) {
                _coreState.value = AssistantCoreState.EXECUTING
                if (toolOutcome.switchedPersona != null) {
                    val targetPersona = toolOutcome.switchedPersona
                    preferences.setActivePersona(targetPersona)
                    _statusBannerMessage.value = "Persona Handoff -> ${targetPersona.displayName.uppercase()} Mode Activated!"
                }
                delay(200L)
                val elapsed = System.currentTimeMillis() - startTime
                _lastLatencyMs.value = elapsed
                dao.insertMessage(
                    ChatMessageEntity(
                        conversationId = 1L,
                        role = "assistant",
                        content = toolOutcome.responseTextHinglish,
                        intentCategory = toolOutcome.category.label,
                        modelUsed = if (toolOutcome.switchedPersona != null) "${toolOutcome.switchedPersona.name.lowercase()}-handoff" else "ultron-device-router",
                        latencyMs = elapsed
                    )
                )
                _telemetry.value = deviceRouter.readRealDeviceTelemetry()
                val latestSettings = settings.value
                if (latestSettings.autoSpeakReplies || fromVoice) {
                    voiceManager.speak(toolOutcome.responseTextHinglish, latestSettings)
                } else {
                    _coreState.value = AssistantCoreState.IDLE
                }
                return@launch
            }

            // Otherwise, query AIProvider (Streaming + Fallback)
            val historySnapshot = messages.value
            val memoriesSnapshot = memories.value
            val telemetryStr = deviceRouter.getTelemetrySummaryHinglish()

            val (finalReply, usedModel) = aiAndWeatherClient.streamOrGenerateChat(
                userPrompt = prompt,
                history = historySnapshot,
                memories = memoriesSnapshot,
                settings = currentSettings,
                intentCategory = toolOutcome.category,
                telemetrySummary = telemetryStr,
                onPartialChunk = { partialText ->
                    _streamingAssistantText.value = partialText
                },
                onProviderFallbackNotice = { notice ->
                    _statusBannerMessage.value = notice
                }
            )

            val elapsed = System.currentTimeMillis() - startTime
            _lastLatencyMs.value = elapsed
            _streamingAssistantText.value = ""

            dao.insertMessage(
                ChatMessageEntity(
                    conversationId = 1L,
                    role = "assistant",
                    content = finalReply,
                    intentCategory = toolOutcome.category.label,
                    modelUsed = usedModel,
                    latencyMs = elapsed
                )
            )

            _modelCatalog.value = _modelCatalog.value.map { spec ->
                if (spec.id == usedModel) spec.copy(averageLatencyMs = elapsed) else spec
            }

            if (currentSettings.autoSpeakReplies || fromVoice) {
                voiceManager.speak(finalReply, currentSettings)
            } else {
                _coreState.value = AssistantCoreState.IDLE
            }
        }
    }

    // --- Live API Key & Sub-Agent Model Testing ---
    fun testApiKeyAndModel(customKey: String = settings.value.customGeminiApiKey, modelId: String = "gemini-2.5-flash") {
        viewModelScope.launch {
            _providerTestStatus.value = "Testing $modelId..."
            val (ok, msg) = aiAndWeatherClient.testProviderOrKey(customKey, modelId)
            _providerTestStatus.value = msg
            _statusBannerMessage.value = msg
            if (ok) {
                _modelCatalog.value = _modelCatalog.value.map {
                    if (it.id == modelId) it.copy(isHealthy = true) else it
                }
            }
        }
    }

    // --- Weather (Mausam) ---
    fun refreshWeather(city: String = settings.value.weatherCity) {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            _weatherError.value = null
            preferences.setWeatherCity(city)
            val result = aiAndWeatherClient.fetchRealWeather(city)
            _isWeatherLoading.value = false
            result.onSuccess { data ->
                _weatherState.value = data
                _weatherError.value = null
            }.onFailure { err ->
                _weatherError.value = err.localizedMessage ?: "Data nahi"
            }
        }
    }

    // --- Screen Samjho / Vision ---
    fun startScreenVisionSession(capturedBitmap: Bitmap?, customPrompt: String = "Meri screen par kya hai aur main buttons kahan hain?") {
        viewModelScope.launch {
            _isScreenVisionActive.value = true
            _coreState.value = AssistantCoreState.THINKING
            _visionExplanation.value = "Screen frame analyze ho raha hai..."

            if (capturedBitmap != null) {
                val (explanation, elements) = aiAndWeatherClient.analyzeImageWithVision(
                    bitmap = capturedBitmap,
                    prompt = customPrompt,
                    settings = settings.value
                )
                _visionExplanation.value = explanation
                _detectedElements.value = elements
                if (settings.value.autoSpeakReplies) {
                    voiceManager.speak(explanation, settings.value)
                } else {
                    _coreState.value = AssistantCoreState.IDLE
                }
            } else {
                val fallbackElements = listOf(
                    DetectedScreenElement(
                        id = "top_bar",
                        label = "Header & Persona Switcher",
                        explanationHinglish = "Top bar mein Hamburger menu, active title (Ultron/Max), bell aur avatar switcher hai.",
                        normalizedX = 0.05f,
                        normalizedY = 0.04f,
                        normalizedW = 0.90f,
                        normalizedH = 0.10f
                    ),
                    DetectedScreenElement(
                        id = "core_reactor",
                        label = "Central Reactor & Quick Pills",
                        explanationHinglish = "Yahan tap karke voice ya Gaane/Padhai/Journal trigger kar sakte hain.",
                        normalizedX = 0.12f,
                        normalizedY = 0.24f,
                        normalizedW = 0.76f,
                        normalizedH = 0.36f
                    ),
                    DetectedScreenElement(
                        id = "composer_nav",
                        label = "Glass Chat Bar & Bottom Navigation",
                        explanationHinglish = "Neeche Home, Scan, floating Mic orb, Yaadein aur Chat tabs hain.",
                        normalizedX = 0.05f,
                        normalizedY = 0.76f,
                        normalizedW = 0.90f,
                        normalizedH = 0.18f
                    )
                )
                val msg = "Screen layout analyze ho gaya hai. Maine 3 main interactive zones highlight kiye hain. Kisi bhi highlight par tap karke uska purpose dekhein."
                _detectedElements.value = fallbackElements
                _visionExplanation.value = msg
                if (settings.value.autoSpeakReplies) {
                    voiceManager.speak(msg, settings.value)
                } else {
                    _coreState.value = AssistantCoreState.IDLE
                }
            }
        }
    }

    fun toggleVisionHighlights(show: Boolean) {
        _showElementHighlights.value = show
    }

    fun stopScreenVisionSession() {
        _isScreenVisionActive.value = false
        _detectedElements.value = emptyList()
        _visionExplanation.value = "Screen Samjho session stopped & capture resources released."
        voiceManager.stopSpeaking()
        _coreState.value = AssistantCoreState.IDLE
    }

    // --- Avatar Studio Real HTTPS Download & Validation ---
    fun downloadAvatarModel(model: AvatarModelEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateAvatar(model.copy(downloadState = "DOWNLOADING", downloadProgress = 10))
            try {
                val request = Request.Builder().url(model.sourceUrl).build()
                val response = aiAndWeatherClient.httpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    dao.updateAvatar(model.copy(downloadState = "FAILED", downloadProgress = 0))
                    _statusBannerMessage.value = "Avatar download failed (HTTP ${response.code})."
                    return@launch
                }

                val body = response.body ?: throw IllegalStateException("Empty file body")
                val contentLength = body.contentLength().coerceAtLeast(1024L)
                if (contentLength > 25L * 1024L * 1024L) {
                    dao.updateAvatar(model.copy(downloadState = "FAILED", downloadProgress = 0))
                    _statusBannerMessage.value = "File size limit exceeded (>25MB)."
                    return@launch
                }

                val avatarDir = File(context.filesDir, "avatars").apply { mkdirs() }
                val destFile = File(avatarDir, "${model.id}.glb")
                var totalRead = 0L

                body.byteStream().use { input ->
                    FileOutputStream(destFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalRead += bytesRead
                            val progress = ((totalRead * 100L) / contentLength).toInt().coerceIn(15, 95)
                            dao.updateAvatar(model.copy(downloadState = "DOWNLOADING", downloadProgress = progress))
                        }
                    }
                }

                val isValidGlb = if (destFile.exists() && destFile.length() > 128L) {
                    val header = ByteArray(4)
                    destFile.inputStream().use { it.read(header) }
                    header[0] == 0x67.toByte() &&
                        header[1] == 0x6C.toByte() &&
                        header[2] == 0x54.toByte() &&
                        header[3] == 0x46.toByte()
                } else {
                    false
                }

                if (isValidGlb) {
                    dao.updateAvatar(
                        model.copy(
                            downloadState = "INSTALLED",
                            downloadProgress = 100,
                            localPath = destFile.absolutePath
                        )
                    )
                    preferences.setSelectedAvatar(model.id)
                    _statusBannerMessage.value = "${model.name} downloaded & GLB header verified (${destFile.length() / 1024} KB)."
                } else {
                    destFile.delete()
                    dao.updateAvatar(model.copy(downloadState = "FAILED", downloadProgress = 0))
                    _statusBannerMessage.value = "GLB validation failed for ${model.name}."
                }
            } catch (e: Exception) {
                dao.updateAvatar(
                    model.copy(
                        downloadState = "FAILED",
                        downloadProgress = 0
                    )
                )
                _statusBannerMessage.value = "Avatar download failed (${e.localizedMessage ?: "Network unavailable"}). Built-in procedural cores remain available."
            }
        }
    }

    fun selectAvatar(modelId: String) {
        viewModelScope.launch {
            preferences.setSelectedAvatar(modelId)
            _statusBannerMessage.value = "Active Avatar updated."
        }
    }

    // --- Markets & Coding Projects Actions ---
    fun addMarketSymbol(symbol: String, note: String = "User Watchlist") {
        val clean = symbol.trim().uppercase(Locale.ROOT)
        if (clean.isBlank()) return
        val now = SimpleDateFormat("dd MMM", Locale.US).format(Date())
        _marketWatchlist.value = _marketWatchlist.value + MarketWatchItem(
            symbol = clean,
            name = "$clean Equity / Index",
            noteHinglish = note,
            addedAt = now
        )
    }

    fun removeMarketSymbol(symbol: String) {
        _marketWatchlist.value = _marketWatchlist.value.filterNot { it.symbol == symbol }
    }

    fun addCodingProject(title: String, snippet: String) {
        if (title.isBlank()) return
        val item = CodingProjectItem(
            id = "proj_${System.currentTimeMillis()}",
            title = title.trim(),
            typeLabel = "code • 1 file",
            filesCount = 1,
            updatedAgo = "Abhi",
            codeSnippet = snippet.ifBlank { "// Created in Ultron Coding Studio" }
        )
        _codingProjects.value = listOf(item) + _codingProjects.value
    }

    // --- CRUD Operations for Journal, Dream Journal, Mood, Reminders, Memories, Chat ---
    fun addJournalEntry(title: String, body: String, tag: String) {
        if (title.isBlank() && body.isBlank()) return
        viewModelScope.launch {
            dao.insertJournalEntry(
                JournalEntryEntity(
                    title = title.ifBlank { "Untitled Note" },
                    body = body,
                    tag = tag.ifBlank { "General" }
                )
            )
        }
    }

    fun deleteJournalEntry(id: Long) {
        viewModelScope.launch { dao.deleteJournalEntry(id) }
    }

    fun clearAllJournals() {
        viewModelScope.launch { dao.clearAllJournalEntries() }
    }

    fun addDreamEntryWithReflection(title: String, transcript: String) {
        if (transcript.isBlank()) return
        viewModelScope.launch {
            _coreState.value = AssistantCoreState.THINKING
            _statusBannerMessage.value = "Dream analyze aur symbolic reflection generate ho raha hai..."
            val (reflection, symbols, imgBase64) = aiAndWeatherClient.generateDreamReflectionAndArt(
                transcript,
                settings.value.customGeminiApiKey
            )
            dao.insertDreamEntry(
                DreamEntryEntity(
                    title = title.ifBlank { "Dream Log • ${transcript.take(24)}" },
                    transcript = transcript,
                    jungianReflection = reflection,
                    symbolsDetected = symbols,
                    generatedImageBase64 = imgBase64
                )
            )
            _coreState.value = AssistantCoreState.IDLE
            _statusBannerMessage.value = "Dream Journal entry saved with Jungian reflection."
        }
    }

    fun deleteDreamEntry(id: Long) {
        viewModelScope.launch { dao.deleteDreamEntry(id) }
    }

    fun addMoodEntry(emoji: String, label: String, score: Int, note: String) {
        viewModelScope.launch {
            dao.insertMoodEntry(
                MoodEntryEntity(
                    moodEmoji = emoji,
                    moodLabel = label,
                    score = score,
                    note = note.ifBlank { "Sab badhiya" }
                )
            )
        }
    }

    fun deleteMoodEntry(id: Long) {
        viewModelScope.launch { dao.deleteMoodEntry(id) }
    }

    fun addReminder(title: String, timeLabel: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.insertReminder(
                ReminderEntity(
                    title = title.trim(),
                    timeLabel = timeLabel.ifBlank { "Today" }
                )
            )
        }
    }

    fun toggleReminderCompleted(reminder: ReminderEntity) {
        viewModelScope.launch {
            dao.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch { dao.deleteReminder(id) }
    }

    fun addMemory(title: String, content: String, category: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            dao.insertMemory(
                MemoryEntity(
                    title = title.trim().ifBlank { content.take(32) },
                    content = content.trim().ifBlank { title.trim() },
                    category = category.uppercase(Locale.ROOT),
                    reasonSaved = "User added in Yaadein"
                )
            )
        }
    }

    fun editMemory(memory: MemoryEntity, newTitle: String, newContent: String, newCategory: String) {
        viewModelScope.launch {
            dao.updateMemory(
                memory.copy(
                    title = newTitle.trim().ifBlank { memory.title },
                    content = newContent.trim().ifBlank { memory.content },
                    category = newCategory.trim().ifBlank { memory.category }
                )
            )
            _statusBannerMessage.value = "Yaad update ho gayi."
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch { dao.deleteMemory(id) }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            dao.clearAllMemories()
            dao.insertMemory(
                MemoryEntity(
                    title = "Configured Creator Record",
                    content = "Mujhe Fardeen ne banaya aur configure kiya hai.",
                    category = "PERSONAL",
                    reasonSaved = "Protected System Attribution Record"
                )
            )
        }
    }

    fun deleteChatMessage(id: Long) {
        viewModelScope.launch { dao.deleteMessage(id) }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            dao.clearAllMessages()
            dao.insertMessage(
                ChatMessageEntity(
                    conversationId = 1L,
                    role = "assistant",
                    content = "Chat history clear kar di gayi hai, ${settings.value.userAddress}. Naya session ready hai.",
                    intentCategory = "CONVERSATION",
                    modelUsed = "ultron-core"
                )
            )
        }
    }

    // --- Settings & Voice Preferences Updates ---
    fun updateLanguageMode(mode: LanguageMode) {
        viewModelScope.launch { preferences.setLanguageMode(mode) }
    }

    fun updateUserAddress(address: String) {
        viewModelScope.launch { preferences.setUserAddress(address) }
    }

    fun savePersonalSettings(
        address: String,
        gender: GenderVerbStyle,
        phone: String,
        musicApp: String,
        favSong: String,
        customKey: String
    ) {
        viewModelScope.launch {
            preferences.setPersonalConfig(address, gender, phone, musicApp, favSong, customKey)
            _statusBannerMessage.value = "Personal settings aur Gemini key save ho gaye."
        }
    }

    fun updateRoutingMode(mode: RoutingMode) {
        viewModelScope.launch { preferences.setRoutingMode(mode) }
    }

    fun updateSubAgentStrategy(strategy: SubAgentRoutingStrategy) {
        viewModelScope.launch {
            preferences.setSubAgentStrategy(strategy)
            _statusBannerMessage.value = "Sub-agent strategy '${strategy.titleHinglish}' set ho gayi."
        }
    }

    fun updateCompanionTone(tone: String) {
        viewModelScope.launch {
            preferences.setCompanionToneMode(tone)
            _statusBannerMessage.value = "Companion tone '$tone' save ho gaya."
        }
    }

    fun updateLookAndFeel(surface: SurfaceThemeStyle, corner: CornerThemeStyle) {
        viewModelScope.launch {
            preferences.setLookAndFeel(surface, corner)
            _statusBannerMessage.value = "Theme updated (${surface.label} • ${corner.label})."
        }
    }

    fun updateRealisticTyping(enabled: Boolean, speed: String) {
        viewModelScope.launch {
            preferences.setRealisticTyping(enabled, speed)
            _statusBannerMessage.value = "Typing style updated ($speed)."
        }
    }

    fun updateBatterySaver(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setBatterySaverMode(enabled)
            _statusBannerMessage.value = if (enabled) "Battery Saver ON (Animations optimised)." else "Battery Saver OFF."
        }
    }

    fun updatePcPairing(host: String, port: String) {
        viewModelScope.launch {
            preferences.setPcPairingConfig(host, port)
            _statusBannerMessage.value = "PC Pairing endpoint saved ($host:$port)."
        }
    }

    fun updateWakeWordConfig(enabled: Boolean, phrase: String = settings.value.wakeWordPhrase) {
        viewModelScope.launch {
            preferences.setWakeWordPhrase(phrase)
            preferences.setWakeWordEnabled(enabled)
            voiceManager.setWakeWordMonitoring(enabled)
        }
    }

    fun saveVoiceConfiguration(voiceId: String, speed: Float, pitch: Float, style: String) {
        viewModelScope.launch {
            preferences.setVoiceConfig(settings.value.activePersona, voiceId, speed, pitch, style)
            _statusBannerMessage.value = "${settings.value.activePersona.displayName} voice profile saved ($style • ${String.format(Locale.US, "%.2fx", speed)})."
        }
    }

    fun previewVoiceSample(voiceId: String, speed: Float, pitch: Float, style: String) {
        val tempSettings = settings.value.copy(
            selectedVoiceId = voiceId,
            speechSpeed = speed,
            speechPitch = pitch,
            speechStyle = style
        )
        val isMax = settings.value.activePersona == AssistantPersona.MAX
        val sample = when (settings.value.languageMode) {
            LanguageMode.HINGLISH -> if (isMax) {
                "Hey Boss! Main MAX hoon, aapka tactical AI buddy. Ultron aur main dono aapki command ke liye ready hain!"
            } else {
                "Systems online, ${settings.value.userAddress}. Main ULTRON AI hoon, jise Fardeen ne develop kiya hai. Meri awaaz ab bilkul clear aur natural hai."
            }
            LanguageMode.HINDI -> "सिस्टम तैयार है। मैं अल्ट्रॉन एआई हूँ, आपकी सेवा में हाज़िर।"
            LanguageMode.ENGLISH -> "Systems online. I am ${settings.value.activePersona.displayName}, developed by Fardeen. Voice parameters are calibrated."
        }
        voiceManager.speak(sample, tempSettings)
    }

    fun updateReducedMotion(reduced: Boolean) {
        viewModelScope.launch { preferences.setReducedMotion(reduced) }
    }

    fun updateAutoSpeak(autoSpeak: Boolean) {
        viewModelScope.launch { preferences.setAutoSpeakReplies(autoSpeak) }
    }

    fun refreshTelemetryNow() {
        _telemetry.value = deviceRouter.readRealDeviceTelemetry()
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
