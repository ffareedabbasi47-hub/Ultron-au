package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Widgets
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AssistantCoreState
import com.example.domain.model.AssistantPersona
import com.example.domain.model.DeviceTelemetry
import com.example.domain.model.UltronDestination
import com.example.ui.theme.UltronCrimson
import com.example.ui.theme.UltronCyan
import com.example.ui.theme.UltronEmerald
import com.example.ui.theme.UltronGlassBorder
import com.example.ui.theme.UltronGold
import com.example.ui.theme.UltronGraphite
import com.example.ui.theme.UltronObsidian
import com.example.ui.theme.UltronSurface
import com.example.ui.theme.UltronSurfaceElevated
import com.example.ui.theme.UltronTextMuted
import com.example.ui.theme.UltronTextPrimary
import com.example.ui.theme.UltronTextSecondary
import com.example.ui.theme.UltronViolet
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Reference-faithful Top Bar:
 * Left: Hamburger Menu + Online/Battery mini pill
 * Center: Active Assistant Title ("Ultron" or "Max")
 * Right: Notification Bell (Aaj/Reminders) + Avatar/Persona Switcher icon
 */
@Composable
fun UltronCompactTopBar(
    telemetry: DeviceTelemetry,
    currentDestination: UltronDestination,
    activePersona: AssistantPersona = AssistantPersona.MAX,
    onOpenDrawer: () -> Unit,
    onTitleClick: () -> Unit,
    onBellClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    val currentTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    Surface(
        color = UltronObsidian.copy(alpha = 0.95f),
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Hamburger + "MAX" Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("hamburger_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Menu",
                        tint = UltronTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "MAX",
                    style = MaterialTheme.typography.titleLarge,
                    color = UltronTextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .clickable { onTitleClick() }
                        .testTag("top_bar_title")
                )
            }

            // Right: ONLINE/OFFLINE | Wi-Fi/Network | Battery | Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Online/Offline Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (telemetry.isOnline) UltronEmerald else UltronCrimson)
                    )
                    Text(
                        text = if (telemetry.isOnline) "ONLINE" else "OFFLINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (telemetry.isOnline) UltronEmerald else UltronCrimson,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Wi-Fi / Network
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = if (telemetry.isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = telemetry.networkLabel,
                        tint = UltronTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Battery
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (telemetry.isCharging) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = "Charging",
                            tint = UltronGold,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = "${telemetry.batteryPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = UltronTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                // Time
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = UltronTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * Reference-Faithful Multi-Ring Concentric Arc Reactor Core:
 * Displays outer glowing rings, counter-rotating segmented arcs, inner equalizer waveform bars,
 * spaced core title ("U.L.T.R.O.N" or "M.A.X"), "AI ASSISTANT" subtitle, and live state badge.
 */
/**
 * Compact, High-Tech Animated MAX Core:
 * Communicates actual system states:
 * - IDLE: "MAX READY" with subtle breathing animation
 * - LISTENING: "LISTENING" with real microphone waveform and animated pulsing ring
 * - THINKING: "THINKING..." with intelligence processing orbital arcs
 * - EXECUTING: "EXECUTING" with forward task execution sweep
 * - SPEAKING: "SPEAKING" with speech amplitude pulse
 * - ERROR: Clear alert state
 */
@Composable
fun UltronAnimatedCore(
    coreState: AssistantCoreState,
    micAmplitude: Float,
    avatarId: String,
    reducedMotion: Boolean,
    activePersona: AssistantPersona = AssistantPersona.MAX,
    coreSize: Dp = 124.dp,
    onCoreTap: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ultron_core_transition")

    val rotationSlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (coreState) {
                    AssistantCoreState.THINKING, AssistantCoreState.EXECUTING -> 2200
                    AssistantCoreState.LISTENING, AssistantCoreState.SPEAKING -> 3600
                    else -> 9000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_slow"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = if (reducedMotion) 0.98f else 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (coreState) {
                    AssistantCoreState.SPEAKING -> 380
                    AssistantCoreState.LISTENING -> 460
                    AssistantCoreState.THINKING, AssistantCoreState.EXECUTING -> 620
                    else -> 2600 // Subtle breathing in IDLE
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val primaryRingColor = when (coreState) {
        AssistantCoreState.IDLE -> UltronEmerald
        AssistantCoreState.WAKE_DETECTED, AssistantCoreState.LISTENING -> UltronCyan
        AssistantCoreState.TRANSCRIBING, AssistantCoreState.THINKING -> UltronViolet
        AssistantCoreState.EXECUTING -> UltronGold
        AssistantCoreState.SPEAKING -> UltronEmerald
        AssistantCoreState.PERMISSION_BLOCKED, AssistantCoreState.ERROR, AssistantCoreState.OFFLINE -> UltronCrimson
        AssistantCoreState.PAUSED, AssistantCoreState.SLEEP_ARMED -> UltronTextMuted
    }

    val secondaryArcColor = when (coreState) {
        AssistantCoreState.THINKING -> UltronCyan
        AssistantCoreState.EXECUTING -> UltronEmerald
        AssistantCoreState.ERROR, AssistantCoreState.PERMISSION_BLOCKED -> UltronGold
        else -> UltronCyan
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onCoreTap() }
            .testTag("central_ultron_core")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(coreSize)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxR = size.minDimension / 2f
                val activeBoost = when (coreState) {
                    AssistantCoreState.LISTENING -> (micAmplitude * 0.18f).coerceIn(0f, 0.20f)
                    AssistantCoreState.SPEAKING -> 0.10f
                    else -> 0f
                }

                // 1. Soft Ambient Outer Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryRingColor.copy(alpha = if (coreState == AssistantCoreState.IDLE) 0.14f else 0.25f),
                            secondaryArcColor.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = maxR * (pulseScale + activeBoost)
                    ),
                    radius = maxR
                )

                // 2. Outer Thin Orbit Ring with Nodes
                drawCircle(
                    color = primaryRingColor.copy(alpha = 0.28f),
                    radius = maxR * 0.92f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                // Orbital Node points
                rotate(degrees = rotationSlow * 0.7f, pivot = center) {
                    val nodeAngles = listOf(40f, 160f, 280f)
                    nodeAngles.forEachIndexed { idx, deg ->
                        val rad = Math.toRadians(deg.toDouble())
                        val nx = center.x + (maxR * 0.92f * cos(rad)).toFloat()
                        val ny = center.y + (maxR * 0.92f * sin(rad)).toFloat()
                        drawCircle(
                            color = if (idx % 2 == 0) primaryRingColor else secondaryArcColor,
                            radius = 2.8.dp.toPx(),
                            center = Offset(nx, ny)
                        )
                    }
                }

                // 3. Sweeping Arcs
                rotate(degrees = rotationSlow, pivot = center) {
                    for (i in 0 until 2) {
                        drawArc(
                            color = primaryRingColor.copy(alpha = 0.85f),
                            startAngle = i * 180f + 15f,
                            sweepAngle = if (coreState == AssistantCoreState.EXECUTING) 130f else 100f,
                            useCenter = false,
                            topLeft = Offset(center.x - maxR * 0.82f, center.y - maxR * 0.82f),
                            size = Size(maxR * 1.64f, maxR * 1.64f),
                            style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // 4. Counter-Rotating Arcs
                rotate(degrees = -rotationSlow * 1.2f, pivot = center) {
                    for (i in 0 until 2) {
                        drawArc(
                            color = secondaryArcColor.copy(alpha = 0.75f),
                            startAngle = i * 180f + 70f,
                            sweepAngle = 85f,
                            useCenter = false,
                            topLeft = Offset(center.x - maxR * 0.74f, center.y - maxR * 0.74f),
                            size = Size(maxR * 1.48f, maxR * 1.48f),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // 5. Inner Dark Sphere Chassis
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            UltronSurfaceElevated,
                            UltronObsidian
                        ),
                        center = center,
                        radius = maxR * 0.64f
                    ),
                    radius = maxR * 0.64f,
                    center = center
                )
                drawCircle(
                    color = primaryRingColor.copy(alpha = 0.50f),
                    radius = maxR * 0.64f,
                    center = center,
                    style = Stroke(width = 1.4.dp.toPx())
                )

                // 6. Microphone Waveform bars when LISTENING or SPEAKING
                if (coreState == AssistantCoreState.LISTENING || coreState == AssistantCoreState.SPEAKING) {
                    val barCount = 7
                    val totalBarsWidth = maxR * 0.50f
                    val spacing = totalBarsWidth / (barCount - 1)
                    val startX = center.x - totalBarsWidth / 2f
                    val barCenterY = center.y - maxR * 0.20f

                    for (b in 0 until barCount) {
                        val distFromCenter = 1f - (abs(b - barCount / 2f) / (barCount / 2f))
                        val animatedWave = if (coreState == AssistantCoreState.LISTENING) {
                            (0.20f + micAmplitude * 0.80f * abs(sin(rotationSlow * 0.1f + b))).coerceIn(0.18f, 1f)
                        } else {
                            (0.25f + 0.60f * abs(cos(rotationSlow * 0.12f + b * 0.8f))).coerceIn(0.20f, 0.95f)
                        }
                        val barHeight = maxR * 0.24f * animatedWave
                        val barColor = if (b % 2 == 0) primaryRingColor else secondaryArcColor
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(startX + b * spacing - 1.5.dp.toPx(), barCenterY - barHeight / 2f),
                            size = Size(3.dp.toPx(), barHeight),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                    }
                }
            }

            // Central State Display Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(coreSize * 0.70f)
                    .clip(CircleShape)
            ) {
                when (coreState) {
                    AssistantCoreState.IDLE -> {
                        Text(
                            text = "MAX",
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronTextPrimary,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "READY",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryRingColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 9.5.sp
                        )
                    }
                    AssistantCoreState.WAKE_DETECTED -> {
                        Text(
                            text = "MAX",
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronTextPrimary,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "WAKE",
                            style = MaterialTheme.typography.labelSmall,
                            color = UltronCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 9.5.sp
                        )
                    }
                    AssistantCoreState.LISTENING -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "LISTENING",
                            style = MaterialTheme.typography.labelMedium,
                            color = UltronCyan,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 10.5.sp
                        )
                    }
                    AssistantCoreState.THINKING, AssistantCoreState.TRANSCRIBING -> {
                        Text(
                            text = "THINKING...",
                            style = MaterialTheme.typography.labelMedium,
                            color = UltronViolet,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 10.5.sp
                        )
                    }
                    AssistantCoreState.EXECUTING -> {
                        Text(
                            text = "EXECUTING",
                            style = MaterialTheme.typography.labelMedium,
                            color = UltronGold,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 10.5.sp
                        )
                    }
                    AssistantCoreState.SPEAKING -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "SPEAKING",
                            style = MaterialTheme.typography.labelMedium,
                            color = UltronEmerald,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 10.5.sp
                        )
                    }
                    AssistantCoreState.ERROR, AssistantCoreState.PERMISSION_BLOCKED -> {
                        Text(
                            text = "ERROR",
                            style = MaterialTheme.typography.labelMedium,
                            color = UltronCrimson,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (coreState == AssistantCoreState.PERMISSION_BLOCKED) "MIC BLOCKED" else "TAP TO FIX",
                            style = MaterialTheme.typography.labelSmall,
                            color = UltronCrimson,
                            fontSize = 8.5.sp
                        )
                    }
                    else -> {
                        Text(
                            text = "MAX",
                            style = MaterialTheme.typography.titleMedium,
                            color = UltronTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = coreState.badgeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryRingColor,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // State Badge Pill below Core
        Surface(
            color = primaryRingColor.copy(alpha = 0.12f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, primaryRingColor.copy(alpha = 0.55f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.5.dp)
                        .clip(CircleShape)
                        .background(primaryRingColor)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = when (coreState) {
                        AssistantCoreState.IDLE -> "MAX • READY"
                        AssistantCoreState.WAKE_DETECTED -> "MAX • WAKE"
                        AssistantCoreState.LISTENING -> "MAX • LISTENING"
                        AssistantCoreState.THINKING, AssistantCoreState.TRANSCRIBING -> "MAX • THINKING..."
                        AssistantCoreState.EXECUTING -> "MAX • EXECUTING"
                        AssistantCoreState.SPEAKING -> "MAX • SPEAKING"
                        AssistantCoreState.ERROR, AssistantCoreState.PERMISSION_BLOCKED -> "MAX • ERROR"
                        else -> "${activePersona.displayName.uppercase(Locale.ROOT)} • ${coreState.badgeLabel}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryRingColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * Reference-Faithful Glassmorphic Chat Bar:
 * [ Paperclip Attachment ] [ Mic ] "Kuch bhi puchho..." [ STOP (when active) ] [ Glowing Send Arrow ]
 */
@Composable
fun UltronCommandInputBar(
    composerText: String,
    coreState: AssistantCoreState,
    isSpeaking: Boolean,
    isListening: Boolean,
    activePersona: AssistantPersona = AssistantPersona.ULTRON,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    onStopClick: () -> Unit,
    onAttachClick: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val showStopButton = isSpeaking || isListening ||
        coreState == AssistantCoreState.LISTENING ||
        coreState == AssistantCoreState.THINKING ||
        coreState == AssistantCoreState.EXECUTING ||
        coreState == AssistantCoreState.SPEAKING

    val accentColor = if (activePersona == AssistantPersona.MAX) UltronEmerald else UltronCyan

    Surface(
        color = UltronGraphite.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.45f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Paperclip Attachment Button (Reference Left Icon)
                IconButton(
                    onClick = onAttachClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(UltronSurfaceElevated)
                        .testTag("composer_attach_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach Document or Scan",
                        tint = UltronTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Mic Push-to-Talk Button
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening || coreState == AssistantCoreState.LISTENING) {
                                accentColor
                            } else {
                                UltronSurfaceElevated
                            }
                        )
                        .border(
                            1.dp,
                            if (isListening) UltronTextPrimary else accentColor.copy(alpha = 0.55f),
                            CircleShape
                        )
                        .testTag("composer_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Push to Talk Microphone",
                        tint = if (isListening || coreState == AssistantCoreState.LISTENING) {
                            UltronObsidian
                        } else {
                            accentColor
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Glassmorphic Input Field ("Kuch bhi puchho...")
                OutlinedTextField(
                    value = composerText,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            text = if (isListening) {
                                "Listening to Boss..."
                            } else {
                                "Ask MAX anything..."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = UltronTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = UltronTextPrimary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (composerText.isNotBlank()) {
                                onSendClick()
                                keyboardController?.hide()
                            }
                        }
                    ),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = UltronGlassBorder,
                        focusedContainerColor = UltronSurface,
                        unfocusedContainerColor = UltronSurface,
                        cursorColor = accentColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("command_text_input")
                )

                // Visible STOP Interrupt button whenever active
                AnimatedVisibility(visible = showStopButton) {
                    Surface(
                        onClick = onStopClick,
                        color = UltronCrimson,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("stop_interrupt_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Assistant",
                                tint = UltronTextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "STOP",
                                style = MaterialTheme.typography.labelSmall,
                                color = UltronTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Glowing Send Button
                IconButton(
                    onClick = {
                        if (composerText.isNotBlank()) {
                            onSendClick()
                            keyboardController?.hide()
                        }
                    },
                    enabled = composerText.isNotBlank(),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (composerText.isNotBlank()) accentColor else UltronSurfaceElevated
                        )
                        .testTag("composer_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (composerText.isNotBlank()) UltronObsidian else UltronTextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

/**
 * 4 Primary Navigation Tabs:
 * HOME (MAX Core + Conversation)
 * CONTROL (Phone/Device Controls & Telemetry)
 * VISION (Screen Understanding & Highlighting)
 * TOOLS (Files, Web, Communication, Notes, Study)
 */
@Composable
fun UltronBottomNavigation(
    currentDestination: UltronDestination,
    isListening: Boolean,
    activePersona: AssistantPersona = AssistantPersona.MAX,
    onNavigate: (UltronDestination) -> Unit,
    onCenterLiveMicClick: () -> Unit = {}
) {
    val accentColor = if (activePersona == AssistantPersona.MAX) UltronEmerald else UltronCyan

    Surface(
        color = UltronObsidian,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column {
            HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.5f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "HOME",
                    icon = Icons.Default.Home,
                    selected = currentDestination == UltronDestination.GHAR,
                    accentColor = accentColor,
                    tag = "nav_tab_home",
                    onClick = { onNavigate(UltronDestination.GHAR) }
                )

                BottomNavItem(
                    label = "CONTROL",
                    icon = Icons.Default.PhoneAndroid,
                    selected = currentDestination == UltronDestination.CONTROL_CENTER,
                    accentColor = accentColor,
                    tag = "nav_tab_control",
                    onClick = { onNavigate(UltronDestination.CONTROL_CENTER) }
                )

                BottomNavItem(
                    label = "VISION",
                    icon = Icons.Default.Visibility,
                    selected = currentDestination == UltronDestination.VISION_CENTER,
                    accentColor = accentColor,
                    tag = "nav_tab_vision",
                    onClick = { onNavigate(UltronDestination.VISION_CENTER) }
                )

                BottomNavItem(
                    label = "TOOLS",
                    icon = Icons.Default.Widgets,
                    selected = currentDestination == UltronDestination.TOOLS,
                    accentColor = accentColor,
                    tag = "nav_tab_tools",
                    onClick = { onNavigate(UltronDestination.TOOLS) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) accentColor else UltronTextSecondary,
            modifier = Modifier.size(21.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) accentColor else UltronTextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

/**
 * Categorized Hamburger Navigation Drawer matching the Reference Video:
 * - Assistant Profile Card with Dual Persona Toggle ("Ultron" ⇄ "Max")
 * - HOME
 * - KAAM-KAAJ (Markets, Marketing, Documents, Website/Coding, Accessibility)
 * - ROZ KE KAAM (Gaane, Padhai, Journal, Dream Journal, Mausam, Aaj, Mood, Scanner)
 * - SYSTEM (Settings, Ultron & Max ke rules, PC ⇄ Phone, Phone Control, Vision, Sub-agents, Voice Engine, Avatar)
 * - AUR (Privacy Policy, About)
 */
@Composable
fun UltronHamburgerDrawer(
    currentDestination: UltronDestination,
    configuredCreator: String,
    activePersona: AssistantPersona = AssistantPersona.ULTRON,
    onSelectDestination: (UltronDestination) -> Unit,
    onTogglePersona: () -> Unit = {},
    onCloseDrawer: () -> Unit
) {
    val drawerDestinations = remember {
        UltronDestination.entries.filter { it.section != "SETTINGS" }.groupBy { it.section }
    }
    val accentColor = if (activePersona == AssistantPersona.MAX) UltronEmerald else UltronCyan

    ModalDrawerSheet(
        drawerContainerColor = UltronGraphite,
        drawerContentColor = UltronTextPrimary,
        modifier = Modifier.width(305.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Drawer Header Profile Card (Reference Video top card with Assistant Name + Dual Persona Switch)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(UltronSurfaceElevated, UltronObsidian)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.18f))
                                    .border(1.dp, accentColor, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = activePersona.displayName,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = UltronTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tumhari AI dost • Creator: $configuredCreator",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = accentColor
                                )
                            }
                        }
                        IconButton(onClick = onCloseDrawer) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Menu",
                                tint = UltronTextSecondary
                            )
                        }
                    }

                    // Instant Dual-Persona Switch Pill ("Max ko bhej" / "Ultron ko bhej")
                    Surface(
                        color = accentColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePersona() }
                            .testTag("drawer_persona_switch_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (activePersona == AssistantPersona.ULTRON) {
                                    "Switch to MAX Mode (\"Max ko bhej\")"
                                } else {
                                    "Switch to ULTRON Mode (\"Ultron ko bhej\")"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Persona",
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = UltronGlassBorder)

            drawerDestinations.forEach { (sectionTitle, destinations) ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = sectionTitle.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
                destinations.forEach { dest ->
                    val selected = dest == currentDestination
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = dest.titleHinglish,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = {
                            onSelectDestination(dest)
                            onCloseDrawer()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = accentColor.copy(alpha = 0.16f),
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = accentColor,
                            unselectedTextColor = UltronTextPrimary
                        ),
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 1.dp)
                            .testTag("drawer_item_${dest.route}")
                    )
                }
            }
        }
    }
}
