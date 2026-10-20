package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.UltronDestination
import com.example.ui.UltronMainViewModel
import com.example.ui.components.UltronBottomNavigation
import com.example.ui.components.UltronCommandInputBar
import com.example.ui.components.UltronCompactTopBar
import com.example.ui.components.UltronHamburgerDrawer
import com.example.ui.screens.UltronAajRemindersScreen
import com.example.ui.screens.UltronAccessibilityGuideScreen
import com.example.ui.screens.UltronAiModelsScreen
import com.example.ui.screens.UltronAvatarStudioScreen
import com.example.ui.screens.UltronAwaazStudioScreen
import com.example.ui.screens.UltronChatAndHistoryScreen
import com.example.ui.screens.UltronCodingScreen
import com.example.ui.screens.UltronControlCenterScreen
import com.example.ui.screens.UltronDreamJournalScreen
import com.example.ui.screens.UltronGaaneScreen
import com.example.ui.screens.UltronGharHomeScreen
import com.example.ui.screens.UltronJournalScreen
import com.example.ui.screens.UltronLiveVoiceScreen
import com.example.ui.screens.UltronLookAndFeelScreen
import com.example.ui.screens.UltronMarketingAndDocsScreen
import com.example.ui.screens.UltronMarketsScreen
import com.example.ui.screens.UltronMausamScreen
import com.example.ui.screens.UltronMoodScreen
import com.example.ui.screens.UltronPadhaiScreen
import com.example.ui.screens.UltronPersonaSwitcherScreen
import com.example.ui.screens.UltronPersonalSettingsScreen
import com.example.ui.screens.UltronScanScreen
import com.example.ui.screens.UltronSettingsAndDiagnosticsScreen
import com.example.ui.screens.UltronSettingsHubScreen
import com.example.ui.screens.UltronToolsHubScreen
import com.example.ui.screens.UltronUtilityDetailScreen
import com.example.ui.screens.UltronVisionCenterScreen
import com.example.ui.screens.UltronYaadeinMemoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.UltronCyan
import com.example.ui.theme.UltronGold
import com.example.ui.theme.UltronObsidian
import com.example.ui.theme.UltronSurfaceElevated
import com.example.ui.theme.UltronTextPrimary
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                UltronRootApp()
            }
        }
    }
}

@Composable
fun UltronRootApp(
    mainViewModel: UltronMainViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentDestination by mainViewModel.currentDestination.collectAsStateWithLifecycle()
    val coreState by mainViewModel.coreState.collectAsStateWithLifecycle()
    val settings by mainViewModel.settings.collectAsStateWithLifecycle()
    val composerText by mainViewModel.composerText.collectAsStateWithLifecycle()
    val streamingText by mainViewModel.streamingAssistantText.collectAsStateWithLifecycle()
    val statusBanner by mainViewModel.statusBannerMessage.collectAsStateWithLifecycle()
    val lastLatencyMs by mainViewModel.lastLatencyMs.collectAsStateWithLifecycle()
    val providerTestStatus by mainViewModel.providerTestStatus.collectAsStateWithLifecycle()

    val messages by mainViewModel.messages.collectAsStateWithLifecycle()
    val memories by mainViewModel.memories.collectAsStateWithLifecycle()
    val journalEntries by mainViewModel.journalEntries.collectAsStateWithLifecycle()
    val dreamEntries by mainViewModel.dreamEntries.collectAsStateWithLifecycle()
    val moodEntries by mainViewModel.moodEntries.collectAsStateWithLifecycle()
    val reminders by mainViewModel.reminders.collectAsStateWithLifecycle()
    val avatars by mainViewModel.avatars.collectAsStateWithLifecycle()
    val marketWatchlist by mainViewModel.marketWatchlist.collectAsStateWithLifecycle()
    val codingProjects by mainViewModel.codingProjects.collectAsStateWithLifecycle()

    val telemetry by mainViewModel.telemetry.collectAsStateWithLifecycle()
    val weatherData by mainViewModel.weatherState.collectAsStateWithLifecycle()
    val weatherError by mainViewModel.weatherError.collectAsStateWithLifecycle()
    val isWeatherLoading by mainViewModel.isWeatherLoading.collectAsStateWithLifecycle()
    val installedApps by mainViewModel.installedApps.collectAsStateWithLifecycle()
    val modelCatalog by mainViewModel.modelCatalog.collectAsStateWithLifecycle()

    val availableVoices by mainViewModel.availableVoices.collectAsStateWithLifecycle()
    val ttsEngineLabel by mainViewModel.ttsEngineLabel.collectAsStateWithLifecycle()
    val isListeningMic by mainViewModel.isListeningMic.collectAsStateWithLifecycle()
    val isSpeaking by mainViewModel.isSpeaking.collectAsStateWithLifecycle()
    val micAmplitude by mainViewModel.micAmplitude.collectAsStateWithLifecycle()
    val wakeWordStatus by mainViewModel.wakeWordStatus.collectAsStateWithLifecycle()
    val voiceDiagnostics by mainViewModel.voiceDiagnostics.collectAsStateWithLifecycle()

    val isVisionActive by mainViewModel.isScreenVisionActive.collectAsStateWithLifecycle()
    val visionExplanation by mainViewModel.visionExplanation.collectAsStateWithLifecycle()
    val detectedElements by mainViewModel.detectedElements.collectAsStateWithLifecycle()
    val showHighlights by mainViewModel.showElementHighlights.collectAsStateWithLifecycle()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            mainViewModel.startMicPushToTalk()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // BackHandler on all secondary screens:Settings sub-screens return to SETTINGS, others return to GHAR
    if (currentDestination != UltronDestination.GHAR) {
        BackHandler {
            if (currentDestination.section == "SETTINGS") {
                mainViewModel.navigateTo(UltronDestination.SETTINGS)
            } else {
                mainViewModel.navigateTo(UltronDestination.GHAR)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            UltronHamburgerDrawer(
                currentDestination = currentDestination,
                configuredCreator = settings.configuredCreator,
                activePersona = settings.activePersona,
                onSelectDestination = { dest ->
                    mainViewModel.navigateTo(dest)
                },
                onTogglePersona = {
                    mainViewModel.togglePersonaMode()
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = UltronObsidian,
            topBar = {
                UltronCompactTopBar(
                    telemetry = telemetry,
                    currentDestination = currentDestination,
                    activePersona = settings.activePersona,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onTitleClick = { mainViewModel.navigateTo(UltronDestination.GHAR) },
                    onBellClick = { mainViewModel.navigateTo(UltronDestination.AAJ) },
                    onAvatarClick = { mainViewModel.navigateTo(UltronDestination.PERSONA_SWITCHER) }
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                ) {
                    UltronCommandInputBar(
                        composerText = composerText,
                        coreState = coreState,
                        isSpeaking = isSpeaking,
                        isListening = isListeningMic,
                        activePersona = settings.activePersona,
                        onTextChange = { mainViewModel.updateComposerText(it) },
                        onSendClick = { mainViewModel.submitUserPrompt() },
                        onMicClick = {
                            if (hasMicPermission) {
                                mainViewModel.startMicPushToTalk()
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onStopClick = { mainViewModel.interruptAndStopAll() },
                        onAttachClick = { mainViewModel.navigateTo(UltronDestination.SCAN) }
                    )
                    UltronBottomNavigation(
                        currentDestination = currentDestination,
                        isListening = isListeningMic,
                        activePersona = settings.activePersona,
                        onNavigate = { mainViewModel.navigateTo(it) },
                        onCenterLiveMicClick = {
                            if (currentDestination != UltronDestination.LIVE_VOICE) {
                                mainViewModel.navigateTo(UltronDestination.LIVE_VOICE)
                            }
                            if (hasMicPermission) {
                                mainViewModel.startMicPushToTalk()
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(UltronObsidian)
                    .padding(innerPadding)
            ) {
                // Non-intrusive Status / Error / Fallback Banner
                AnimatedVisibility(visible = !statusBanner.isNullOrBlank()) {
                    Surface(
                        color = UltronSurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, UltronGold.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .testTag("status_banner_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = UltronGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = statusBanner.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = UltronTextPrimary
                                )
                            }
                            if (statusBanner?.contains("permission", ignoreCase = true) == true) {
                                TextButton(onClick = { mainViewModel.deviceRouter.openAppPermissionSettings() }) {
                                    Text("OPEN SETTINGS", color = UltronCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                            IconButton(onClick = { mainViewModel.dismissBanner() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = UltronTextPrimary
                                )
                            }
                        }
                    }
                }

                // Active Destination Content
                Box(modifier = Modifier.weight(1f)) {
                    when (currentDestination) {
                        UltronDestination.GHAR -> UltronGharHomeScreen(
                            settings = settings,
                            coreState = coreState,
                            wakeWordStatus = wakeWordStatus,
                            voiceDiagnostics = voiceDiagnostics,
                            micAmplitude = micAmplitude,
                            messages = messages,
                            streamingText = streamingText,
                            weatherData = weatherData,
                            weatherError = weatherError,
                            reminders = reminders,
                            latestMood = moodEntries.firstOrNull(),
                            telemetry = telemetry,
                            onCoreTap = {
                                if (isSpeaking || isListeningMic) {
                                    mainViewModel.interruptAndStopAll()
                                } else if (hasMicPermission) {
                                    mainViewModel.startMicPushToTalk()
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            onNavigate = { mainViewModel.navigateTo(it) },
                            onQuickPrompt = { mainViewModel.submitUserPrompt(it) },
                            onSpeakText = { mainViewModel.speakMessage(it) },
                            onTestWakeWord = { mainViewModel.testWakeWord(settings.activePersona.name) },
                            onToggleWakeWord = { enabled ->
                                if (enabled && !hasMicPermission) {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    mainViewModel.updateWakeWordConfig(enabled)
                                }
                            },
                            onRequestMicPermission = {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            },
                            onTogglePersona = {
                                mainViewModel.togglePersonaMode()
                            }
                        )

                        UltronDestination.PERSONA_SWITCHER, UltronDestination.SETTINGS_SECRET ->
                            UltronPersonaSwitcherScreen(
                                settings = settings,
                                onSwitchPersona = { mainViewModel.switchPersona(it) },
                                onUpdateTone = { mainViewModel.updateCompanionTone(it) }
                            )

                        UltronDestination.CHAT -> UltronChatAndHistoryScreen(
                            messages = messages,
                            streamingText = streamingText,
                            lastLatencyMs = lastLatencyMs,
                            onSpeakMessage = { mainViewModel.speakMessage(it) },
                            onDeleteMessage = { mainViewModel.deleteChatMessage(it) },
                            onClearAll = { mainViewModel.clearChatHistory() },
                            onQuickPrompt = { mainViewModel.submitUserPrompt(it) }
                        )

                        UltronDestination.AWAAZ_STUDIO -> UltronAwaazStudioScreen(
                            settings = settings,
                            availableVoices = availableVoices,
                            ttsEngineLabel = ttsEngineLabel,
                            isSpeaking = isSpeaking,
                            wakeWordStatus = wakeWordStatus,
                            onPreviewVoice = { id, spd, ptch, stl ->
                                mainViewModel.previewVoiceSample(id, spd, ptch, stl)
                            },
                            onStopVoice = { mainViewModel.interruptAndStopAll() },
                            onSaveVoiceConfig = { id, spd, ptch, stl ->
                                mainViewModel.saveVoiceConfiguration(id, spd, ptch, stl)
                            },
                            onUpdateLanguage = { mainViewModel.updateLanguageMode(it) },
                            onUpdateWakeConfig = { enabled, phrase ->
                                mainViewModel.updateWakeWordConfig(enabled, phrase)
                            },
                            onTestWakeWord = { mainViewModel.testWakeWord(it) },
                            onRefreshVoices = { mainViewModel.voiceManager.refreshAvailableVoices() },
                            onRequestMicPermission = {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            },
                            onOpenAppSettings = { mainViewModel.deviceRouter.openAppPermissionSettings() }
                        )

                        UltronDestination.LIVE_VOICE -> UltronLiveVoiceScreen(
                            settings = settings,
                            coreState = coreState,
                            wakeWordStatus = wakeWordStatus,
                            micAmplitude = micAmplitude,
                            isListening = isListeningMic,
                            isSpeaking = isSpeaking,
                            lastTranscriptOrReply = streamingText.ifBlank {
                                messages.lastOrNull()?.content.orEmpty()
                            },
                            onStartPushToTalk = {
                                if (hasMicPermission) {
                                    mainViewModel.startMicPushToTalk()
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            onStopAll = { mainViewModel.interruptAndStopAll() },
                            onTestWakeWord = { mainViewModel.testWakeWord(settings.activePersona.name) },
                            onToggleWakeWord = { mainViewModel.updateWakeWordConfig(it) }
                        )

                        UltronDestination.AI_MODELS -> UltronAiModelsScreen(
                            settings = settings,
                            models = modelCatalog,
                            isApiKeyConfigured = mainViewModel.aiAndWeatherClient.isApiKeyConfigured(settings.customGeminiApiKey),
                            providerTestStatus = providerTestStatus,
                            onSelectRoutingMode = { mainViewModel.updateRoutingMode(it) },
                            onSelectSubAgentStrategy = { mainViewModel.updateSubAgentStrategy(it) },
                            onTestModel = { modelId -> mainViewModel.testApiKeyAndModel(settings.customGeminiApiKey, modelId) }
                        )

                        UltronDestination.AVATAR_STUDIO -> UltronAvatarStudioScreen(
                            settings = settings,
                            avatars = avatars,
                            coreState = coreState,
                            micAmplitude = micAmplitude,
                            onSelectAvatar = { mainViewModel.selectAvatar(it) },
                            onDownloadAvatar = { mainViewModel.downloadAvatarModel(it) }
                        )

                        UltronDestination.CONTROL_CENTER -> UltronControlCenterScreen(
                            telemetry = telemetry,
                            installedApps = installedApps,
                            onRefreshTelemetry = { mainViewModel.refreshTelemetryNow() },
                            onSetMediaVolume = {
                                mainViewModel.deviceRouter.setMediaVolume(it)
                                mainViewModel.refreshTelemetryNow()
                            },
                            onToggleFlashlight = {
                                mainViewModel.deviceRouter.toggleFlashlight(it)
                                mainViewModel.refreshTelemetryNow()
                            },
                            onOpenSettingsAction = {
                                mainViewModel.deviceRouter.openAndroidSettingsPanel(it)
                            },
                            onLaunchApp = { appName ->
                                mainViewModel.submitUserPrompt("$appName kholo")
                            }
                        )

                        UltronDestination.VISION_CENTER -> UltronVisionCenterScreen(
                            isVisionActive = isVisionActive,
                            visionExplanation = visionExplanation,
                            detectedElements = detectedElements,
                            showHighlights = showHighlights,
                            onStartVisionWithBitmap = { bmp, prompt ->
                                mainViewModel.startScreenVisionSession(bmp, prompt)
                            },
                            onToggleHighlights = { mainViewModel.toggleVisionHighlights(it) },
                            onReadScreenExplanation = { mainViewModel.speakMessage(it) },
                            onStopVision = { mainViewModel.stopScreenVisionSession() }
                        )

                        UltronDestination.SCAN -> UltronScanScreen(
                            hasCameraPermission = hasCameraPermission,
                            visionExplanation = visionExplanation,
                            onRequestCameraPermission = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            onAnalyzeCapturedBitmap = { bmp, prompt ->
                                mainViewModel.startScreenVisionSession(bmp, prompt)
                            }
                        )

                        UltronDestination.GAANE -> UltronGaaneScreen(
                            onOpenYouTubeQuery = { query ->
                                val (_, msg) = mainViewModel.deviceRouter.openYouTubeSearchOrHome(
                                    query,
                                    settings.userAddress
                                )
                                mainViewModel.speakMessage(msg)
                            }
                        )

                        UltronDestination.PADHAI -> UltronPadhaiScreen(
                            messages = messages,
                            streamingText = streamingText,
                            onSubmitStudyPrompt = { mainViewModel.submitUserPrompt(it) },
                            onNavigateToChat = { mainViewModel.navigateTo(UltronDestination.CHAT) }
                        )

                        UltronDestination.JOURNAL -> UltronJournalScreen(
                            entries = journalEntries,
                            onAddEntry = { t, b, tag -> mainViewModel.addJournalEntry(t, b, tag) },
                            onDeleteEntry = { mainViewModel.deleteJournalEntry(it) },
                            onClearAll = { mainViewModel.clearAllJournals() }
                        )

                        UltronDestination.DREAM_JOURNAL -> UltronDreamJournalScreen(
                            dreams = dreamEntries,
                            onRecordVoiceForDream = {
                                if (hasMicPermission) {
                                    mainViewModel.startMicPushToTalk()
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            onSaveDream = { t, tr -> mainViewModel.addDreamEntryWithReflection(t, tr) },
                            onDeleteDream = { mainViewModel.deleteDreamEntry(it) }
                        )

                        UltronDestination.MAUSAM -> UltronMausamScreen(
                            weatherData = weatherData,
                            weatherError = weatherError,
                            isLoading = isWeatherLoading,
                            currentCity = settings.weatherCity,
                            onRefreshCity = { mainViewModel.refreshWeather(it) }
                        )

                        UltronDestination.AAJ -> UltronAajRemindersScreen(
                            reminders = reminders,
                            onAddReminder = { t, tm -> mainViewModel.addReminder(t, tm) },
                            onToggleReminder = { mainViewModel.toggleReminderCompleted(it) },
                            onDeleteReminder = { mainViewModel.deleteReminder(it) }
                        )

                        UltronDestination.MOOD -> UltronMoodScreen(
                            moods = moodEntries,
                            onAddMood = { em, lbl, sc, nt ->
                                mainViewModel.addMoodEntry(em, lbl, sc, nt)
                            },
                            onDeleteMood = { mainViewModel.deleteMoodEntry(it) }
                        )

                        UltronDestination.YAADEIN -> UltronYaadeinMemoryScreen(
                            memories = memories,
                            onAddMemory = { t, c, cat -> mainViewModel.addMemory(t, c, cat) },
                            onDeleteMemory = { mainViewModel.deleteMemory(it) },
                            onClearMemories = { mainViewModel.clearAllMemories() }
                        )

                        UltronDestination.MARKETS -> UltronMarketsScreen(
                            watchlist = marketWatchlist,
                            onAddSymbol = { sym, note -> mainViewModel.addMarketSymbol(sym, note) },
                            onRemoveSymbol = { mainViewModel.removeMarketSymbol(it) },
                            onAskMarketAi = { prompt ->
                                mainViewModel.submitUserPrompt(prompt)
                                mainViewModel.navigateTo(UltronDestination.CHAT)
                            }
                        )

                        UltronDestination.MARKETING -> UltronMarketingAndDocsScreen(
                            isMarketing = true,
                            onSubmitAiTask = { prompt ->
                                mainViewModel.submitUserPrompt(prompt)
                                mainViewModel.navigateTo(UltronDestination.CHAT)
                            },
                            onOpenScanner = { mainViewModel.navigateTo(UltronDestination.SCAN) }
                        )

                        UltronDestination.DOCUMENTS -> UltronMarketingAndDocsScreen(
                            isMarketing = false,
                            onSubmitAiTask = { prompt ->
                                mainViewModel.submitUserPrompt(prompt)
                                mainViewModel.navigateTo(UltronDestination.CHAT)
                            },
                            onOpenScanner = { mainViewModel.navigateTo(UltronDestination.SCAN) }
                        )

                        UltronDestination.CODING -> UltronCodingScreen(
                            projects = codingProjects,
                            onCreateProject = { title, snippet -> mainViewModel.addCodingProject(title, snippet) },
                            onAskCodingAi = { prompt ->
                                mainViewModel.submitUserPrompt(prompt)
                                mainViewModel.navigateTo(UltronDestination.CHAT)
                            }
                        )

                        UltronDestination.ACCESSIBILITY_GUIDE -> UltronAccessibilityGuideScreen(
                            hasMicPermission = hasMicPermission,
                            onOpenAccessibilitySettings = { mainViewModel.deviceRouter.openAccessibilitySettings() },
                            onOpenBatterySettings = { mainViewModel.deviceRouter.openBatteryOptimizationSettings() },
                            onOpenAppPermissions = { mainViewModel.deviceRouter.openAppPermissionSettings() },
                            onOpenVisionCenter = { mainViewModel.navigateTo(UltronDestination.VISION_CENTER) }
                        )

                        UltronDestination.SETTINGS -> UltronSettingsHubScreen(
                            settings = settings,
                            memoriesCount = memories.size,
                            journalsCount = journalEntries.size,
                            chatsCount = messages.size,
                            onNavigate = { mainViewModel.navigateTo(it) },
                            onToggleBatterySaver = { mainViewModel.updateBatterySaver(it) }
                        )

                        UltronDestination.SETTINGS_PERSONAL -> UltronPersonalSettingsScreen(
                            settings = settings,
                            providerTestStatus = providerTestStatus,
                            onSavePersonal = { addr, gen, ph, mus, fav, key ->
                                mainViewModel.savePersonalSettings(addr, gen, ph, mus, fav, key)
                            },
                            onTestApiKey = { key -> mainViewModel.testApiKeyAndModel(key) }
                        )

                        UltronDestination.SETTINGS_THEME, UltronDestination.SETTINGS_TYPING ->
                            UltronLookAndFeelScreen(
                                settings = settings,
                                onUpdateLookAndFeel = { surf, corn -> mainViewModel.updateLookAndFeel(surf, corn) },
                                onUpdateRealisticTyping = { en, spd -> mainViewModel.updateRealisticTyping(en, spd) }
                            )

                        UltronDestination.DIAGNOSTICS, UltronDestination.SETTINGS_ADVANCED ->
                            UltronSettingsAndDiagnosticsScreen(
                                settings = settings,
                                hasMicPermission = hasMicPermission,
                                hasCameraPermission = hasCameraPermission,
                                isApiKeyConfigured = mainViewModel.aiAndWeatherClient.isApiKeyConfigured(settings.customGeminiApiKey),
                                telemetry = telemetry,
                                onUpdateUserAddress = { mainViewModel.updateUserAddress(it) },
                                onUpdateLanguage = { mainViewModel.updateLanguageMode(it) },
                                onUpdateReducedMotion = { mainViewModel.updateReducedMotion(it) },
                                onUpdateAutoSpeak = { mainViewModel.updateAutoSpeak(it) },
                                onRequestMicPermission = {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                onRequestCameraPermission = {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                onOpenSystemAppSettings = {
                                    mainViewModel.deviceRouter.openAppPermissionSettings()
                                }
                            )

                        UltronDestination.TOOLS -> UltronToolsHubScreen(
                            activePersona = settings.activePersona,
                            onNavigate = { mainViewModel.navigateTo(it) },
                            onSendEmailIntent = { to, subj, body -> mainViewModel.deviceRouter.sendEmailIntent(to, subj, body) },
                            onSendWhatsAppIntent = { ph, msg -> mainViewModel.deviceRouter.sendWhatsAppOrSmsIntent(ph, msg) },
                            onTriggerDialerIntent = { num -> mainViewModel.deviceRouter.triggerEmergencyDialIntent(num) }
                        )

                        else -> UltronUtilityDetailScreen(
                            destination = currentDestination,
                            settings = settings,
                            memoriesCount = memories.size,
                            journalsCount = journalEntries.size,
                            chatsCount = messages.size,
                            onUpdatePcPairing = { host, port -> mainViewModel.updatePcPairing(host, port) },
                            onSendEmail = { to, subj, body -> mainViewModel.deviceRouter.sendEmailIntent(to, subj, body) },
                            onSendWhatsApp = { ph, msg -> mainViewModel.deviceRouter.sendWhatsAppOrSmsIntent(ph, msg) },
                            onTriggerEmergencyDial = { num -> mainViewModel.deviceRouter.triggerEmergencyDialIntent(num) }
                        )
                    }
                }
            }
        }
    }
}
