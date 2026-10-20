package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import com.example.domain.model.UltronDestination
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AvatarModelEntity
import com.example.data.local.UltronSettingsState
import com.example.domain.model.AIModelSpec
import com.example.domain.model.AssistantCoreState
import com.example.domain.model.LanguageMode
import com.example.domain.model.RoutingMode
import com.example.domain.model.VoiceProfileInfo
import com.example.domain.model.WakeWordStatus
import com.example.ui.components.UltronAnimatedCore
import com.example.ui.theme.UltronCrimson
import com.example.ui.theme.UltronCyan
import com.example.ui.theme.UltronEmerald
import com.example.ui.theme.UltronGlassBorder
import com.example.ui.theme.UltronGold
import com.example.ui.theme.UltronObsidian
import com.example.ui.theme.UltronSurface
import com.example.ui.theme.UltronSurfaceElevated
import com.example.ui.theme.UltronTextMuted
import com.example.ui.theme.UltronTextPrimary
import com.example.ui.theme.UltronTextSecondary
import com.example.ui.theme.UltronViolet
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UltronAwaazStudioScreen(
    settings: UltronSettingsState,
    availableVoices: List<VoiceProfileInfo>,
    ttsEngineLabel: String,
    isSpeaking: Boolean,
    wakeWordStatus: WakeWordStatus,
    onPreviewVoice: (String, Float, Float, String) -> Unit,
    onStopVoice: () -> Unit,
    onSaveVoiceConfig: (String, Float, Float, String) -> Unit,
    onUpdateLanguage: (LanguageMode) -> Unit,
    onUpdateWakeConfig: (Boolean, String) -> Unit,
    onTestWakeWord: (String) -> Unit,
    onRefreshVoices: () -> Unit,
    onRequestMicPermission: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    var selectedVoiceId by remember(settings.selectedVoiceId, availableVoices) {
        mutableStateOf(
            settings.selectedVoiceId.ifBlank { availableVoices.firstOrNull()?.id.orEmpty() }
        )
    }
    var speed by remember(settings.speechSpeed) { mutableFloatStateOf(settings.speechSpeed) }
    var pitch by remember(settings.speechPitch) { mutableFloatStateOf(settings.speechPitch) }
    var style by remember(settings.speechStyle) { mutableStateOf(settings.speechStyle) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Awaaz Studio & Wake Word",
                    style = MaterialTheme.typography.headlineMedium,
                    color = UltronTextPrimary
                )
                Text(
                    text = "Engine: $ttsEngineLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronCyan
                )
            }
            OutlinedButton(onClick = onRefreshVoices) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Scan Voices", style = MaterialTheme.typography.labelSmall)
            }
        }

        // 1. Wake Word Settings Card (Critical Requirement)
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WAKE WORD DETECTION",
                            style = MaterialTheme.typography.labelMedium,
                            color = UltronCyan
                        )
                        Text(
                            text = "Status: ${wakeWordStatus.label}",
                            style = MaterialTheme.typography.titleMedium,
                            color = when (wakeWordStatus) {
                                WakeWordStatus.ACTIVE -> UltronEmerald
                                WakeWordStatus.MICROPHONE_BLOCKED -> UltronCrimson
                                else -> UltronGold
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = settings.wakeWordEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && wakeWordStatus == WakeWordStatus.MICROPHONE_BLOCKED) {
                                onRequestMicPermission()
                            } else {
                                onUpdateWakeConfig(enabled, settings.wakeWordPhrase)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = UltronCyan),
                        modifier = Modifier.testTag("wake_word_enable_switch")
                    )
                }

                Text(
                    text = wakeWordStatus.detailHinglish,
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronTextSecondary
                )

                // Wake Phrase Options: ULTRON | HEY ULTRON | HELLO ULTRON
                Text(
                    text = "Active Wake Word:",
                    style = MaterialTheme.typography.labelSmall,
                    color = UltronTextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MAX" to "MAX", "HEY MAX" to "HEY MAX", "ULTRON" to "ULTRON", "HEY ULTRON" to "HEY ULTRON").forEach { (code, label) ->
                        FilterChip(
                            selected = settings.wakeWordPhrase.equals(code, ignoreCase = true),
                            onClick = { onUpdateWakeConfig(settings.wakeWordEnabled, code) },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UltronEmerald.copy(alpha = 0.22f),
                                selectedLabelColor = UltronEmerald
                            )
                        )
                    }
                }

                // Controls: Enable, Disable, Test "MAX"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onUpdateWakeConfig(true, settings.wakeWordPhrase) },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronEmerald),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enable", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onUpdateWakeConfig(false, settings.wakeWordPhrase) },
                        border = BorderStroke(1.dp, UltronTextMuted),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Disable", color = UltronTextPrimary)
                    }
                    Button(
                        onClick = { onTestWakeWord("MAX") },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronEmerald),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("awaaz_test_max_button")
                    ) {
                        Text("TEST \"MAX\"", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }
                }

                if (wakeWordStatus == WakeWordStatus.MICROPHONE_BLOCKED) {
                    OutlinedButton(
                        onClick = onOpenAppSettings,
                        border = BorderStroke(1.dp, UltronCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = UltronCrimson)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OPEN SETTINGS (Grant Microphone)", color = UltronCrimson)
                    }
                }
            }
        }

        // 2. Voice Controls Card (Speed 0.8x-1.2x, Pitch, Style, Language)
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "VOICE CONFIGURATION & PROSODY",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )

                // Speech Speed: 0.8x - 1.2x
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Speech Speed", style = MaterialTheme.typography.bodyMedium, color = UltronTextPrimary)
                    Text(
                        text = String.format(Locale.US, "%.2fx", speed),
                        style = MaterialTheme.typography.labelLarge,
                        color = UltronCyan
                    )
                }
                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 0.8f..1.2f,
                    colors = SliderDefaults.colors(thumbColor = UltronCyan, activeTrackColor = UltronCyan)
                )

                // Pitch: 0.75x - 1.25x
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Voice Pitch (Depth)", style = MaterialTheme.typography.bodyMedium, color = UltronTextPrimary)
                    Text(
                        text = String.format(Locale.US, "%.2fx", pitch),
                        style = MaterialTheme.typography.labelLarge,
                        color = UltronViolet
                    )
                }
                Slider(
                    value = pitch,
                    onValueChange = { pitch = it },
                    valueRange = 0.75f..1.25f,
                    colors = SliderDefaults.colors(thumbColor = UltronViolet, activeTrackColor = UltronViolet)
                )

                // Speech Style: Natural, Deep Cinematic, Professional, Casual
                Text("Speech Style Preset:", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Natural", "Deep Cinematic", "Professional", "Casual").forEach { preset ->
                        FilterChip(
                            selected = style == preset,
                            onClick = { style = preset },
                            label = { Text(preset, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UltronViolet.copy(alpha = 0.25f),
                                selectedLabelColor = UltronTextPrimary
                            )
                        )
                    }
                }

                // Language Mode: Roman Hinglish, Hindi, English
                Text("Reply & Voice Language:", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageMode.entries.forEach { mode ->
                        FilterChip(
                            selected = settings.languageMode == mode,
                            onClick = { onUpdateLanguage(mode) },
                            label = { Text(mode.displayName, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UltronCyan.copy(alpha = 0.22f),
                                selectedLabelColor = UltronCyan
                            )
                        )
                    }
                }

                // Preview, Stop, and Save Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPreviewVoice(selectedVoiceId, speed, pitch, style) },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preview_voice_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = UltronObsidian)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }

                    if (isSpeaking) {
                        Button(
                            onClick = onStopVoice,
                            colors = ButtonDefaults.buttonColors(containerColor = UltronCrimson),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = UltronTextPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop", color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { onSaveVoiceConfig(selectedVoiceId, speed, pitch, style) },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronViolet),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_voice_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = UltronTextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Enumerated Device / Provider Voices List
        Text(
            text = "INSTALLED DEVICE & SYSTEM VOICES (${availableVoices.size})",
            style = MaterialTheme.typography.labelMedium,
            color = UltronCyan
        )

        availableVoices.forEach { voice ->
            val isSelected = voice.id == selectedVoiceId
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) UltronSurfaceElevated else UltronSurface
                ),
                border = BorderStroke(1.dp, if (isSelected) UltronCyan else UltronGlassBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedVoiceId = voice.id
                        onSaveVoiceConfig(voice.id, speed, pitch, style)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = voice.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) UltronCyan else UltronTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ID: ${voice.id} • ${voice.qualityLabel} • ${if (voice.requiresNetwork) "Cloud/Net" else "On-Device Local"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                    }
                    IconButton(onClick = { onPreviewVoice(voice.id, speed, pitch, style) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Sample Voice",
                            tint = UltronCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UltronLiveVoiceScreen(
    settings: UltronSettingsState,
    coreState: AssistantCoreState,
    wakeWordStatus: WakeWordStatus,
    micAmplitude: Float,
    isListening: Boolean,
    isSpeaking: Boolean,
    lastTranscriptOrReply: String,
    onStartPushToTalk: () -> Unit,
    onStopAll: () -> Unit,
    onTestWakeWord: () -> Unit,
    onToggleWakeWord: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ULTRON LIVE VOICE SESSION",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary
        )
        Text(
            text = "State: ${coreState.badgeLabel} • Wake Word: ${wakeWordStatus.label}",
            style = MaterialTheme.typography.bodyMedium,
            color = UltronCyan
        )

        UltronAnimatedCore(
            coreState = coreState,
            micAmplitude = micAmplitude,
            avatarId = settings.selectedAvatarId,
            reducedMotion = settings.reducedMotion,
            coreSize = 200.dp,
            onCoreTap = {
                if (isListening || isSpeaking) onStopAll() else onStartPushToTalk()
            }
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "LIVE TRANSCRIPT / RESPONSE",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lastTranscriptOrReply.ifBlank { "Mic dabayein ya 'Max' / 'Ultron' bolein..." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = UltronTextPrimary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onStartPushToTalk,
                colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("live_start_mic_button")
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = UltronObsidian)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Push To Talk", color = UltronObsidian, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onStopAll,
                colors = ButtonDefaults.buttonColors(containerColor = UltronCrimson),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("live_stop_button")
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, tint = UltronTextPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("STOP / Interrupt", color = UltronTextPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onTestWakeWord,
                border = BorderStroke(1.dp, UltronCyan),
                modifier = Modifier.weight(1f)
            ) {
                Text("Test Wake 'ULTRON'", color = UltronCyan)
            }
            OutlinedButton(
                onClick = { onToggleWakeWord(!settings.wakeWordEnabled) },
                border = BorderStroke(1.dp, UltronViolet),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (settings.wakeWordEnabled) "Pause Wake Service" else "Start Wake Service",
                    color = UltronTextPrimary
                )
            }
        }
    }
}

@Composable
fun UltronAiModelsScreen(
    settings: UltronSettingsState,
    models: List<AIModelSpec>,
    isApiKeyConfigured: Boolean,
    providerTestStatus: String? = null,
    onSelectRoutingMode: (RoutingMode) -> Unit,
    onSelectSubAgentStrategy: (com.example.domain.model.SubAgentRoutingStrategy) -> Unit = {},
    onTestModel: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Sub-agents & AI Brain",
                style = MaterialTheme.typography.headlineMedium,
                color = UltronTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isApiKeyConfigured) {
                    "Gemini Cloud API Configured • Automatic Fallback Enabled"
                } else {
                    "GEMINI_API_KEY not set • Settings > Personal ya Secrets panel me key jodein (Local Fallback Active)"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (isApiKeyConfigured) UltronEmerald else UltronGold
            )
            if (!providerTestStatus.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = providerTestStatus,
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
            }
        }

        // Priority Strategy Card from Reference Video ("Best pehle", "Ranked – sabko use karo", "Sab ek saath")
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PRIORITY (KAUN PEHLE KAAM KARE)",
                        style = MaterialTheme.typography.labelMedium,
                        color = UltronCyan
                    )
                    com.example.domain.model.SubAgentRoutingStrategy.entries.forEach { strat ->
                        val selected = settings.subAgentStrategy == strat
                        Surface(
                            color = if (selected) UltronEmerald.copy(alpha = 0.16f) else UltronObsidian,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (selected) UltronEmerald else UltronGlassBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectSubAgentStrategy(strat) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = strat.titleHinglish,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selected) UltronEmerald else UltronTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = strat.descHinglish,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = UltronTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SMART ROUTING MODE",
                        style = MaterialTheme.typography.labelMedium,
                        color = UltronCyan
                    )
                    RoutingMode.entries.forEach { mode ->
                        val selected = settings.routingMode == mode
                        Surface(
                            color = if (selected) UltronCyan.copy(alpha = 0.16f) else UltronObsidian,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (selected) UltronCyan else UltronGlassBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectRoutingMode(mode) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selected) UltronCyan else UltronTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = mode.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = UltronTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "CONFIGURED SUB-AGENTS (${models.size} MODELS)",
                style = MaterialTheme.typography.labelMedium,
                color = UltronCyan
            )
        }

        items(models, key = { it.id }) { spec ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = spec.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = UltronTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ID: ${spec.id} • ${spec.provider}",
                                style = MaterialTheme.typography.labelSmall,
                                color = UltronTextMuted
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (!spec.isLocal) {
                                OutlinedButton(
                                    onClick = { onTestModel(spec.id) },
                                    border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.65f)),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Test", style = MaterialTheme.typography.labelSmall, color = UltronCyan)
                                }
                            }
                            Surface(
                                color = if (spec.isLocal) UltronViolet.copy(alpha = 0.2f) else UltronEmerald.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (spec.isLocal) "ON-DEVICE" else "CLOUD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (spec.isLocal) UltronViolet else UltronEmerald,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = spec.descriptionHinglish,
                        style = MaterialTheme.typography.bodySmall,
                        color = UltronTextSecondary
                    )
                    Text(
                        text = "Capabilities: ${spec.capabilities.joinToString(" • ")}" +
                            if (spec.averageLatencyMs > 0) " • Avg ${spec.averageLatencyMs}ms" else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = UltronCyan
                    )
                }
            }
        }
    }
}

@Composable
fun UltronAvatarStudioScreen(
    settings: UltronSettingsState,
    avatars: List<AvatarModelEntity>,
    coreState: AssistantCoreState,
    micAmplitude: Float,
    onSelectAvatar: (String) -> Unit,
    onDownloadAvatar: (AvatarModelEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Avatar Studio & 3D Download Center",
                    style = MaterialTheme.typography.headlineMedium,
                    color = UltronTextPrimary
                )
                Text(
                    text = "Original Licensed Models • Voice-Reactive Shader Core",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronCyan
                )
                Spacer(modifier = Modifier.height(10.dp))
                UltronAnimatedCore(
                    coreState = coreState,
                    micAmplitude = micAmplitude,
                    avatarId = settings.selectedAvatarId,
                    reducedMotion = settings.reducedMotion,
                    coreSize = 165.dp,
                    onCoreTap = {}
                )
            }
        }

        items(avatars, key = { it.id }) { model ->
            val isSelected = model.id == settings.selectedAvatarId
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) UltronSurfaceElevated else UltronSurface
                ),
                border = BorderStroke(1.5.dp, if (isSelected) UltronCyan else UltronGlassBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) UltronCyan else UltronTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = model.downloadState,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (model.downloadState == "INSTALLED") UltronEmerald else UltronGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Format: ${model.format} • Size: ${model.fileSizeLabel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = UltronTextSecondary
                    )
                    Text(
                        text = "License: ${model.license} • Attribution: ${model.attribution}",
                        style = MaterialTheme.typography.labelSmall,
                        color = UltronTextMuted
                    )

                    if (model.downloadState == "DOWNLOADING") {
                        LinearProgressIndicator(
                            progress = { model.downloadProgress / 100f },
                            color = UltronCyan,
                            trackColor = UltronObsidian,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (model.downloadState == "INSTALLED") {
                            Button(
                                onClick = { onSelectAvatar(model.id) },
                                enabled = !isSelected,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) UltronEmerald else UltronCyan
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = UltronObsidian,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSelected) "Active Avatar" else "Select Avatar",
                                    color = UltronObsidian,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = { onDownloadAvatar(model) },
                                colors = ButtonDefaults.buttonColors(containerColor = UltronViolet)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = UltronTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download & Verify", color = UltronTextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UltronToolsScreen(
    onNavigate: (UltronDestination) -> Unit
) {
    val context = LocalContext.current

    val toolItems = listOf(
        ToolCardInfo(
            title = "Files & Documents",
            subtitle = "Summarize docs, notes & file tasks",
            icon = Icons.Default.Folder,
            accent = UltronCyan,
            tag = "tool_item_files",
            onClick = { onNavigate(UltronDestination.DOCUMENTS) }
        ),
        ToolCardInfo(
            title = "Web Search",
            subtitle = "Search Google & knowledge web",
            icon = Icons.Default.Language,
            accent = UltronEmerald,
            tag = "tool_item_web",
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        ),
        ToolCardInfo(
            title = "Calls & Emergency",
            subtitle = "Direct dialer & 112 emergency intent",
            icon = Icons.Default.Phone,
            accent = UltronCrimson,
            tag = "tool_item_calls",
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        ),
        ToolCardInfo(
            title = "Notes & Journal",
            subtitle = "Quick thoughts, daily logs & reflections",
            icon = Icons.Default.EditNote,
            accent = UltronViolet,
            tag = "tool_item_notes",
            onClick = { onNavigate(UltronDestination.JOURNAL) }
        ),
        ToolCardInfo(
            title = "Yaadein (Memories)",
            subtitle = "Personal memory records & facts",
            icon = Icons.Default.Bookmarks,
            accent = UltronGold,
            tag = "tool_item_yaadein",
            onClick = { onNavigate(UltronDestination.YAADEIN) }
        ),
        ToolCardInfo(
            title = "Padhai (Study Assistant)",
            subtitle = "Explanations, STEM formulas & concepts",
            icon = Icons.Default.School,
            accent = UltronCyan,
            tag = "tool_item_padhai",
            onClick = { onNavigate(UltronDestination.PADHAI) }
        ),
        ToolCardInfo(
            title = "Scanner (OCR & QR)",
            subtitle = "Camera text extraction & QR inspect",
            icon = Icons.Default.QrCodeScanner,
            accent = UltronEmerald,
            tag = "tool_item_scan",
            onClick = { onNavigate(UltronDestination.SCAN) }
        ),
        ToolCardInfo(
            title = "Markets & Watchlist",
            subtitle = "Market tracking & tactical notes",
            icon = Icons.Default.Language,
            accent = UltronGold,
            tag = "tool_item_markets",
            onClick = { onNavigate(UltronDestination.MARKETS) }
        ),
        ToolCardInfo(
            title = "Music (Gaane)",
            subtitle = "Media playback & YouTube search",
            icon = Icons.Default.MusicNote,
            accent = Color(0xFFEC4899),
            tag = "tool_item_gaane",
            onClick = { onNavigate(UltronDestination.GAANE) }
        ),
        ToolCardInfo(
            title = "Dream Journal",
            subtitle = "Voice transcription & Jungian reflections",
            icon = Icons.Default.NightsStay,
            accent = UltronViolet,
            tag = "tool_item_dreams",
            onClick = { onNavigate(UltronDestination.DREAM_JOURNAL) }
        ),
        ToolCardInfo(
            title = "Voice Engine (TTS)",
            subtitle = "Speech speed, pitch & voice models",
            icon = Icons.Default.GraphicEq,
            accent = UltronCyan,
            tag = "tool_item_voice_engine",
            onClick = { onNavigate(UltronDestination.AWAAZ_STUDIO) }
        ),
        ToolCardInfo(
            title = "Settings & Config",
            subtitle = "Personal address, API keys & diagnostics",
            icon = Icons.Default.Settings,
            accent = UltronTextSecondary,
            tag = "tool_item_settings",
            onClick = { onNavigate(UltronDestination.SETTINGS) }
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "TOOLS HUB",
                    style = MaterialTheme.typography.headlineMedium,
                    color = UltronTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Utilities • Files • Web • Communication • Study",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronCyan
                )
            }
        }

        items(toolItems, key = { it.tag }) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { item.onClick() }
                    .testTag(item.tag)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(item.accent.copy(alpha = 0.15f))
                            .border(1.dp, item.accent.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                    }
                }
            }
        }
    }
}

private data class ToolCardInfo(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
    val tag: String,
    val onClick: () -> Unit
)

