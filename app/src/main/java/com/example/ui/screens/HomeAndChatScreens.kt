package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.data.local.MoodEntryEntity
import com.example.data.local.ReminderEntity
import com.example.data.local.UltronSettingsState
import com.example.domain.model.AssistantCoreState
import com.example.domain.model.AssistantPersona
import com.example.domain.model.DeviceTelemetry
import com.example.domain.model.UltronDestination
import com.example.domain.model.VoiceDiagnosticsInfo
import com.example.domain.model.WakeWordStatus
import com.example.domain.model.WeatherData
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UltronGharHomeScreen(
    settings: UltronSettingsState,
    coreState: AssistantCoreState,
    wakeWordStatus: WakeWordStatus,
    voiceDiagnostics: VoiceDiagnosticsInfo = VoiceDiagnosticsInfo(),
    micAmplitude: Float,
    messages: List<ChatMessageEntity>,
    streamingText: String,
    weatherData: WeatherData?,
    weatherError: String?,
    reminders: List<ReminderEntity>,
    latestMood: MoodEntryEntity?,
    telemetry: DeviceTelemetry,
    onCoreTap: () -> Unit,
    onNavigate: (UltronDestination) -> Unit,
    onQuickPrompt: (String) -> Unit,
    onSpeakText: (String) -> Unit,
    onTestWakeWord: () -> Unit,
    onToggleWakeWord: (Boolean) -> Unit,
    onRequestMicPermission: () -> Unit,
    onTogglePersona: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val recentMessages = messages.takeLast(2)
    val persona = settings.activePersona
    val personaAccent = if (persona == AssistantPersona.MAX) UltronEmerald else UltronCyan
    val cardAlpha = settings.surfaceStyle.alpha
    val cornerRadius = settings.cornerStyle.radiusDp.dp

    val context = LocalContext.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val adaptiveCoreSize = if (maxHeight < 680.dp) 112.dp else 126.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Top Greeting & Persona Status Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Namaste, ${settings.userAddress}",
                        style = MaterialTheme.typography.titleLarge,
                        color = UltronTextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Personal AI Assistant",
                        style = MaterialTheme.typography.bodySmall,
                        color = personaAccent
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Real Defined Energy Score
                    Surface(
                        color = UltronSurfaceElevated.copy(alpha = cardAlpha),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, UltronGold.copy(alpha = 0.45f)),
                        modifier = Modifier.clickable { onNavigate(UltronDestination.CONTROL_CENTER) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "System Energy",
                                tint = UltronGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${telemetry.realEnergyUnits} Energy",
                                style = MaterialTheme.typography.labelSmall,
                                color = UltronTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Persona Handoff Pill ("Ultron ko bhej" / "Max ko bhej")
                    Surface(
                        color = personaAccent.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, personaAccent.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable { onTogglePersona() }
                            .testTag("home_persona_switch_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Persona",
                                tint = personaAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (persona == AssistantPersona.MAX) "Ultron ko bhej" else "Max ko bhej",
                                style = MaterialTheme.typography.labelSmall,
                                color = personaAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 2. WAKE WORD SETTINGS & STATUS CARD (Requirement 7)
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface.copy(alpha = cardAlpha)),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(cornerRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .testTag("wake_word_settings_card")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = personaAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Wake Word: ${persona.defaultWakePhrase.uppercase(Locale.ROOT)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = UltronTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                val statusDotColor = when (wakeWordStatus) {
                                    WakeWordStatus.ACTIVE -> UltronEmerald
                                    WakeWordStatus.PAUSED -> UltronGold
                                    WakeWordStatus.MICROPHONE_BLOCKED -> UltronCrimson
                                    WakeWordStatus.SERVICE_STOPPED -> UltronTextMuted
                                }
                                val statusLabel = when (wakeWordStatus) {
                                    WakeWordStatus.ACTIVE -> "ACTIVE (Listening for \"${persona.defaultWakePhrase}\")"
                                    WakeWordStatus.PAUSED -> "PAUSED"
                                    WakeWordStatus.MICROPHONE_BLOCKED -> "MICROPHONE BLOCKED"
                                    WakeWordStatus.SERVICE_STOPPED -> "SERVICE STOPPED"
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(statusDotColor)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = statusLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = statusDotColor,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = "Engine: ${voiceDiagnostics.wakeEngineName} • Triggers: ${voiceDiagnostics.wakeDetectionCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = UltronTextSecondary,
                                    fontSize = 9.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Enable / Disable Control
                        OutlinedButton(
                            onClick = {
                                if (wakeWordStatus == WakeWordStatus.MICROPHONE_BLOCKED) {
                                    onRequestMicPermission()
                                } else {
                                    onToggleWakeWord(!settings.wakeWordEnabled)
                                }
                            },
                            border = BorderStroke(1.dp, if (settings.wakeWordEnabled) UltronCrimson.copy(alpha = 0.7f) else personaAccent.copy(alpha = 0.7f)),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (settings.wakeWordEnabled) "Disable" else "Enable",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (settings.wakeWordEnabled) UltronCrimson else personaAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Test Wake Word Button ("TEST MAX")
                    OutlinedButton(
                        onClick = onTestWakeWord,
                        border = BorderStroke(1.dp, personaAccent.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("test_wake_word_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = personaAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TEST \"${persona.defaultWakePhrase.uppercase(Locale.ROOT)}\" (Yes, Boss -> Listening)",
                            style = MaterialTheme.typography.labelSmall,
                            color = personaAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. CENTRAL MAX CORE (Reduced Footprint, Animated States)
            UltronAnimatedCore(
                coreState = coreState,
                micAmplitude = micAmplitude,
                avatarId = settings.selectedAvatarId,
                reducedMotion = settings.reducedMotion,
                activePersona = persona,
                coreSize = adaptiveCoreSize,
                onCoreTap = onCoreTap
            )

            // 4. CLEAN CONVERSATION PANEL (Recent Bubbles + VIEW CHAT)
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface.copy(alpha = cardAlpha)),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(cornerRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .testTag("home_conversation_panel")
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT CONVERSATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = personaAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "VIEW CHAT →",
                            style = MaterialTheme.typography.labelSmall,
                            color = personaAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onNavigate(UltronDestination.CHAT) }
                                .padding(2.dp)
                                .testTag("view_full_chat_button")
                        )
                    }

                    if (recentMessages.isEmpty() && streamingText.isBlank()) {
                        Text(
                            text = "MAX: \"Bolna shuru karein ya mic dabayein, Boss!\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextMuted,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        recentMessages.forEach { msg ->
                            val isUser = msg.role == "user"
                            Surface(
                                color = if (isUser) UltronSurfaceElevated else UltronObsidian,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isUser) UltronViolet.copy(alpha = 0.45f) else personaAccent.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = if (isUser) "YOU:" else "${persona.displayName.uppercase(Locale.ROOT)}:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isUser) UltronViolet else personaAccent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(52.dp)
                                    )
                                    Text(
                                        text = msg.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = UltronTextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (!isUser) {
                                        IconButton(
                                            onClick = { onSpeakText(msg.content) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Speak Reply",
                                                tint = personaAccent,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (streamingText.isNotBlank()) {
                        Surface(
                            color = UltronObsidian,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, personaAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${persona.displayName}: $streamingText",
                                style = MaterialTheme.typography.bodySmall,
                                color = personaAccent,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // 5. QUICK ACTIONS (Clean working cards: Phone Control, Vision, Files, Web, Calls, Notes)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "QUICK ACTIONS",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronTextSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )

                // Row 1: Phone Control | Vision | Files
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Phone Control",
                        subtitle = "RAM ${telemetry.ramPercent}%",
                        icon = Icons.Default.PhoneAndroid,
                        accentColor = UltronEmerald,
                        tag = "quick_action_control",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(UltronDestination.CONTROL_CENTER) }
                    )
                    QuickActionCard(
                        title = "Vision",
                        subtitle = "Screen Assist",
                        icon = Icons.Default.Visibility,
                        accentColor = UltronCyan,
                        tag = "quick_action_vision",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(UltronDestination.VISION_CENTER) }
                    )
                    QuickActionCard(
                        title = "Files",
                        subtitle = "Docs & Notes",
                        icon = Icons.Default.Folder,
                        accentColor = UltronGold,
                        tag = "quick_action_files",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(UltronDestination.DOCUMENTS) }
                    )
                }

                // Row 2: Web | Calls | Notes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Web",
                        subtitle = "Search Google",
                        icon = Icons.Default.Language,
                        accentColor = UltronCyan,
                        tag = "quick_action_web",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                    QuickActionCard(
                        title = "Calls",
                        subtitle = "Phone & 112",
                        icon = Icons.Default.Phone,
                        accentColor = UltronCrimson,
                        tag = "quick_action_calls",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                    QuickActionCard(
                        title = "Notes",
                        subtitle = "Journal & Logs",
                        icon = Icons.Default.EditNote,
                        accentColor = UltronViolet,
                        tag = "quick_action_notes",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(UltronDestination.JOURNAL) }
                    )
                }
            }

            // 6. Natural Hinglish Questions & Commands
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val sampleCommands = listOf(
                    "Max, India ki capital kya hai?",
                    "Max, gravity kya hoti hai?",
                    "Max, YouTube kholo",
                    "Max, aaj kya date hai?",
                    "Max, CPU kitna use ho raha hai?",
                    "Max ek note bana"
                )
                sampleCommands.forEach { cmd ->
                    AssistChip(
                        onClick = { onQuickPrompt(cmd) },
                        label = {
                            Text(
                                text = cmd,
                                style = MaterialTheme.typography.bodySmall,
                                color = UltronTextPrimary
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = UltronSurfaceElevated
                        ),
                        border = BorderStroke(1.dp, UltronGlassBorder)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun ReferenceCircularPill(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .testTag(tag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(UltronSurfaceElevated.copy(alpha = 0.85f))
                .border(1.dp, accentColor.copy(alpha = 0.55f), CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = UltronTextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = UltronSurface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.16f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = UltronTextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = UltronTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun InfoSummaryCard(
    title: String,
    valueText: String,
    subText: String,
    icon: ImageVector,
    accent: Color,
    cardAlpha: Float,
    cornerRadius: androidx.compose.ui.unit.Dp,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated.copy(alpha = cardAlpha)),
        border = BorderStroke(1.dp, UltronGlassBorder),
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = UltronTextSecondary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                color = UltronTextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.bodySmall,
                color = UltronTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Full Chat & Command History Screen with Replay (TTS), Copy, Delete, and Context Follow-up support.
 */
@Composable
fun UltronChatAndHistoryScreen(
    messages: List<ChatMessageEntity>,
    streamingText: String,
    lastLatencyMs: Long,
    onSpeakMessage: (String) -> Unit,
    onDeleteMessage: (Long) -> Unit,
    onClearAll: () -> Unit,
    onQuickPrompt: (String) -> Unit
) {
    val context = LocalContext.current
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Chat aur Command History",
                    style = MaterialTheme.typography.headlineMedium,
                    color = UltronTextPrimary
                )
                Text(
                    text = "${messages.size} messages • Context Memory Active" +
                        if (lastLatencyMs > 0) " • Last reply ${lastLatencyMs}ms" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronCyan
                )
            }
            OutlinedButton(
                onClick = onClearAll,
                border = BorderStroke(1.dp, UltronCrimson.copy(alpha = 0.6f)),
                modifier = Modifier.testTag("clear_chat_history_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = UltronCrimson,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear", style = MaterialTheme.typography.labelSmall, color = UltronCrimson)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Follow-Up Context Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Max ko bhej",
                "Ultron ko bhej",
                "Ek note bana",
                "Science project",
                "Ye mujhe simple language mein samjha",
                "Aaj kya date hai?",
                "CPU kitna use ho raha hai?"
            ).forEach { prompt ->
                AssistChip(
                    onClick = { onQuickPrompt(prompt) },
                    label = { Text(prompt, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.role == "user"
                val isMaxModel = msg.modelUsed.contains("max", ignoreCase = true)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUser) UltronSurfaceElevated else UltronSurface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isUser) UltronViolet.copy(alpha = 0.5f) else if (isMaxModel) UltronEmerald.copy(alpha = 0.5f) else UltronCyan.copy(alpha = 0.45f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isUser) "USER" else if (isMaxModel) "MAX AI" else "ULTRON AI",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isUser) UltronViolet else if (isMaxModel) UltronEmerald else UltronCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "• ${msg.intentCategory}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = UltronTextMuted
                                )
                            }
                            Text(
                                text = timeFormatter.format(Date(msg.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = UltronTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = msg.content,
                            style = MaterialTheme.typography.bodyLarge,
                            color = UltronTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Row: Replay (TTS), Copy, Delete + Model Tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (!isUser) "Model: ${msg.modelUsed}" else "",
                                style = MaterialTheme.typography.labelSmall,
                                color = UltronTextMuted
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { onSpeakMessage(msg.content) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Replay Audio",
                                        tint = UltronCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("ULTRON Message", msg.content))
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Text",
                                        tint = UltronTextSecondary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteMessage(msg.id) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Message",
                                        tint = UltronCrimson.copy(alpha = 0.85f),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (streamingText.isNotBlank()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = UltronObsidian),
                        border = BorderStroke(1.5.dp, UltronCyan),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "STREAMING...",
                                style = MaterialTheme.typography.labelMedium,
                                color = UltronCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = streamingText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = UltronTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
