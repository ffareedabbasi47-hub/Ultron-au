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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
import com.example.ui.theme.UltronCardBorderGlow
import com.example.ui.theme.UltronCrimson
import com.example.ui.theme.UltronCyan
import com.example.ui.theme.UltronCyanBright
import com.example.ui.theme.UltronDeepDark
import com.example.ui.theme.UltronDeepPurple
import com.example.ui.theme.UltronEmerald
import com.example.ui.theme.UltronEnergyBg
import com.example.ui.theme.UltronGlassBorder
import com.example.ui.theme.UltronGold
import com.example.ui.theme.UltronMagenta
import com.example.ui.theme.UltronObsidian
import com.example.ui.theme.UltronOrbCyan
import com.example.ui.theme.UltronOrbMagenta
import com.example.ui.theme.UltronOrbSilver
import com.example.ui.theme.UltronSurface
import com.example.ui.theme.UltronSurfaceElevated
import com.example.ui.theme.UltronTextMuted
import com.example.ui.theme.UltronTextPrimary
import com.example.ui.theme.UltronTextSecondary
import com.example.ui.theme.UltronViolet
import com.example.ui.theme.UltronVividPurple

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
        val adaptiveCoreSize = if (maxHeight < 680.dp) 180.dp else 215.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Greeting Row with Live/Sleep Status & Energy Pill (Reference Matched)
            val userGreetingName = if (persona == AssistantPersona.MAX) "Boss" else settings.userAddress
            val timeGreeting = remember {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                when {
                    hour < 12 -> "Good morning,"
                    hour < 17 -> "Good afternoon,"
                    else -> "Good evening,"
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = timeGreeting,
                        style = MaterialTheme.typography.titleMedium,
                        color = UltronTextSecondary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = userGreetingName,
                        style = MaterialTheme.typography.headlineLarge,
                        color = UltronTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = (-0.5).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        val isAwake = coreState == AssistantCoreState.LISTENING ||
                            coreState == AssistantCoreState.SPEAKING ||
                            coreState == AssistantCoreState.THINKING
                        Icon(
                            imageVector = if (isAwake) Icons.Default.GraphicEq else Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = UltronViolet,
                            modifier = Modifier.size(15.dp)
                        )
                        val statusText = when (coreState) {
                            AssistantCoreState.LISTENING -> "Sun rahi hai..."
                            AssistantCoreState.SPEAKING -> "Bol rahi hai..."
                            AssistantCoreState.THINKING, AssistantCoreState.TRANSCRIBING -> "Soch rahi hai..."
                            AssistantCoreState.EXECUTING -> "Kaam ho raha hai..."
                            else -> "${persona.displayName} so rahi hai — bolo \"${persona.defaultWakePhrase}\""
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Reference Right: Energy Pill (with Lightning Bolt & Gold Count)
                Surface(
                    color = UltronEnergyBg,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, UltronCardBorderGlow),
                    modifier = Modifier
                        .clickable { onNavigate(UltronDestination.CONTROL_CENTER) }
                        .testTag("energy_status_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Energy",
                            tint = UltronGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "4",
                                style = MaterialTheme.typography.titleMedium,
                                color = UltronTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Energy",
                                style = MaterialTheme.typography.labelSmall,
                                color = UltronTextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // 2. Central Animated Multi-Ring Core (Reference Main Focus)
            UltronAnimatedCore(
                coreState = coreState,
                micAmplitude = micAmplitude,
                avatarId = settings.selectedAvatarId,
                reducedMotion = settings.reducedMotion,
                activePersona = persona,
                coreSize = adaptiveCoreSize,
                onCoreTap = onCoreTap
            )

            // 3. Reference Quick-Action Chips Row: Gaane, Padhai, Journal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReferenceActionPill(
                    title = "Gaane",
                    icon = Icons.Default.MusicNote,
                    tag = "quick_action_gaane",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.GAANE) }
                )
                ReferenceActionPill(
                    title = "Padhai",
                    icon = Icons.Default.MenuBook,
                    tag = "quick_action_padhai",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.PADHAI) }
                )
                ReferenceActionPill(
                    title = "Journal",
                    icon = Icons.Default.Edit,
                    tag = "quick_action_journal",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.JOURNAL) }
                )
            }

            // 4. Reference Daily Information Cards Row: Mausam, Aaj, Mood
            val todayDay = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() }
            val todayDateSub = remember { SimpleDateFormat("EEE, MMM", Locale.getDefault()).format(Date()) }
            val weatherTemp = weatherData?.let { "${it.temperatureCelsius}°" } ?: "27°"
            val weatherCond = weatherData?.conditionText ?: "Clear"
            val weatherHum = weatherData?.let { "${it.humidityPercent}%" } ?: "59%"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Mausam
                ReferenceDailyCard(
                    title = "Mausam",
                    icon = Icons.Default.WbSunny,
                    mainValue = weatherTemp,
                    subValue = weatherCond,
                    detailIcon = Icons.Default.WaterDrop,
                    detailText = weatherHum,
                    tag = "daily_card_mausam",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.MAUSAM) }
                )

                // Card 2: Aaj (Calendar)
                ReferenceDailyCard(
                    title = "Aaj",
                    icon = Icons.Default.CalendarToday,
                    mainValue = todayDay,
                    subValue = todayDateSub,
                    tag = "daily_card_aaj",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.AAJ) }
                )

                // Card 3: Mood
                val moodTitle = latestMood?.label ?: "Khush"
                val moodSub = latestMood?.subLabel ?: "Josh me"
                ReferenceDailyCard(
                    title = "Mood",
                    icon = Icons.Default.Favorite,
                    mainValue = moodTitle,
                    subValue = moodSub,
                    isMood = true,
                    tag = "daily_card_mood",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.MOOD) }
                )
            }

            // 5. Wake Word Pipeline & Test Card (Sleek Reference Card)
            Surface(
                color = UltronSurface.copy(alpha = 0.90f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, UltronGlassBorder),
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
                                tint = UltronViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Wake Word: ${persona.defaultWakePhrase.uppercase(Locale.ROOT)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = UltronTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
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
                            border = BorderStroke(
                                1.dp,
                                if (settings.wakeWordEnabled) UltronCrimson.copy(alpha = 0.6f) else UltronViolet.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = if (settings.wakeWordEnabled) "Disable" else "Enable",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (settings.wakeWordEnabled) UltronCrimson else UltronViolet,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Test Wake Word Button ("TEST MAX" / "TEST ULTRON")
                    OutlinedButton(
                        onClick = onTestWakeWord,
                        border = BorderStroke(1.dp, UltronViolet.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("test_wake_word_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = UltronViolet,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TEST \"${persona.defaultWakePhrase.uppercase(Locale.ROOT)}\" (Yes, Boss -> Listening)",
                            style = MaterialTheme.typography.labelSmall,
                            color = UltronViolet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 6. Device Control, Vision & Documents Shortcut Row (Preserves test targets)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    title = "Control",
                    subtitle = "RAM ${telemetry.ramPercent}%",
                    icon = Icons.Default.PhoneAndroid,
                    accentColor = UltronEmerald,
                    tag = "quick_action_control",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.CONTROL_CENTER) }
                )
                QuickActionCard(
                    title = "Vision",
                    subtitle = "Scanner",
                    icon = Icons.Default.Visibility,
                    accentColor = UltronCyanBright,
                    tag = "quick_action_vision",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.SCAN) }
                )
                QuickActionCard(
                    title = "Files",
                    subtitle = "Documents",
                    icon = Icons.Default.Folder,
                    accentColor = UltronGold,
                    tag = "quick_action_files",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UltronDestination.DOCUMENTS) }
                )
            }

            // 7. Recent Conversation Panel (Clean Compact Bubbles + VIEW CHAT)
            Surface(
                color = UltronSurface.copy(alpha = 0.90f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, UltronGlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .testTag("home_conversation_panel")
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
                        Text(
                            text = "RECENT CONVERSATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = UltronViolet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "VIEW CHAT →",
                            style = MaterialTheme.typography.labelSmall,
                            color = UltronViolet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable { onNavigate(UltronDestination.CHAT) }
                                .padding(2.dp)
                                .testTag("view_full_chat_button")
                        )
                    }

                    if (recentMessages.isEmpty() && streamingText.isBlank()) {
                        Text(
                            text = "${persona.displayName}: \"Bolna shuru karein ya mic dabayein, Boss!\"",
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
                                    if (isUser) UltronViolet.copy(alpha = 0.40f) else UltronGlassBorder
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
                                        color = if (isUser) UltronViolet else UltronCyanBright,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(54.dp)
                                    )
                                    Text(
                                        text = msg.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = UltronTextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    if (streamingText.isNotBlank()) {
                        Surface(
                            color = UltronObsidian,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, UltronViolet),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${persona.displayName}: $streamingText",
                                style = MaterialTheme.typography.bodySmall,
                                color = UltronViolet,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Reference Quick-Action Pill (Gaane, Padhai, Journal)
 */
@Composable
private fun ReferenceActionPill(
    title: String,
    icon: ImageVector,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = UltronSurface.copy(alpha = 0.88f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, UltronCardBorderGlow),
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = UltronViolet,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = UltronTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                maxLines = 1
            )
        }
    }
}

/**
 * Reference Daily Information Card (Mausam, Aaj, Mood)
 */
@Composable
private fun ReferenceDailyCard(
    title: String,
    icon: ImageVector,
    mainValue: String,
    subValue: String,
    detailIcon: ImageVector? = null,
    detailText: String? = null,
    isMood: Boolean = false,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = UltronSurface.copy(alpha = 0.90f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, UltronGlassBorder),
        modifier = modifier
            .height(112.dp)
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isMood) {
                // Subtle glowing heart outline watermark in background
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = UltronViolet.copy(alpha = 0.12f),
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 6.dp, end = 6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header (Icon + Title)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isMood) UltronMagenta else UltronTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = UltronTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Main Value
                Text(
                    text = mainValue,
                    style = MaterialTheme.typography.titleLarge,
                    color = UltronTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtitle / Details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = subValue,
                        style = MaterialTheme.typography.labelSmall,
                        color = UltronTextMuted,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (detailIcon != null && detailText != null) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = detailIcon,
                            contentDescription = null,
                            tint = UltronCyanBright,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = detailText,
                            style = MaterialTheme.typography.labelSmall,
                            color = UltronTextMuted,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
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
