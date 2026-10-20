package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.VoiceChat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.domain.model.AssistantPersona
import com.example.domain.model.UltronDestination
import com.example.ui.theme.UltronCrimson
import com.example.ui.theme.UltronCyan
import com.example.ui.theme.UltronEmerald
import com.example.ui.theme.UltronGlassBorder
import com.example.ui.theme.UltronGold
import com.example.ui.theme.UltronSurface
import com.example.ui.theme.UltronSurfaceElevated
import com.example.ui.theme.UltronTextMuted
import com.example.ui.theme.UltronTextPrimary
import com.example.ui.theme.UltronTextSecondary
import com.example.ui.theme.UltronViolet

/**
 * Requirement 17: Dedicated TOOLS Hub Screen
 * Houses Files, Web, Communication, and Daily Utilities.
 * Clean, organized cards where every single button actually works.
 */
@Composable
fun UltronToolsHubScreen(
    activePersona: AssistantPersona,
    onNavigate: (UltronDestination) -> Unit,
    onSendEmailIntent: (String, String, String) -> Unit,
    onSendWhatsAppIntent: (String, String) -> Unit,
    onTriggerDialerIntent: (String) -> Unit
) {
    val context = LocalContext.current
    val accentColor = if (activePersona == AssistantPersona.MAX) UltronEmerald else UltronCyan
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
        ) {
            Text(
                text = "TOOLS & UTILITIES",
                style = MaterialTheme.typography.titleLarge,
                color = UltronTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Files, Web, Communication aur Daily Assistants — sab ek jagah",
                style = MaterialTheme.typography.bodySmall,
                color = UltronTextSecondary
            )
        }

        // Section 1: Files, Notes & Work
        ToolSectionCard(
            sectionTitle = "FILES, NOTES & CODE",
            sectionAccent = UltronGold,
            tools = listOf(
                ToolItemData(
                    title = "Files & Docs",
                    subtitle = "System Documents & Vault",
                    icon = Icons.Default.Folder,
                    color = UltronGold,
                    tag = "tool_files_docs",
                    onClick = { onNavigate(UltronDestination.DOCUMENTS) }
                ),
                ToolItemData(
                    title = "Journal",
                    subtitle = "Personal Notes & Logs",
                    icon = Icons.Default.EditNote,
                    color = UltronViolet,
                    tag = "tool_journal",
                    onClick = { onNavigate(UltronDestination.JOURNAL) }
                ),
                ToolItemData(
                    title = "Coding Projects",
                    subtitle = "Snippets & Dev Projects",
                    icon = Icons.Default.Code,
                    color = UltronCyan,
                    tag = "tool_coding",
                    onClick = { onNavigate(UltronDestination.CODING) }
                ),
                ToolItemData(
                    title = "Dream Journal",
                    subtitle = "Jungian Archetypes & Logs",
                    icon = Icons.Default.Nightlight,
                    color = UltronViolet,
                    tag = "tool_dream",
                    onClick = { onNavigate(UltronDestination.DREAM_JOURNAL) }
                )
            )
        )

        // Section 2: Web & Research
        ToolSectionCard(
            sectionTitle = "WEB & RESEARCH",
            sectionAccent = UltronCyan,
            tools = listOf(
                ToolItemData(
                    title = "Google Search",
                    subtitle = "Open Web Browser",
                    icon = Icons.Default.Language,
                    color = UltronCyan,
                    tag = "tool_google_search",
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                ),
                ToolItemData(
                    title = "Markets & Crypto",
                    subtitle = "Watchlist & Market Intelligence",
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    color = UltronEmerald,
                    tag = "tool_markets",
                    onClick = { onNavigate(UltronDestination.MARKETS) }
                ),
                ToolItemData(
                    title = "Marketing & Copy",
                    subtitle = "Pitch, Campaigns & Social",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = UltronGold,
                    tag = "tool_marketing",
                    onClick = { onNavigate(UltronDestination.MARKETING) }
                ),
                ToolItemData(
                    title = "Scanner (OCR/QR)",
                    subtitle = "Camera Vision & Document Scan",
                    icon = Icons.Default.QrCodeScanner,
                    color = UltronCyan,
                    tag = "tool_scanner",
                    onClick = { onNavigate(UltronDestination.SCAN) }
                )
            )
        )

        // Section 3: Communication
        ToolSectionCard(
            sectionTitle = "COMMUNICATION",
            sectionAccent = UltronEmerald,
            tools = listOf(
                ToolItemData(
                    title = "Phone Dialer",
                    subtitle = "Standard Android dialer",
                    icon = Icons.Default.Phone,
                    color = UltronEmerald,
                    tag = "tool_dialer",
                    onClick = { onTriggerDialerIntent("") }
                ),
                ToolItemData(
                    title = "WhatsApp / SMS",
                    subtitle = "Direct message handoff",
                    icon = Icons.Default.VoiceChat,
                    color = UltronEmerald,
                    tag = "tool_whatsapp",
                    onClick = { onSendWhatsAppIntent("", "Namaste from MAX AI!") }
                ),
                ToolItemData(
                    title = "Email Composer",
                    subtitle = "Intent with draft content",
                    icon = Icons.Default.Email,
                    color = UltronCyan,
                    tag = "tool_email",
                    onClick = { onSendEmailIntent("", "Message from MAX AI", "Hey Boss,") }
                ),
                ToolItemData(
                    title = "Emergency (112)",
                    subtitle = "Instant emergency dialer",
                    icon = Icons.Default.Warning,
                    color = UltronCrimson,
                    tag = "tool_emergency",
                    onClick = { onTriggerDialerIntent("112") }
                )
            )
        )

        // Section 4: Daily Utilities & Lifestyle
        ToolSectionCard(
            sectionTitle = "DAILY UTILITIES",
            sectionAccent = accentColor,
            tools = listOf(
                ToolItemData(
                    title = "Mausam (Weather)",
                    subtitle = "Live city weather forecast",
                    icon = Icons.Default.Cloud,
                    color = UltronCyan,
                    tag = "tool_mausam",
                    onClick = { onNavigate(UltronDestination.MAUSAM) }
                ),
                ToolItemData(
                    title = "Aaj (Reminders)",
                    subtitle = "Tasks, alarms & agenda",
                    icon = Icons.Default.Today,
                    color = UltronGold,
                    tag = "tool_aaj",
                    onClick = { onNavigate(UltronDestination.AAJ) }
                ),
                ToolItemData(
                    title = "Gaane (Music)",
                    subtitle = "YouTube music & search",
                    icon = Icons.Default.MusicNote,
                    color = UltronCrimson,
                    tag = "tool_gaane",
                    onClick = { onNavigate(UltronDestination.GAANE) }
                ),
                ToolItemData(
                    title = "Padhai (Study)",
                    subtitle = "Formulae, concepts & quizzes",
                    icon = Icons.Default.School,
                    color = UltronViolet,
                    tag = "tool_padhai",
                    onClick = { onNavigate(UltronDestination.PADHAI) }
                ),
                ToolItemData(
                    title = "Mood Tracker",
                    subtitle = "Emotional logs & wellbeing",
                    icon = Icons.Default.Favorite,
                    color = UltronCrimson,
                    tag = "tool_mood",
                    onClick = { onNavigate(UltronDestination.MOOD) }
                ),
                ToolItemData(
                    title = "Awaaz Studio",
                    subtitle = "Voice speed, style & engines",
                    icon = Icons.Default.GraphicEq,
                    color = accentColor,
                    tag = "tool_awaaz_studio",
                    onClick = { onNavigate(UltronDestination.AWAAZ_STUDIO) }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private data class ToolItemData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val tag: String,
    val onClick: () -> Unit
)

@Composable
private fun ToolSectionCard(
    sectionTitle: String,
    sectionAccent: Color,
    tools: List<ToolItemData>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = UltronSurface),
        border = BorderStroke(1.dp, UltronGlassBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.labelSmall,
                color = sectionAccent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            val chunked = tools.chunked(2)
            for (rowTools in chunked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (tool in rowTools) {
                        Surface(
                            color = UltronSurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, tool.color.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { tool.onClick() }
                                .testTag(tool.tag)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(tool.color.copy(alpha = 0.16f))
                                ) {
                                    Icon(
                                        imageVector = tool.icon,
                                        contentDescription = tool.title,
                                        tint = tool.color,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tool.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = UltronTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = tool.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = UltronTextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    if (rowTools.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
