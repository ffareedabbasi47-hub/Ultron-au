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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UltronSettingsState
import com.example.domain.model.AIModelSpec
import com.example.domain.model.AssistantPersona
import com.example.domain.model.CodingProjectItem
import com.example.domain.model.CornerThemeStyle
import com.example.domain.model.DeviceTelemetry
import com.example.domain.model.GenderVerbStyle
import com.example.domain.model.MarketWatchItem
import com.example.domain.model.RoutingMode
import com.example.domain.model.SubAgentRoutingStrategy
import com.example.domain.model.SurfaceThemeStyle
import com.example.domain.model.UltronDestination
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

/**
 * Complete Reference-Faithful Categorized Settings Hub Screen:
 * Groups: PERSONAL, COMPANION, BRAIN & TOOLS, CONNECTIONS, LOOK & FEEL, ACCESSIBILITY & CONTROL, PRIVACY & SAFETY
 */
@Composable
fun UltronSettingsHubScreen(
    settings: UltronSettingsState,
    memoriesCount: Int,
    journalsCount: Int,
    chatsCount: Int,
    onNavigate: (UltronDestination) -> Unit,
    onToggleBatterySaver: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Har setting card ek real configuration screen kholta hai • Creator: ${settings.configuredCreator}",
            style = MaterialTheme.typography.bodySmall,
            color = UltronCyan
        )

        // 1. PERSONAL
        SettingsGroupHeader("PERSONAL")
        SettingsGroupCard {
            SettingsNavRow(
                title = "Personal",
                subtitle = "Naam (${settings.userAddress}), gender (${settings.genderVerbStyle.labelHinglish}), pasandida gaana, Gemini API Key",
                icon = Icons.Default.Person,
                accent = UltronCyan,
                tag = "settings_row_personal",
                onClick = { onNavigate(UltronDestination.SETTINGS_PERSONAL) }
            )
        }

        // 2. COMPANION
        SettingsGroupHeader("COMPANION")
        SettingsGroupCard {
            SettingsNavRow(
                title = "Ultron ⇄ Max Dual Core",
                subtitle = "Active: ${settings.activePersona.displayName} • Voice handoff: 'Max ko bhej' / 'Ultron ko bhej'",
                icon = Icons.Default.SwapHoriz,
                accent = UltronEmerald,
                tag = "settings_row_persona",
                onClick = { onNavigate(UltronDestination.PERSONA_SWITCHER) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Voice Engine",
                subtitle = "Awaaz, speed (${String.format(java.util.Locale.US, "%.2fx", settings.speechSpeed)}), pitch, Hindi/Hinglish",
                icon = Icons.Default.GraphicEq,
                accent = UltronViolet,
                tag = "settings_row_voice",
                onClick = { onNavigate(UltronDestination.AWAAZ_STUDIO) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Companion Tone Settings",
                subtitle = "Tone: ${settings.companionToneMode} • Polite boundary & persona style",
                icon = Icons.Default.AutoAwesome,
                accent = Color(0xFFEC4899),
                tag = "settings_row_secret",
                onClick = { onNavigate(UltronDestination.SETTINGS_SECRET) }
            )
        }

        // 3. BRAIN & TOOLS
        SettingsGroupHeader("BRAIN & TOOLS")
        SettingsGroupCard {
            SettingsNavRow(
                title = "Sub-agents",
                subtitle = "5 AI brains • Strategy: ${settings.subAgentStrategy.titleHinglish}",
                icon = Icons.Default.Psychology,
                accent = UltronCyan,
                tag = "settings_row_subagents",
                onClick = { onNavigate(UltronDestination.AI_MODELS) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Skills & Playbooks",
                subtitle = "Padhai, Coding, Markets, Screen Samjho aur Dream Journal workflows",
                icon = Icons.Default.Extension,
                accent = UltronGold,
                tag = "settings_row_skills",
                onClick = { onNavigate(UltronDestination.SETTINGS_SKILLS) }
            )
        }

        // 4. CONNECTIONS & BACKUP
        SettingsGroupHeader("CONNECTIONS")
        SettingsGroupCard {
            SettingsNavRow(
                title = "Email, WhatsApp & Social",
                subtitle = "Android Intent composer for Email aur WhatsApp/SMS handoff",
                icon = Icons.Default.Email,
                accent = UltronEmerald,
                tag = "settings_row_messages",
                onClick = { onNavigate(UltronDestination.SETTINGS_MESSAGES) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Connectors & PC ⇄ Phone",
                subtitle = "PC Host: ${settings.pcPairingHost}:${settings.pcPairingPort} • Honest status",
                icon = Icons.Default.Hub,
                accent = UltronCyan,
                tag = "settings_row_connectors",
                onClick = { onNavigate(UltronDestination.PC_PHONE) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Backup & Local Database",
                subtitle = "$memoriesCount Yaadein • $journalsCount Journals • $chatsCount Messages",
                icon = Icons.Default.Backup,
                accent = UltronViolet,
                tag = "settings_row_backup",
                onClick = { onNavigate(UltronDestination.SETTINGS_BACKUP) }
            )
        }

        // 5. LOOK & FEEL
        SettingsGroupHeader("LOOK & FEEL")
        SettingsGroupCard {
            SettingsNavRow(
                title = "Appearance & 3D Avatar Core",
                subtitle = "Active Core: ${settings.selectedAvatarId} • Khronos GLB 2.0",
                icon = Icons.Default.ViewInAr,
                accent = UltronCyan,
                tag = "settings_row_avatar",
                onClick = { onNavigate(UltronDestination.AVATAR_STUDIO) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Theme & Corners",
                subtitle = "Surface: ${settings.surfaceStyle.label} • Corners: ${settings.cornerStyle.label}",
                icon = Icons.Default.Palette,
                accent = Color(0xFFEC4899),
                tag = "settings_row_theme",
                onClick = { onNavigate(UltronDestination.SETTINGS_THEME) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Realistic Typing",
                subtitle = if (settings.realisticTypingEnabled) "ON • Speed: ${settings.realisticTypingSpeed}" else "OFF",
                icon = Icons.Default.Keyboard,
                accent = UltronGold,
                tag = "settings_row_typing",
                onClick = { onNavigate(UltronDestination.SETTINGS_TYPING) }
            )
        }

        // 6. ACCESSIBILITY & CONTROL
        SettingsGroupHeader("ACCESSIBILITY & CONTROL")
        SettingsGroupCard {
            SettingsNavRow(
                title = "Accessibility & Screen Assist",
                subtitle = "Screen Samjho, Android Accessibility aur Battery rules",
                icon = Icons.Default.AccessibilityNew,
                accent = UltronEmerald,
                tag = "settings_row_accessibility",
                onClick = { onNavigate(UltronDestination.ACCESSIBILITY_GUIDE) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Voice Guardian (Safety Dialer)",
                subtitle = "Emergency 112 dialer intent handoff with user confirmation",
                icon = Icons.Default.PhoneInTalk,
                accent = UltronCrimson,
                tag = "settings_row_guardian",
                onClick = { onNavigate(UltronDestination.SETTINGS_VOICE_GUARDIAN) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = UltronGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Battery Saver",
                            style = MaterialTheme.typography.titleSmall,
                            color = UltronTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Animations aur background polling ko halka rakhta hai",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                    }
                }
                Switch(
                    checked = settings.batterySaverMode,
                    onCheckedChange = onToggleBatterySaver
                )
            }
        }

        // 7. PRIVACY, DIAGNOSTICS & RULES
        SettingsGroupHeader("PRIVACY & DIAGNOSTICS")
        SettingsGroupCard {
            SettingsNavRow(
                title = "Permissions & Diagnostics",
                subtitle = "Mic, Camera, API Key aur Real Device Telemetry",
                icon = Icons.Default.Settings,
                accent = UltronCyan,
                tag = "settings_row_diagnostics",
                onClick = { onNavigate(UltronDestination.DIAGNOSTICS) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Ultron & Max ke rules",
                subtitle = "Safety, confirmation aur privacy rules",
                icon = Icons.Default.Gavel,
                accent = UltronGold,
                tag = "settings_row_rules",
                onClick = { onNavigate(UltronDestination.RULES) }
            )
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.4f))
            SettingsNavRow(
                title = "Privacy Policy & About",
                subtitle = "On-device Room storage • Creator: ${settings.configuredCreator}",
                icon = Icons.Default.Lock,
                accent = UltronViolet,
                tag = "settings_row_privacy",
                onClick = { onNavigate(UltronDestination.PRIVACY_POLICY) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SettingsGroupHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = UltronTextMuted,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingsGroupCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = UltronSurface),
        border = BorderStroke(1.dp, UltronGlassBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.16f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = UltronTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open $title",
            tint = UltronTextMuted
        )
    }
}

/**
 * Reference Personal Settings Detail Screen:
 * - Tumhe kya bulaun? (Fardeen)
 * - Gender verb selector (Ladka / Ladki / Nahi batana)
 * - Mera WhatsApp / Phone number
 * - Preferred Music App (YouTube / Spotify / Apple Music / JioSaavn / Gaana / YT Music)
 * - Favourite Song
 * - Custom Gemini API Key with live [ Test Key ] button
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UltronPersonalSettingsScreen(
    settings: UltronSettingsState,
    providerTestStatus: String?,
    onSavePersonal: (String, GenderVerbStyle, String, String, String, String) -> Unit,
    onTestApiKey: (String) -> Unit
) {
    var nameInput by remember(settings.userAddress) { mutableStateOf(settings.userAddress) }
    var genderInput by remember(settings.genderVerbStyle) { mutableStateOf(settings.genderVerbStyle) }
    var phoneInput by remember(settings.userPhoneNumber) { mutableStateOf(settings.userPhoneNumber) }
    var musicAppInput by remember(settings.preferredMusicApp) { mutableStateOf(settings.preferredMusicApp) }
    var favSongInput by remember(settings.favouriteSong) { mutableStateOf(settings.favouriteSong) }
    var apiKeyInput by remember(settings.customGeminiApiKey) { mutableStateOf(settings.customGeminiApiKey) }

    val musicApps = listOf("YouTube", "Spotify", "Apple Music", "JioSaavn", "Gaana", "YT Music")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Personal Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Protected Creator Attribution: ${settings.configuredCreator}",
            style = MaterialTheme.typography.bodySmall,
            color = UltronCyan
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Tumhe kya bulaun? (User Name)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "TUM KAUN HO? (Hinglish Verb Agreement)",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GenderVerbStyle.entries.forEach { style ->
                        FilterChip(
                            selected = genderInput == style,
                            onClick = { genderInput = style },
                            label = { Text(style.labelHinglish) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UltronCyan.copy(alpha = 0.2f),
                                selectedLabelColor = UltronCyan
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Mera WhatsApp / Phone Number (Optional)") },
                    placeholder = { Text("919876543210") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "PREFERRED MUSIC APP",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    musicApps.forEach { app ->
                        FilterChip(
                            selected = musicAppInput == app,
                            onClick = { musicAppInput = app },
                            label = { Text(app) }
                        )
                    }
                }

                OutlinedTextField(
                    value = favSongInput,
                    onValueChange = { favSongInput = it },
                    label = { Text("Pasandida Gaana (Favourite Song)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Gemini API Key Card with Live Test Button
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.45f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = UltronCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GEMINI API KEY (Cloud Brain)",
                        style = MaterialTheme.typography.titleMedium,
                        color = UltronTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "AI Studio Secrets panel (BuildConfig.GEMINI_API_KEY) ya yahan apni Gemini API key daal kar Test karein.",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronTextSecondary
                )
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text("Custom Gemini API Key (AIza...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!providerTestStatus.isNullOrBlank()) {
                    Text(
                        text = providerTestStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (providerTestStatus.startsWith("Verified")) UltronEmerald else UltronGold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onTestApiKey(apiKeyInput) },
                        border = BorderStroke(1.dp, UltronCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Key", color = UltronCyan, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            onSavePersonal(
                                nameInput,
                                genderInput,
                                phoneInput,
                                musicAppInput,
                                favSongInput,
                                apiKeyInput
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UltronCyan,
                            contentColor = UltronObsidian
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Settings", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Dual Persona Switcher & Companion Tone Screen ("Ultron" ⇄ "Max"):
 * Allows instant switching or voice command "Max ko bhej" / "Ultron ko bhej".
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UltronPersonaSwitcherScreen(
    settings: UltronSettingsState,
    onSwitchPersona: (AssistantPersona) -> Unit,
    onUpdateTone: (String) -> Unit
) {
    val tones = listOf("Cinematic Sci-Fi", "Tactical Buddy", "Calm Mentor", "Witty Hinglish")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Ultron ⇄ Max Dual Core",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Aap kabhi bhi mic ya chat me 'Max ko bhej' ya 'Ultron ko bhej' bolkar assistant aur UI badal sakte hain!",
            style = MaterialTheme.typography.bodySmall,
            color = UltronCyan
        )

        AssistantPersona.entries.forEach { persona ->
            val isSelected = settings.activePersona == persona
            val accent = if (persona == AssistantPersona.MAX) UltronEmerald else UltronCyan

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) UltronSurfaceElevated else UltronSurface
                ),
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, accent),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchPersona(persona) }
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
                            text = "${persona.displayName} (${persona.coreSpacedTitle})",
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSelected) {
                            Surface(
                                color = accent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = persona.roleSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                    Text(
                        text = if (persona == AssistantPersona.ULTRON) {
                            "Composed, analytical cyber-intelligence. Calls you ${settings.userAddress}. Deep voice pitch (${persona.defaultPitch}x)."
                        } else {
                            "Witty, energetic tactical companion. Calls you Boss. Crisp natural voice (${persona.defaultPitch}x)."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = UltronTextSecondary
                    )
                }
            }
        }

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
                    text = "COMPANION CONVERSATION TONE",
                    style = MaterialTheme.typography.titleMedium,
                    color = UltronCyan
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tones.forEach { tone ->
                        FilterChip(
                            selected = settings.companionToneMode == tone,
                            onClick = { onUpdateTone(tone) },
                            label = { Text(tone) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Look & Feel (Theme Surface & Corners) + Realistic Typing Screen matching Reference Video:
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UltronLookAndFeelScreen(
    settings: UltronSettingsState,
    onUpdateLookAndFeel: (SurfaceThemeStyle, CornerThemeStyle) -> Unit,
    onUpdateRealisticTyping: (Boolean, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Theme, Corners & Realistic Typing",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )

        // Surface Style (Flat / Glass / Soft / Clay)
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(settings.cornerStyle.radiusDp.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SURFACE TEXTURE (LOOK & FEEL)",
                    style = MaterialTheme.typography.titleMedium,
                    color = UltronCyan
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SurfaceThemeStyle.entries.forEach { surface ->
                        FilterChip(
                            selected = settings.surfaceStyle == surface,
                            onClick = { onUpdateLookAndFeel(surface, settings.cornerStyle) },
                            label = { Text(surface.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "CORNERS (CARD RADIUS)",
                    style = MaterialTheme.typography.titleMedium,
                    color = UltronCyan
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CornerThemeStyle.entries.forEach { corner ->
                        FilterChip(
                            selected = settings.cornerStyle == corner,
                            onClick = { onUpdateLookAndFeel(settings.surfaceStyle, corner) },
                            label = { Text(corner.label) }
                        )
                    }
                }
            }
        }

        // Realistic Typing Card
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(settings.cornerStyle.radiusDp.dp),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Realistic Typing",
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Jawab ek-ek shabd karke natural speed me dikhega",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                    }
                    Switch(
                        checked = settings.realisticTypingEnabled,
                        onCheckedChange = { onUpdateRealisticTyping(it, settings.realisticTypingSpeed) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Dheere", "Normal", "Tez").forEach { spd ->
                        FilterChip(
                            selected = settings.realisticTypingSpeed == spd,
                            onClick = { onUpdateRealisticTyping(settings.realisticTypingEnabled, spd) },
                            label = { Text(spd) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reference Markets Screen (Watchlist + AI Market Query)
 */
@Composable
fun UltronMarketsScreen(
    watchlist: List<MarketWatchItem>,
    onAddSymbol: (String, String) -> Unit,
    onRemoveSymbol: (String) -> Unit,
    onAskMarketAi: (String) -> Unit
) {
    var symbolInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = UltronCyan)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Markets & Watchlist",
                style = MaterialTheme.typography.headlineMedium,
                color = UltronTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Apne stock/crypto symbols track karein aur AI se market concepts samjhein.",
            style = MaterialTheme.typography.bodySmall,
            color = UltronTextSecondary
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("WATCHLIST ME SYMBOL JODEIN", style = MaterialTheme.typography.labelMedium, color = UltronCyan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = symbolInput,
                        onValueChange = { symbolInput = it },
                        label = { Text("Symbol (e.g. INFY.NS)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Sector/Note") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        if (symbolInput.isNotBlank()) {
                            onAddSymbol(symbolInput, noteInput.ifBlank { "Tracked Symbol" })
                            symbolInput = ""
                            noteInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UltronCyan, contentColor = UltronObsidian),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Watchlist", fontWeight = FontWeight.Bold)
                }
            }
        }

        watchlist.forEach { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
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
                            text = item.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${item.name} • ${item.noteHinglish}",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                    }
                    Row {
                        OutlinedButton(
                            onClick = { onAskMarketAi("Explain ${item.symbol} (${item.name}) business model and key metrics in Hinglish") }
                        ) {
                            Text("AI Brief", style = MaterialTheme.typography.labelSmall, color = UltronCyan)
                        }
                        IconButton(onClick = { onRemoveSymbol(item.symbol) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = UltronCrimson)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reference Website / Coding Screen matching Video:
 * "Naya Web App" | "Naya Mobile App" + Recent Projects list + Code Viewer
 */
@Composable
fun UltronCodingScreen(
    projects: List<CodingProjectItem>,
    onCreateProject: (String, String) -> Unit,
    onAskCodingAi: (String) -> Unit
) {
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
    var promptText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Website / Coding",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )

        // Two primary cards from Reference Video: "Naya Web App" and "Naya Mobile App"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronCyan.copy(alpha = 0.14f)),
                border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onCreateProject(
                            "Web App • Glass Portfolio",
                            "<!DOCTYPE html>\n<html><body style='background:#060A14;color:#00E5FF'><h1>ULTRON Web Core</h1></body></html>"
                        )
                    }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = UltronCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Naya Web App", style = MaterialTheme.typography.titleMedium, color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                    Text("HTML/JS/Three.js template", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = UltronViolet.copy(alpha = 0.14f)),
                border = BorderStroke(1.dp, UltronViolet.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onCreateProject(
                            "Mobile Compose Screen",
                            "@Composable\nfun CyberCard() {\n    Surface(color = UltronSurface) { Text(\"Hello Fardeen\") }\n}"
                        )
                    }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Icon(Icons.Default.Computer, contentDescription = null, tint = UltronViolet)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Naya Mobile App", style = MaterialTheme.typography.titleMedium, color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                    Text("Kotlin Compose module", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                }
            }
        }

        // AI Code Generator Input
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("AI CODE GENERATOR", style = MaterialTheme.typography.labelMedium, color = UltronCyan)
                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    placeholder = { Text("e.g. Write a Kotlin coroutine retry helper or Three.js shader...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (promptText.isNotBlank()) {
                            onAskCodingAi("Write clean production code for: $promptText")
                            promptText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UltronCyan, contentColor = UltronObsidian),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate Code in Chat", fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("TUMHARE PROJECTS", style = MaterialTheme.typography.labelMedium, color = UltronTextMuted)

        projects.forEach { proj ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated),
                border = BorderStroke(
                    1.dp,
                    if (selectedProject?.id == proj.id) UltronCyan else UltronGlassBorder
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedProject = proj }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(proj.title, style = MaterialTheme.typography.titleMedium, color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                        Text(proj.updatedAgo, style = MaterialTheme.typography.labelSmall, color = UltronTextMuted)
                    }
                    Text(proj.typeLabel, style = MaterialTheme.typography.bodySmall, color = UltronCyan)
                }
            }
        }

        selectedProject?.let { active ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronObsidian),
                border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("CODE PREVIEW: ${active.title}", style = MaterialTheme.typography.labelSmall, color = UltronCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = active.codeSnippet,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        color = UltronEmerald
                    )
                }
            }
        }
    }
}

/**
 * Reference Accessibility & Background Guide Screen matching Video:
 * Honest status cards + real system settings intents.
 */
@Composable
fun UltronAccessibilityGuideScreen(
    hasMicPermission: Boolean,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenAppPermissions: () -> Unit,
    onOpenVisionCenter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Accessibility & Background",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Android OS permissions aur Screen Assist ke real controls.",
            style = MaterialTheme.typography.bodySmall,
            color = UltronCyan
        )

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
                    text = "1. Screen Samjho & UI Reading",
                    style = MaterialTheme.typography.titleMedium,
                    color = UltronTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ULTRON aur MAX aapki chuni hui screenshot ya camera frame ko analyze karke buttons highlight karte hain. Android Accessibility Settings kholne ke liye neeche button dabayein.",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronTextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpenVisionCenter,
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCyan, contentColor = UltronObsidian),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Open Vision", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onOpenAccessibilitySettings,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Android Accessibility", color = UltronCyan)
                    }
                }
            }
        }

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
                    text = "2. Background & Battery Optimization",
                    style = MaterialTheme.typography.titleMedium,
                    color = UltronTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Android OS battery bachane ke liye background mic/services ko pause kar sakta hai. Unrestricted battery setting ke liye neeche click karein.",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronTextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenBatterySettings,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Battery Settings", color = UltronGold)
                    }
                    OutlinedButton(
                        onClick = onOpenAppPermissions,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (hasMicPermission) "Permissions (OK)" else "Fix Mic Permission", color = UltronEmerald)
                    }
                }
            }
        }
    }
}

/**
 * Marketing, Documents, Rules, PC <-> Phone, Messages, Voice Guardian & Privacy Screens
 */
@Composable
fun UltronMarketingAndDocsScreen(
    isMarketing: Boolean,
    onSubmitAiTask: (String) -> Unit,
    onOpenScanner: () -> Unit
) {
    var topicInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isMarketing) "Marketing & Copywriting Studio" else "Documents & Report Studio",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isMarketing) {
                "Reels script, YouTube hooks, product launch copy aur social posts banayein."
            } else {
                "Notes, structured summaries, study handouts aur OCR scan analyze karein."
            },
            style = MaterialTheme.typography.bodySmall,
            color = UltronCyan
        )

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
                OutlinedTextField(
                    value = topicInput,
                    onValueChange = { topicInput = it },
                    label = {
                        Text(if (isMarketing) "Campaign ya Product Topic" else "Document Topic ya Text")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (topicInput.isNotBlank()) {
                                val prompt = if (isMarketing) {
                                    "Create a high-converting Hinglish marketing campaign, Reel hook, and caption for: $topicInput"
                                } else {
                                    "Create a clean structured summary and key bullet points for: $topicInput"
                                }
                                onSubmitAiTask(prompt)
                                topicInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCyan, contentColor = UltronObsidian),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isMarketing) "Generate Campaign" else "Summarize / Draft", fontWeight = FontWeight.Bold)
                    }
                    if (!isMarketing) {
                        OutlinedButton(onClick = onOpenScanner) {
                            Text("Open Scanner", color = UltronCyan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UltronUtilityDetailScreen(
    destination: UltronDestination,
    settings: UltronSettingsState,
    memoriesCount: Int,
    journalsCount: Int,
    chatsCount: Int,
    onUpdatePcPairing: (String, String) -> Unit,
    onSendEmail: (String, String, String) -> Unit,
    onSendWhatsApp: (String, String) -> Unit,
    onTriggerEmergencyDial: (String) -> Unit
) {
    var hostInput by remember(settings.pcPairingHost) { mutableStateOf(settings.pcPairingHost) }
    var portInput by remember(settings.pcPairingPort) { mutableStateOf(settings.pcPairingPort) }
    var emailTo by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    var confirmEmergency by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = destination.titleHinglish,
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary,
            fontWeight = FontWeight.Bold
        )

        when (destination) {
            UltronDestination.PC_PHONE, UltronDestination.SETTINGS_CONNECTORS -> {
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
                            text = "PC ⇄ Phone Local Bridge Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Honest Status: Local LAN endpoint save karein. Jab aapke PC par companion server active hoga tabhi bridge connect hoga.",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                        OutlinedTextField(
                            value = hostInput,
                            onValueChange = { hostInput = it },
                            label = { Text("PC Local IP Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = portInput,
                            onValueChange = { portInput = it },
                            label = { Text("Port") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = { onUpdatePcPairing(hostInput, portInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = UltronCyan, contentColor = UltronObsidian),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Endpoint", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            UltronDestination.SETTINGS_MESSAGES -> {
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
                        Text("Email & WhatsApp Intent Handoff", style = MaterialTheme.typography.titleMedium, color = UltronCyan)
                        Text(
                            text = "Android security ke mutabik message aapke official Email ya WhatsApp app me pre-fill hokar khulega Jahan aap Send confirm karenge.",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                        OutlinedTextField(
                            value = emailTo,
                            onValueChange = { emailTo = it },
                            label = { Text("Recipient Email ya Phone Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = emailBody,
                            onValueChange = { emailBody = it },
                            label = { Text("Message Content") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onSendEmail(emailTo, "Message from ULTRON AI", emailBody) },
                                colors = ButtonDefaults.buttonColors(containerColor = UltronCyan, contentColor = UltronObsidian),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Open Email", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onSendWhatsApp(emailTo, emailBody) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Open WhatsApp/SMS", color = UltronEmerald)
                            }
                        }
                    }
                }
            }

            UltronDestination.SETTINGS_VOICE_GUARDIAN -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = UltronSurface),
                    border = BorderStroke(1.dp, UltronCrimson.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Voice Guardian & Emergency Dialer", style = MaterialTheme.typography.titleMedium, color = UltronCrimson, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Safety Rule: Bina aapke explicit confirmation ke koi call dial nahi hota. Confirm karne par Android Dialer 112 ke saath khulega.",
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextSecondary
                        )
                        if (!confirmEmergency) {
                            OutlinedButton(
                                onClick = { confirmEmergency = true },
                                border = BorderStroke(1.dp, UltronCrimson),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Prepare Emergency 112 Dialer", color = UltronCrimson, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        confirmEmergency = false
                                        onTriggerEmergencyDial("112")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = UltronCrimson),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Confirm Open Dialer (112)", fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { confirmEmergency = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // Rules, Backup, Skills, Privacy Policy, About
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
                            text = "ULTRON AI & MAX • Core Rules & Storage Summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Protected Creator Attribution: Mujhe Fardeen ne banaya aur configure kiya hai.\n" +
                                "• Dual Persona Handoff: 'Max ko bhej' aur 'Ultron ko bhej' se instant persona + voice switch.\n" +
                                "• Zero Fake Telemetry: RAM, Storage, Battery, Network aur CPU cores seedhe Android OS APIs se aate hain.\n" +
                                "• Local Room Database: $memoriesCount Yaadein, $journalsCount Journals, $chatsCount Chat messages on-device surakshit hain.\n" +
                                "• Privacy: Koi bhi screen capture ya mic session bina aapke action aur visible state indicator ke nahi chalta.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = UltronTextPrimary
                        )
                    }
                }
            }
        }
    }
}
