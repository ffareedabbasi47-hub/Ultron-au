package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DreamEntryEntity
import com.example.data.local.JournalEntryEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.MoodEntryEntity
import com.example.data.local.ReminderEntity
import com.example.domain.model.WeatherData
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
fun UltronGaaneScreen(
    onOpenYouTubeQuery: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Gaane & YouTube Handoff",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary
        )
        Text(
            text = "Official YouTube search & app intent handoff. Playback starts when you select a track.",
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
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search song, artist or video topic...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gaane_search_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onOpenYouTubeQuery(searchQuery.ifBlank { "Top Hindi Songs" }) },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCrimson),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("gaane_search_youtube_button")
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = UltronTextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Search on YouTube", color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onOpenYouTubeQuery("") },
                        border = BorderStroke(1.dp, UltronCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, tint = UltronCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open YouTube App", color = UltronCyan)
                    }
                }
            }
        }

        Text("QUICK MUSIC & VIDEO PICKS", style = MaterialTheme.typography.labelMedium, color = UltronCyan)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Arijit Singh Acoustic",
                "Coding Lo-Fi Beats",
                "Cinematic Cyberpunk Synthwave",
                "Quantum Physics in Hinglish",
                "Indian Classical Instrumental",
                "A.R. Rahman Best Tracks"
            ).forEach { topic ->
                AssistChip(
                    onClick = { onOpenYouTubeQuery(topic) },
                    label = { Text(topic) }
                )
            }
        }
    }
}

@Composable
fun UltronPadhaiScreen(
    messages: List<ChatMessageEntity>,
    streamingText: String,
    onSubmitStudyPrompt: (String) -> Unit,
    onNavigateToChat: () -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Easy Hinglish") }
    val latestAssistantReply = messages.lastOrNull { it.role == "assistant" }?.content.orEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Padhai • Hinglish Study Companion",
                style = MaterialTheme.typography.headlineMedium,
                color = UltronTextPrimary
            )
            Text(
                text = "Step-by-step concepts, real-world examples, aur practice questions.",
                style = MaterialTheme.typography.bodySmall,
                color = UltronCyan
            )
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic ya concept likho (e.g. Gravity, Black Holes, Recursion)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("padhai_topic_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Easy Hinglish", "Exam Prep", "Deep STEM").forEach { lvl ->
                            FilterChip(
                                selected = level == lvl,
                                onClick = { level = lvl },
                                label = { Text(lvl, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val t = topic.ifBlank { "Gravity aur Newton ke laws" }
                                onSubmitStudyPrompt("Padhai Mode ($level): '$t' ko natural Hinglish mein step-by-step example ke saath samjhao.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("padhai_explain_button")
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = UltronObsidian)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Samjhao", color = UltronObsidian, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val t = topic.ifBlank { "Science & Logic" }
                                onSubmitStudyPrompt("Padhai Quiz ($level): '$t' par 3 short practice questions aur hints do.")
                            },
                            border = BorderStroke(1.dp, UltronViolet),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Practice Quiz", color = UltronTextPrimary)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated),
                border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ULTRON STUDY EXPLANATION", style = MaterialTheme.typography.labelMedium, color = UltronCyan)
                        Text(
                            text = "Open Full Chat →",
                            style = MaterialTheme.typography.labelSmall,
                            color = UltronCyan,
                            modifier = Modifier.clickable { onNavigateToChat() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = streamingText.ifBlank { latestAssistantReply.ifBlank { "Topic enter karke 'Samjhao' dabayein." } },
                        style = MaterialTheme.typography.bodyLarge,
                        color = UltronTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun UltronJournalScreen(
    entries: List<JournalEntryEntity>,
    onAddEntry: (String, String, String) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(entries, searchQuery) {
        if (searchQuery.isBlank()) entries
        else entries.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.body.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Journal & Notes", style = MaterialTheme.typography.headlineMedium, color = UltronTextPrimary)
                    Text("${entries.size} saved notes • Local Persistent Storage", style = MaterialTheme.typography.bodySmall, color = UltronCyan)
                }
                if (entries.isNotEmpty()) {
                    OutlinedButton(onClick = onClearAll) {
                        Text("Clear All", style = MaterialTheme.typography.labelSmall, color = UltronCrimson)
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
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Note Title") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_title_input")
                    )
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Write your note or thought...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_body_input")
                    )
                    Button(
                        onClick = {
                            if (title.isNotBlank() || body.isNotBlank()) {
                                onAddEntry(title, body, "Note")
                                title = ""
                                body = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_save_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = UltronObsidian)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Journal Entry", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search saved notes...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(filtered, key = { it.id }) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium, color = UltronCyan, fontWeight = FontWeight.Bold)
                        Row {
                            IconButton(
                                onClick = {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cb.setPrimaryClip(ClipData.newPlainText(item.title, "${item.title}\n${item.body}"))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Export/Copy", tint = UltronTextSecondary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onDeleteEntry(item.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = UltronCrimson, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Text(item.body, style = MaterialTheme.typography.bodyMedium, color = UltronTextPrimary)
                }
            }
        }
    }
}

@Composable
fun UltronDreamJournalScreen(
    dreams: List<DreamEntryEntity>,
    onRecordVoiceForDream: () -> Unit,
    onSaveDream: (String, String) -> Unit,
    onDeleteDream: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var transcript by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Dream Journal • Jungian Reflection", style = MaterialTheme.typography.headlineMedium, color = UltronTextPrimary)
            Text(
                text = "Exploratory symbolic reflection & optional surreal art (Not a clinical/medical diagnosis).",
                style = MaterialTheme.typography.bodySmall,
                color = UltronViolet
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface),
                border = BorderStroke(1.dp, UltronViolet.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Dream Title (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = transcript,
                        onValueChange = { transcript = it },
                        label = { Text("Describe your dream...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dream_transcript_input")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRecordVoiceForDream,
                            border = BorderStroke(1.dp, UltronCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = UltronCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Voice Dictate", color = UltronCyan)
                        }

                        Button(
                            onClick = {
                                if (transcript.isNotBlank()) {
                                    onSaveDream(title, transcript)
                                    title = ""
                                    transcript = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UltronViolet),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("dream_save_button")
                        ) {
                            Icon(Icons.Default.NightsStay, contentDescription = null, tint = UltronTextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reflect & Save", color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(dreams, key = { it.id }) { dream ->
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated),
                border = BorderStroke(1.dp, UltronGlassBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dream.title, style = MaterialTheme.typography.titleMedium, color = UltronViolet, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { onDeleteDream(dream.id) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Dream", tint = UltronCrimson)
                        }
                    }
                    Text("Transcript: ${dream.transcript}", style = MaterialTheme.typography.bodyMedium, color = UltronTextPrimary)
                    Text("Archetype Symbols: ${dream.symbolsDetected}", style = MaterialTheme.typography.labelSmall, color = UltronCyan)
                    Text(dream.jungianReflection, style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)

                    if (!dream.generatedImageBase64.isNullOrBlank()) {
                        val decodedBmp = remember(dream.generatedImageBase64) {
                            try {
                                val bytes = Base64.decode(dream.generatedImageBase64, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                            } catch (_: Exception) {
                                null
                            }
                        }
                        if (decodedBmp != null) {
                            Image(
                                bitmap = decodedBmp,
                                contentDescription = "Dream Symbolic Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UltronMausamScreen(
    weatherData: WeatherData?,
    weatherError: String?,
    isLoading: Boolean,
    currentCity: String,
    onRefreshCity: (String) -> Unit
) {
    var cityInput by remember(currentCity) { mutableStateOf(currentCity) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mausam • Live Open-Meteo Weather", style = MaterialTheme.typography.headlineMedium, color = UltronTextPrimary)
        Text("Real meteorological telemetry—never fabricated.", style = MaterialTheme.typography.bodySmall, color = UltronCyan)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("City Name") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("mausam_city_input")
            )
            Button(
                onClick = { onRefreshCity(cityInput) },
                colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("mausam_fetch_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = UltronObsidian)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Fetch", color = UltronObsidian, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("New Delhi", "Mumbai", "Bengaluru", "Hyderabad", "Lucknow", "Kolkata").forEach { c ->
                AssistChip(
                    onClick = {
                        cityInput = c
                        onRefreshCity(c)
                    },
                    label = { Text(c) }
                )
            }
        }

        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircularProgressIndicator(color = UltronCyan, modifier = Modifier.size(24.dp))
                Text("Fetching real Open-Meteo weather...", color = UltronTextPrimary)
            }
        } else if (weatherData != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurface),
                border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(weatherData.city, style = MaterialTheme.typography.titleLarge, color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                            Text(weatherData.conditionText, style = MaterialTheme.typography.bodyMedium, color = UltronCyan)
                        }
                        Text(
                            text = "${weatherData.temperatureC}°C",
                            style = MaterialTheme.typography.displayMedium,
                            color = UltronCyan
                        )
                    }
                    Text(
                        text = "Feels Like: ${weatherData.feelsLikeC}°C • Humidity: ${weatherData.humidityPercent}% • Wind: ${weatherData.windSpeedKmh} km/h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = UltronTextSecondary
                    )
                    Text(
                        text = "Source: ${weatherData.source} • Updated: ${weatherData.updatedAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = UltronTextMuted
                    )
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated),
                border = BorderStroke(1.dp, UltronGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = weatherError ?: "Mausam ke liye setup ya internet chahiye.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = UltronGold
                    )
                }
            }
        }
    }
}

@Composable
fun UltronAajRemindersScreen(
    reminders: List<ReminderEntity>,
    onAddReminder: (String, String) -> Unit,
    onToggleReminder: (ReminderEntity) -> Unit,
    onDeleteReminder: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var timeLabel by remember { mutableStateOf("06:00 PM") }
    val fullDateStr = SimpleDateFormat("EEEE, dd MMMM yyyy • hh:mm a", Locale.US).format(Date())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Aaj • Real Device Date & Reminders", style = MaterialTheme.typography.headlineMedium, color = UltronTextPrimary)
            Text(fullDateStr, style = MaterialTheme.typography.titleMedium, color = UltronGold)
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
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("New Reminder / Task") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aaj_reminder_title_input")
                    )
                    OutlinedTextField(
                        value = timeLabel,
                        onValueChange = { timeLabel = it },
                        label = { Text("Time (e.g. 07:30 PM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAddReminder(title, timeLabel)
                                title = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("aaj_add_reminder_button")
                    ) {
                        Text("Add Reminder", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(reminders, key = { it.id }) { rem ->
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onToggleReminder(rem) }
                    ) {
                        Icon(
                            imageVector = if (rem.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (rem.isCompleted) UltronEmerald else UltronCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = rem.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (rem.isCompleted) UltronTextMuted else UltronTextPrimary,
                                textDecoration = if (rem.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text(rem.timeLabel, style = MaterialTheme.typography.labelSmall, color = UltronGold)
                        }
                    }
                    IconButton(onClick = { onDeleteReminder(rem.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = UltronCrimson)
                    }
                }
            }
        }
    }
}

@Composable
fun UltronMoodScreen(
    moods: List<MoodEntryEntity>,
    onAddMood: (String, String, Int, String) -> Unit,
    onDeleteMood: (Long) -> Unit
) {
    var note by remember { mutableStateOf("") }
    val moodOptions = listOf(
        Triple("⚡", "Focused", 5),
        Triple("😌", "Calm", 4),
        Triple("🔥", "Energized", 5),
        Triple("🌙", "Tired", 2),
        Triple("🌀", "Stressed", 1)
    )
    var selectedMood by remember { mutableStateOf(moodOptions.first()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Mood Check-In & Trends", style = MaterialTheme.typography.headlineMedium, color = UltronTextPrimary)
            Text("Voluntary private mood reflection (No medical diagnosis claims).", style = MaterialTheme.typography.bodySmall, color = UltronEmerald)
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        moodOptions.forEach { option ->
                            val isSelected = selectedMood.second == option.second
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedMood = option },
                                label = { Text("${option.first} ${option.second}") }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Short note (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onAddMood(selectedMood.first, selectedMood.second, selectedMood.third, note)
                            note = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mood_log_button")
                    ) {
                        Text("Log Mood (${selectedMood.first} ${selectedMood.second})", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(moods, key = { it.id }) { m ->
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
                        Text("${m.moodEmoji} ${m.moodLabel} (Score ${m.score}/5)", style = MaterialTheme.typography.titleMedium, color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                        Text(m.note, style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                    }
                    IconButton(onClick = { onDeleteMood(m.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = UltronCrimson)
                    }
                }
            }
        }
    }
}

@Composable
fun UltronYaadeinMemoryScreen(
    memories: List<MemoryEntity>,
    onAddMemory: (String, String, String) -> Unit,
    onDeleteMemory: (Long) -> Unit,
    onClearMemories: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Preference") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Yaadein • Long-Term Memory", style = MaterialTheme.typography.headlineMedium, color = UltronTextPrimary)
                    Text("User-approved facts & preferences. Inspect, export or delete anytime.", style = MaterialTheme.typography.bodySmall, color = UltronCyan)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            val exportText = memories.joinToString("\n") { "[${it.category}] ${it.title}: ${it.content}" }
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("ULTRON Yaadein Export", exportText))
                        }
                    ) {
                        Text("Export", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(onClick = onClearMemories) {
                        Text("Reset", style = MaterialTheme.typography.labelSmall, color = UltronCrimson)
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
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Memory Key / Topic (e.g. Favorite Stack)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("yaadein_title_input")
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("What should ULTRON remember?") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("yaadein_content_input")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Preference", "Goal", "Context", "Fact").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onAddMemory(title, content, category)
                                title = ""
                                content = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("yaadein_save_button")
                    ) {
                        Text("Save to Yaadein", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(memories, key = { it.id }) { mem ->
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
                        Text("[${mem.category}] ${mem.title}", style = MaterialTheme.typography.titleMedium, color = UltronCyan, fontWeight = FontWeight.Bold)
                        Text(mem.content, style = MaterialTheme.typography.bodyMedium, color = UltronTextPrimary)
                        Text("Why saved: ${mem.reasonSaved}", style = MaterialTheme.typography.labelSmall, color = UltronTextMuted)
                    }
                    IconButton(onClick = { onDeleteMemory(mem.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Memory", tint = UltronCrimson)
                    }
                }
            }
        }
    }
}
