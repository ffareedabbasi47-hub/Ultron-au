package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UltronSettingsState
import com.example.domain.model.DetectedScreenElement
import com.example.domain.model.DeviceTelemetry
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.LanguageMode
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

@Composable
fun UltronControlCenterScreen(
    telemetry: DeviceTelemetry,
    installedApps: List<InstalledAppInfo>,
    onRefreshTelemetry: () -> Unit,
    onSetMediaVolume: (Int) -> Unit,
    onToggleFlashlight: (Boolean) -> Unit,
    onOpenSettingsAction: (String) -> Unit,
    onLaunchApp: (String) -> Unit
) {
    var appSearchQuery by remember { mutableStateOf("") }
    val filteredApps = remember(installedApps, appSearchQuery) {
        if (appSearchQuery.isBlank()) installedApps.take(20)
        else installedApps.filter { it.appName.contains(appSearchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Phone Control & Real Telemetry",
                        style = MaterialTheme.typography.headlineMedium,
                        color = UltronTextPrimary
                    )
                    Text(
                        text = "Genuine Android APIs • No Fake Toggles",
                        style = MaterialTheme.typography.bodySmall,
                        color = UltronCyan
                    )
                }
                OutlinedButton(
                    onClick = onRefreshTelemetry,
                    modifier = Modifier.testTag("refresh_telemetry_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // 1. Real Telemetry Grid: RAM, CPU, BATTERY, STORAGE, NETWORK
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
                    Text(
                        text = "LIVE HARDWARE TELEMETRY",
                        style = MaterialTheme.typography.labelMedium,
                        color = UltronCyan
                    )

                    // RAM
                    TelemetryRow(
                        label = "RAM",
                        valueText = telemetry.formattedRam,
                        progress = telemetry.ramPercent / 100f,
                        accent = UltronCyan
                    )

                    // CPU & JVM Heap (System-wide /proc/stat CPU % is OS restricted on Android 8+)
                    TelemetryRow(
                        label = "CPU (${telemetry.cpuCores} Cores) • App Heap",
                        valueText = "${telemetry.appHeapUsedMb} MB / ${telemetry.appHeapMaxMb} MB",
                        progress = (telemetry.appHeapUsedMb.toFloat() / telemetry.appHeapMaxMb.coerceAtLeast(1L)).coerceIn(0.05f, 1f),
                        accent = UltronViolet
                    )
                    Text(
                        text = "System-wide CPU %: ${telemetry.systemCpuStatusHinglish}",
                        style = MaterialTheme.typography.labelSmall,
                        color = UltronTextMuted
                    )

                    // BATTERY
                    TelemetryRow(
                        label = "BATTERY",
                        valueText = "${telemetry.batteryPercent}% ${if (telemetry.isCharging) "(Charging)" else ""}",
                        progress = telemetry.batteryPercent / 100f,
                        accent = UltronEmerald
                    )

                    // STORAGE
                    TelemetryRow(
                        label = "STORAGE",
                        valueText = String.format(
                            Locale.US,
                            "%.1f GB free / %.1f GB (%d%% used)",
                            telemetry.storageFreeGb,
                            telemetry.storageTotalGb,
                            telemetry.storagePercent
                        ),
                        progress = telemetry.storagePercent / 100f,
                        accent = UltronGold
                    )

                    // NETWORK
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("NETWORK", style = MaterialTheme.typography.labelMedium, color = UltronTextSecondary)
                        Text(
                            text = telemetry.networkLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (telemetry.isOnline) UltronEmerald else UltronCrimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Real Device Controls (Volume, Flashlight, Wi-Fi, Bluetooth, Brightness, Settings)
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
                    Text(
                        text = "DEVICE CONTROLS & ANDROID PANELS",
                        style = MaterialTheme.typography.labelMedium,
                        color = UltronCyan
                    )

                    // Media Volume Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Media Volume", style = MaterialTheme.typography.bodyMedium, color = UltronTextPrimary)
                        Text("${telemetry.mediaVolumePercent}%", color = UltronCyan, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = telemetry.currentMediaVolume.toFloat(),
                        onValueChange = { onSetMediaVolume(it.toInt()) },
                        valueRange = 0f..telemetry.maxMediaVolume.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(thumbColor = UltronCyan, activeTrackColor = UltronCyan)
                    )

                    // Flashlight Toggle + System Settings Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onToggleFlashlight(!telemetry.flashlightOn) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (telemetry.flashlightOn) UltronGold else UltronSurfaceElevated
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("control_flashlight_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashlightOn,
                                contentDescription = null,
                                tint = if (telemetry.flashlightOn) UltronObsidian else UltronCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (telemetry.flashlightOn) "Torch: ON" else "Torch: OFF",
                                color = if (telemetry.flashlightOn) UltronObsidian else UltronTextPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    Settings.Panel.ACTION_WIFI
                                } else {
                                    Settings.ACTION_WIFI_SETTINGS
                                }
                                onOpenSettingsAction(action)
                            },
                            border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("control_wifi_button")
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = UltronCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wi-Fi Panel", color = UltronTextPrimary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onOpenSettingsAction(Settings.ACTION_BLUETOOTH_SETTINGS) },
                            border = BorderStroke(1.dp, UltronViolet.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = UltronViolet)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bluetooth", color = UltronTextPrimary)
                        }

                        OutlinedButton(
                            onClick = { onOpenSettingsAction(Settings.ACTION_DISPLAY_SETTINGS) },
                            border = BorderStroke(1.dp, UltronGold.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Brightness6, contentDescription = null, tint = UltronGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Brightness", color = UltronTextPrimary)
                        }

                        OutlinedButton(
                            onClick = { onOpenSettingsAction(Settings.ACTION_SETTINGS) },
                            border = BorderStroke(1.dp, UltronEmerald.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = UltronEmerald)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Settings", color = UltronTextPrimary)
                        }
                    }
                }
            }
        }

        // 3. App Launcher Section ("Apps kholna")
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
                        text = "APPS KHOLNA (INSTALLED APPS & INTENTS)",
                        style = MaterialTheme.typography.labelMedium,
                        color = UltronCyan
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("YouTube", "WhatsApp", "Chrome", "Settings").forEach { quickApp ->
                            OutlinedButton(
                                onClick = { onLaunchApp(quickApp) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(quickApp, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = appSearchQuery,
                        onValueChange = { appSearchQuery = it },
                        placeholder = { Text("Search installed apps...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        items(filteredApps, key = { it.packageName }) { appInfo ->
            Surface(
                color = UltronSurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, UltronGlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLaunchApp(appInfo.appName) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = appInfo.appName,
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronTextPrimary
                        )
                        Text(
                            text = appInfo.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = UltronTextMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Launch,
                        contentDescription = "Launch ${appInfo.appName}",
                        tint = UltronCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    valueText: String,
    progress: Float,
    accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = UltronTextSecondary)
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium,
                color = UltronTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0.02f, 1f) },
            color = accent,
            trackColor = UltronObsidian,
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(50))
        )
    }
}

/**
 * MAX / ULTRON VISION CENTER ("Screen Samjho"):
 * Controls: START VISION | EXPLAIN SCREEN | FIND BUTTON | HIGHLIGHT | READ SCREEN | STOP
 * Shows animated bounding highlights around detected UI elements ("Boss, yahan tap karna hai.")
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UltronVisionCenterScreen(
    isVisionActive: Boolean,
    visionExplanation: String,
    detectedElements: List<DetectedScreenElement>,
    showHighlights: Boolean,
    onStartVisionWithBitmap: (Bitmap?, String) -> Unit,
    onToggleHighlights: (Boolean) -> Unit,
    onReadScreenExplanation: (String) -> Unit,
    onStopVision: () -> Unit
) {
    val context = LocalContext.current
    var selectedElement by remember { mutableStateOf<DetectedScreenElement?>(null) }

    // MediaProjection system consent dialog launcher
    val mediaProjectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onStartVisionWithBitmap(null, "MediaProjection consent granted. Explain visible layout and buttons.")
        } else {
            onStartVisionWithBitmap(null, "Analyze current ULTRON interface layout.")
        }
    }

    // Zero-permission Android Photo Picker for analyzing any screenshot
    val screenshotPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bmp = loadBitmapFromUri(context, uri)
            onStartVisionWithBitmap(bmp, "Explain this screenshot in Hinglish and find key buttons.")
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "vision_highlight_pulse")
    val highlightAlpha by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "highlight_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ULTRON VISION (Screen Samjho)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = UltronTextPrimary
                )
                Text(
                    text = if (isVisionActive) "ACTIVE SESSION • Consent Verified" else "IDLE • Explicit User Consent Required",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isVisionActive) UltronEmerald else UltronCyan
                )
            }
        }

        // Vision Action Controls Grid:
        // START VISION | EXPLAIN SCREEN | FIND BUTTON | HIGHLIGHT | READ SCREEN | STOP
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                val mpm = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                                mediaProjectionLauncher.launch(mpm.createScreenCaptureIntent())
                            } catch (_: Exception) {
                                onStartVisionWithBitmap(null, "Start Vision")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vision_start_button")
                    ) {
                        Text("START VISION", color = UltronObsidian, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onStartVisionWithBitmap(null, "Meri screen par kya hai? Explain visible UI.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronViolet),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vision_explain_button")
                    ) {
                        Text("EXPLAIN SCREEN", color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onToggleHighlights(true)
                            selectedElement = detectedElements.firstOrNull()
                        },
                        border = BorderStroke(1.dp, UltronCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vision_find_button")
                    ) {
                        Text("FIND BUTTON", color = UltronCyan)
                    }

                    OutlinedButton(
                        onClick = { onToggleHighlights(!showHighlights) },
                        border = BorderStroke(1.dp, UltronGold),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vision_highlight_button")
                    ) {
                        Text(
                            text = if (showHighlights) "HIGHLIGHT: ON" else "HIGHLIGHT: OFF",
                            color = UltronGold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onReadScreenExplanation(visionExplanation) },
                        border = BorderStroke(1.dp, UltronEmerald),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vision_read_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = UltronEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("READ SCREEN", color = UltronEmerald)
                    }

                    Button(
                        onClick = onStopVision,
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCrimson),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vision_stop_button")
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, tint = UltronTextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("STOP", color = UltronTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        screenshotPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    border = BorderStroke(1.dp, UltronGlassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ImageSearch, contentDescription = null, tint = UltronCyan)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick Screenshot from Gallery for Gemini Vision Analysis", color = UltronTextPrimary)
                }
            }
        }

        // Interactive Highlight Overlay Viewport
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronObsidian),
            border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "DETECTED UI ELEMENT HIGHLIGHT MAP",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(UltronSurfaceElevated)
                        .border(1.dp, UltronGlassBorder, RoundedCornerShape(12.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (showHighlights) {
                            detectedElements.forEach { el ->
                                val isChosen = selectedElement?.id == el.id
                                val boxColor = if (isChosen) UltronCrimson else UltronCyan
                                val left = el.normalizedX * size.width
                                val top = el.normalizedY * size.height
                                val w = el.normalizedW * size.width
                                val h = el.normalizedH * size.height

                                drawRoundRect(
                                    color = boxColor.copy(alpha = 0.14f * highlightAlpha),
                                    topLeft = Offset(left, top),
                                    size = Size(w, h),
                                    cornerRadius = CornerRadius(16f, 16f)
                                )
                                drawRoundRect(
                                    color = boxColor.copy(alpha = highlightAlpha),
                                    topLeft = Offset(left, top),
                                    size = Size(w, h),
                                    cornerRadius = CornerRadius(16f, 16f),
                                    style = Stroke(width = 2.5.dp.toPx())
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        detectedElements.forEach { el ->
                            Surface(
                                color = UltronObsidian.copy(alpha = 0.86f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.7f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedElement = el }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = el.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = UltronCyan
                                    )
                                    Text(
                                        text = "Boss, yahan tap karna hai →",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = UltronGold
                                    )
                                }
                            }
                        }
                    }
                }

                selectedElement?.let { el ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Highlight Detail (${el.label}): ${el.explanationHinglish}",
                        style = MaterialTheme.typography.bodySmall,
                        color = UltronGold
                    )
                }
            }
        }

        // Vision Explanation Card
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurface),
            border = BorderStroke(1.dp, UltronGlassBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SCREEN SAMJHO EXPLANATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = visionExplanation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = UltronTextPrimary
                )
            }
        }
    }
}

/**
 * Scan / Camera OCR & QR Inspection Screen
 */
@Composable
fun UltronScanScreen(
    hasCameraPermission: Boolean,
    visionExplanation: String,
    onRequestCameraPermission: () -> Unit,
    onAnalyzeCapturedBitmap: (Bitmap, String) -> Unit
) {
    val context = LocalContext.current
    var customScanQuestion by remember {
        mutableStateOf("Is photo/document mein jo text, QR ya object hai use Hinglish mein explain karo.")
    }

    val takePicturePreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onAnalyzeCapturedBitmap(bitmap, customScanQuestion)
        }
    }

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            loadBitmapFromUri(context, uri)?.let { bmp ->
                onAnalyzeCapturedBitmap(bmp, customScanQuestion)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Scan, OCR & Visual Intelligence",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary
        )
        Text(
            text = "Camera permission is requested only when you tap Capture.",
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
                    value = customScanQuestion,
                    onValueChange = { customScanQuestion = it },
                    label = { Text("What should ULTRON extract or scan?") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                takePicturePreviewLauncher.launch(null)
                            } else {
                                onRequestCameraPermission()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UltronCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scan_camera_capture_button")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = UltronObsidian)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasCameraPermission) "Camera Scan" else "Grant Camera",
                            color = UltronObsidian,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            pickPhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        border = BorderStroke(1.dp, UltronViolet),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scan_pick_image_button")
                    ) {
                        Icon(Icons.Default.ImageSearch, contentDescription = null, tint = UltronViolet)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Image / Doc", color = UltronTextPrimary)
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
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "OCR / SCAN RESULT",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = visionExplanation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = UltronTextPrimary
                )
            }
        }
    }
}

/**
 * Settings, Persona, Permissions Dashboard & Honest Diagnostics Screen
 */
@Composable
fun UltronSettingsAndDiagnosticsScreen(
    settings: UltronSettingsState,
    hasMicPermission: Boolean,
    hasCameraPermission: Boolean,
    isApiKeyConfigured: Boolean,
    telemetry: DeviceTelemetry,
    onUpdateUserAddress: (String) -> Unit,
    onUpdateLanguage: (LanguageMode) -> Unit,
    onUpdateReducedMotion: (Boolean) -> Unit,
    onUpdateAutoSpeak: (Boolean) -> Unit,
    onRequestMicPermission: () -> Unit,
    onRequestCameraPermission: () -> Unit,
    onOpenSystemAppSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Settings, Persona & Diagnostics",
            style = MaterialTheme.typography.headlineMedium,
            color = UltronTextPrimary
        )

        // 1. Protected Creator Attribution Card
        Card(
            colors = CardDefaults.cardColors(containerColor = UltronSurfaceElevated),
            border = BorderStroke(1.dp, UltronCyan.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "PROTECTED CREATOR RECORD",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )
                Text(
                    text = "Configured Creator: ${settings.configuredCreator}",
                    style = MaterialTheme.typography.titleLarge,
                    color = UltronTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "\"Mujhe Fardeen ne banaya hai.\" (Mere configured creator record ke mutabik mujhe Fardeen ne develop kiya hai. Random user prompts cannot overwrite this record.)",
                    style = MaterialTheme.typography.bodySmall,
                    color = UltronTextSecondary
                )
            }
        }

        // 2. Persona & Language Settings
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
                    text = "PERSONA, ADDRESS & MOTION",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )

                Text("How should ULTRON address you?", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Boss", "Fardeen", "Commander").forEach { addr ->
                        FilterChip(
                            selected = settings.userAddress == addr,
                            onClick = { onUpdateUserAddress(addr) },
                            label = { Text(addr) }
                        )
                    }
                }

                Text("Primary UI & Reply Language:", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageMode.entries.forEach { mode ->
                        FilterChip(
                            selected = settings.languageMode == mode,
                            onClick = { onUpdateLanguage(mode) },
                            label = { Text(mode.displayName) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Speak Assistant Replies (TTS)", color = UltronTextPrimary)
                        Text("Automatically speak responses using Awaaz Studio profile", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                    }
                    Switch(
                        checked = settings.autoSpeakReplies,
                        onCheckedChange = onUpdateAutoSpeak
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reduced Motion / Low-GPU Mode", color = UltronTextPrimary)
                        Text("Simplifies core ring animations for battery efficiency", style = MaterialTheme.typography.bodySmall, color = UltronTextSecondary)
                    }
                    Switch(
                        checked = settings.reducedMotion,
                        onCheckedChange = onUpdateReducedMotion
                    )
                }
            }
        }

        // 3. Permissions & Platform Boundary Dashboard
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
                    text = "PERMISSIONS & PLATFORM BOUNDARY MAP",
                    style = MaterialTheme.typography.labelMedium,
                    color = UltronCyan
                )

                PermissionStatusItem(
                    feature = "Voice Input & Wake Word (RECORD_AUDIO)",
                    granted = hasMicPermission,
                    onAction = onRequestMicPermission
                )
                PermissionStatusItem(
                    feature = "Scan / OCR Camera (CAMERA)",
                    granted = hasCameraPermission,
                    onAction = onRequestCameraPermission
                )
                PermissionStatusItem(
                    feature = "Gemini Cloud AI Key (BuildConfig.GEMINI_API_KEY)",
                    granted = isApiKeyConfigured,
                    actionLabel = "Secrets Panel",
                    onAction = {}
                )

                OutlinedButton(
                    onClick = onOpenSystemAppSettings,
                    border = BorderStroke(1.dp, UltronCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_system_permission_settings_button")
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = UltronCyan)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OPEN ANDROID APP PERMISSION SETTINGS", color = UltronCyan)
                }
            }
        }
    }
}

@Composable
private fun PermissionStatusItem(
    feature: String,
    granted: Boolean,
    actionLabel: String = "Grant",
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(feature, style = MaterialTheme.typography.bodyMedium, color = UltronTextPrimary)
            Text(
                text = if (granted) "Status: ACTIVE / GRANTED" else "Status: PERMISSION / SETUP REQUIRED",
                style = MaterialTheme.typography.labelSmall,
                color = if (granted) UltronEmerald else UltronCrimson
            )
        }
        if (!granted) {
            OutlinedButton(onClick = onAction) {
                Text(actionLabel, style = MaterialTheme.typography.labelSmall)
            }
        } else {
            Icon(Icons.Default.CheckCircle, contentDescription = "Granted", tint = UltronEmerald)
        }
    }
}

private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (_: Exception) {
        null
    }
}
