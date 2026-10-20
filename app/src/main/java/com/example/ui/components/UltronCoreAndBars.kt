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
import androidx.compose.foundation.layout.offset
import com.example.ui.theme.UltronCardBorderGlow
import com.example.ui.theme.UltronCrimson
import com.example.ui.theme.UltronCyan
import com.example.ui.theme.UltronCyanBright
import com.example.ui.theme.UltronCyanDim
import com.example.ui.theme.UltronDeepDark
import com.example.ui.theme.UltronDeepPurple
import com.example.ui.theme.UltronEmerald
import com.example.ui.theme.UltronEnergyBg
import com.example.ui.theme.UltronGlassBorder
import com.example.ui.theme.UltronGold
import com.example.ui.theme.UltronGraphite
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
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Reference-faithful Top Bar:
 * Left: Hamburger Menu (Drawer)
 * Center: Active Assistant Title ("ULTRON" / "MAYA" / "MAX") or Screen Name
 * Right: Notification Bell (Aaj/Reminders) + AI Persona Avatar Badge (with cyan/purple glowing rim)
 */
@Composable
fun UltronCompactTopBar(
    telemetry: DeviceTelemetry,
    currentDestination: UltronDestination,
    activePersona: AssistantPersona = AssistantPersona.ULTRON,
    onOpenDrawer: () -> Unit,
    onTitleClick: () -> Unit,
    onBellClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    val titleText = if (currentDestination == UltronDestination.GHAR) {
        activePersona.displayName
    } else {
        currentDestination.titleHinglish
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
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Hamburger Menu Button
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .testTag("hamburger_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Menu",
                    tint = UltronTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Center: Screen / Assistant Title
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
                color = UltronTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 0.8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onTitleClick() }
                    .testTag("top_bar_title")
            )

            // Right: Bell Notification + Persona Avatar Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBellClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("top_bar_bell")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Reminders & Alerts",
                        tint = UltronTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // AI Avatar Thumbnail Badge with Glowing Border
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(UltronCyan.copy(alpha = 0.35f), UltronViolet.copy(alpha = 0.35f))
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(UltronCyan, UltronViolet, UltronMagenta, UltronCyan)
                            ),
                            shape = CircleShape
                        )
                        .clickable { onAvatarClick() }
                        .testTag("top_bar_avatar")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Switch Persona / Avatar Studio",
                        tint = UltronCyanBright,
                        modifier = Modifier.size(18.dp)
                    )
                }
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
 * Reference-Faithful Multi-Ring Concentric Arc Reactor Core:
 * - Glowing white/silver arc on upper-right
 * - Glowing cyan arc on left
 * - Glowing magenta/purple arc on top
 * - Background technical dashed circle & crosshairs
 * - Spaced gradient title ("U . L . T . R . O . N" or "M . A . Y . A" or "M . A . X")
 * - Dynamic equalizer waveform dots/bars
 * - Status hint ("BOLO, KAISE HELP KARUN?" / "SUN RAHI HAI...")
 */
@Composable
fun UltronAnimatedCore(
    coreState: AssistantCoreState,
    micAmplitude: Float,
    avatarId: String,
    reducedMotion: Boolean,
    activePersona: AssistantPersona = AssistantPersona.ULTRON,
    coreSize: Dp = 205.dp,
    onCoreTap: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ultron_core_transition")

    val rotationSlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (coreState) {
                    AssistantCoreState.THINKING, AssistantCoreState.EXECUTING -> 3200
                    AssistantCoreState.LISTENING, AssistantCoreState.SPEAKING -> 4500
                    else -> 12000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_slow"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = if (reducedMotion) 0.99f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (coreState) {
                    AssistantCoreState.SPEAKING -> 420
                    AssistantCoreState.LISTENING -> 500
                    AssistantCoreState.THINKING, AssistantCoreState.EXECUTING -> 700
                    else -> 2800 // Gentle breathing in IDLE
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val primaryAccent = when (coreState) {
        AssistantCoreState.IDLE -> UltronViolet
        AssistantCoreState.WAKE_DETECTED, AssistantCoreState.LISTENING -> UltronCyanBright
        AssistantCoreState.TRANSCRIBING, AssistantCoreState.THINKING -> UltronMagenta
        AssistantCoreState.EXECUTING -> UltronGold
        AssistantCoreState.SPEAKING -> UltronEmerald
        AssistantCoreState.PERMISSION_BLOCKED, AssistantCoreState.ERROR, AssistantCoreState.OFFLINE -> UltronCrimson
        AssistantCoreState.PAUSED, AssistantCoreState.SLEEP_ARMED -> UltronTextMuted
    }

    val displayLetters = when (activePersona) {
        AssistantPersona.ULTRON -> "U . L . T . R . O . N"
        AssistantPersona.MAX -> "M . A . X"
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
                    AssistantCoreState.LISTENING -> (micAmplitude * 0.22f).coerceIn(0f, 0.25f)
                    AssistantCoreState.SPEAKING -> 0.12f
                    else -> 0f
                }

                // 1. Technical Crosshair Marks in Background (+)
                val crosshairColor = UltronCyan.copy(alpha = 0.22f)
                val chLen = 8.dp.toPx()
                val chOffsets = listOf(
                    Offset(size.width * 0.15f, size.height * 0.22f),
                    Offset(size.width * 0.84f, size.height * 0.26f),
                    Offset(size.width * 0.16f, size.height * 0.78f),
                    Offset(size.width * 0.85f, size.height * 0.76f)
                )
                chOffsets.forEach { pos ->
                    drawLine(crosshairColor, Offset(pos.x - chLen, pos.y), Offset(pos.x + chLen, pos.y), 1.5.dp.toPx())
                    drawLine(crosshairColor, Offset(pos.x, pos.y - chLen), Offset(pos.x, pos.y + chLen), 1.5.dp.toPx())
                }

                // 2. Soft Ambient Background Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryAccent.copy(alpha = if (coreState == AssistantCoreState.IDLE) 0.18f else 0.30f),
                            UltronViolet.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = maxR * (pulseScale + activeBoost)
                    ),
                    radius = maxR
                )

                // 3. Outer Thin Dashed Technical Orbit Ring
                drawCircle(
                    color = UltronViolet.copy(alpha = 0.35f),
                    radius = maxR * 0.94f,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )

                // 4. Reference Glowing Arc 1: Top-Right White/Silver Arc
                rotate(degrees = rotationSlow * 0.6f, pivot = center) {
                    drawArc(
                        color = UltronOrbSilver,
                        startAngle = 295f,
                        sweepAngle = 76f,
                        useCenter = false,
                        topLeft = Offset(center.x - maxR * 0.86f, center.y - maxR * 0.86f),
                        size = Size(maxR * 1.72f, maxR * 1.72f),
                        style = Stroke(width = 6.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 5. Reference Glowing Arc 2: Left Cyan Arc
                rotate(degrees = -rotationSlow * 0.8f, pivot = center) {
                    drawArc(
                        color = UltronCyanBright,
                        startAngle = 140f,
                        sweepAngle = 65f,
                        useCenter = false,
                        topLeft = Offset(center.x - maxR * 0.86f, center.y - maxR * 0.86f),
                        size = Size(maxR * 1.72f, maxR * 1.72f),
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 6. Reference Glowing Arc 3: Top Magenta/Purple Arc
                rotate(degrees = rotationSlow * 1.1f, pivot = center) {
                    drawArc(
                        color = UltronOrbMagenta,
                        startAngle = 220f,
                        sweepAngle = 82f,
                        useCenter = false,
                        topLeft = Offset(center.x - maxR * 0.77f, center.y - maxR * 0.77f),
                        size = Size(maxR * 1.54f, maxR * 1.54f),
                        style = Stroke(width = 3.8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 7. Inner Sphere Dark Chassis
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            UltronSurfaceElevated.copy(alpha = 0.95f),
                            UltronObsidian
                        ),
                        center = center,
                        radius = maxR * 0.68f
                    ),
                    radius = maxR * 0.68f,
                    center = center
                )
                drawCircle(
                    color = UltronGlassBorder.copy(alpha = 0.65f),
                    radius = maxR * 0.68f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                // 8. Dynamic Audio Equalizer Waveform Dots/Bars (13 Bars)
                val barCount = 13
                val totalBarsWidth = maxR * 0.82f
                val spacing = totalBarsWidth / (barCount - 1)
                val startX = center.x - totalBarsWidth / 2f
                val barCenterY = center.y + maxR * 0.18f

                for (b in 0 until barCount) {
                    val normalizedDist = 1f - (abs(b - barCount / 2f) / (barCount / 2f))
                    val waveAmp = when (coreState) {
                        AssistantCoreState.LISTENING -> {
                            (0.20f + (micAmplitude * 1.4f + 0.3f * abs(sin(rotationSlow * 0.12f + b * 0.6f))) * normalizedDist)
                                .coerceIn(0.15f, 1f)
                        }
                        AssistantCoreState.SPEAKING -> {
                            (0.25f + 0.75f * abs(cos(rotationSlow * 0.15f + b * 0.5f)) * normalizedDist)
                                .coerceIn(0.18f, 1f)
                        }
                        AssistantCoreState.THINKING, AssistantCoreState.EXECUTING -> {
                            (0.22f + 0.55f * abs(sin(rotationSlow * 0.2f + b * 0.4f)))
                                .coerceIn(0.18f, 0.85f)
                        }
                        else -> {
                            (0.18f + 0.12f * abs(sin(rotationSlow * 0.05f + b * 0.3f)))
                                .coerceIn(0.15f, 0.35f)
                        }
                    }

                    val barHeight = maxR * 0.22f * waveAmp
                    val isCenter = abs(b - barCount / 2) <= 1
                    val barColor = if (isCenter) UltronCyanBright else if (b % 2 == 0) UltronViolet else UltronMagenta

                    drawRoundRect(
                        color = barColor.copy(alpha = if (coreState == AssistantCoreState.IDLE) 0.65f else 0.95f),
                        topLeft = Offset(startX + b * spacing - 1.5.dp.toPx(), barCenterY - barHeight / 2f),
                        size = Size(3.dp.toPx(), barHeight.coerceAtLeast(3.dp.toPx())),
                        cornerRadius = CornerRadius(2.5f, 2.5f)
                    )
                }
            }

            // Central Reference Content (AI ASSISTANT + Stylized Name + Hint)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(coreSize * 0.72f)
                    .clip(CircleShape)
            ) {
                // Top Tag: "—— AI ASSISTANT ——"
                Text(
                    text = "—— AI ASSISTANT ——",
                    style = MaterialTheme.typography.labelSmall,
                    color = UltronTextSecondary,
                    fontSize = 8.5.sp,
                    letterSpacing = 1.8.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Big Spaced Gradient Title ("U . L . T . R . O . N" / "M . A . Y . A")
                Text(
                    text = displayLetters,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    fontSize = 21.sp,
                    letterSpacing = 2.sp,
                    color = UltronTextPrimary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Hint Caption ("BOLO, KAISE HELP KARUN?" / "SUN RAHI HAI...")
                val hintText = when (coreState) {
                    AssistantCoreState.IDLE -> "BOLO, KAISE HELP KARUN?"
                    AssistantCoreState.WAKE_DETECTED -> "WAKE DETECTED"
                    AssistantCoreState.LISTENING -> "SUN RAHI HAI..."
                    AssistantCoreState.THINKING, AssistantCoreState.TRANSCRIBING -> "SOCH RAHI HAI..."
                    AssistantCoreState.EXECUTING -> "KAAM HO RAHA HAI..."
                    AssistantCoreState.SPEAKING -> "BOL RAHI HAI..."
                    AssistantCoreState.PERMISSION_BLOCKED -> "MIC PERMISSION CHAHIYE"
                    AssistantCoreState.ERROR, AssistantCoreState.OFFLINE -> "KUCHH DIQQAT AAYI"
                    else -> coreState.badgeLabel.uppercase(Locale.ROOT)
                }

                Text(
                    text = hintText,
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryAccent,
                    fontSize = 9.sp,
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Reference-Faithful Glassmorphic Chat Bar:
 * [ Paperclip Attachment ] "Maya se kuchh bhi poochho..." [ STOP (when active) ] [ Send Arrow ]
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

    val placeholderName = if (activePersona == AssistantPersona.MAX) "Max" else activePersona.displayName

    Surface(
        color = UltronObsidian.copy(alpha = 0.95f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Surface(
            color = UltronSurface.copy(alpha = 0.92f),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, UltronGlassBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Paperclip Attachment Button (Reference Left Icon)
                IconButton(
                    onClick = onAttachClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("composer_attach_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach Document or Scan",
                        tint = UltronTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Input Field ("Maya se kuchh bhi poochho...")
                OutlinedTextField(
                    value = composerText,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            text = if (isListening) {
                                "Sun rahi hoon, boliye..."
                            } else {
                                "$placeholderName se kuchh bhi poochho..."
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = UltronViolet
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
                            .height(36.dp)
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

                // Reference Send Arrow in Purple Circle
                IconButton(
                    onClick = {
                        if (composerText.isNotBlank()) {
                            onSendClick()
                            keyboardController?.hide()
                        }
                    },
                    enabled = composerText.isNotBlank(),
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (composerText.isNotBlank()) {
                                Brush.horizontalGradient(listOf(UltronVividPurple, UltronDeepPurple))
                            } else {
                                Brush.horizontalGradient(listOf(UltronSurfaceElevated, UltronSurfaceElevated))
                            }
                        )
                        .testTag("composer_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (composerText.isNotBlank()) UltronTextPrimary else UltronTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * 5 Reference-Faithful Navigation Dock Items:
 * 1. Home (Ghar)
 * 2. Scan (Viewfinder / Camera)
 * 3. Elevated Central Live / Mic Button (Vibrant Purple Floating Orb)
 * 4. Yaadein (Memories & Tasks)
 * 5. Chat (Conversation)
 */
@Composable
fun UltronBottomNavigation(
    currentDestination: UltronDestination,
    isListening: Boolean,
    activePersona: AssistantPersona = AssistantPersona.ULTRON,
    onNavigate: (UltronDestination) -> Unit,
    onCenterLiveMicClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Surface(
            color = UltronDeepDark,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column {
                HorizontalDivider(color = UltronGlassBorder.copy(alpha = 0.45f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Home
                    BottomNavItem(
                        label = "Home",
                        icon = Icons.Default.Home,
                        selected = currentDestination == UltronDestination.GHAR,
                        accentColor = UltronViolet,
                        tag = "nav_tab_home",
                        onClick = { onNavigate(UltronDestination.GHAR) }
                    )

                    // 2. Scan
                    BottomNavItem(
                        label = "Scan",
                        icon = Icons.Default.Visibility,
                        selected = currentDestination == UltronDestination.SCAN || currentDestination == UltronDestination.VISION_CENTER,
                        accentColor = UltronViolet,
                        tag = "nav_tab_scan",
                        onClick = { onNavigate(UltronDestination.SCAN) }
                    )

                    // 3. Center Spacer for Elevated Mic Orb
                    Spacer(modifier = Modifier.width(60.dp))

                    // 4. Yaadein
                    BottomNavItem(
                        label = "Yaadein",
                        icon = Icons.Default.Bookmarks,
                        selected = currentDestination == UltronDestination.YAADEIN,
                        accentColor = UltronViolet,
                        tag = "nav_tab_yaadein",
                        onClick = { onNavigate(UltronDestination.YAADEIN) }
                    )

                    // 5. Chat
                    BottomNavItem(
                        label = "Chat",
                        icon = Icons.AutoMirrored.Filled.Chat,
                        selected = currentDestination == UltronDestination.CHAT,
                        accentColor = UltronViolet,
                        tag = "nav_tab_chat",
                        onClick = { onNavigate(UltronDestination.CHAT) }
                    )
                }
            }
        }

        // Elevated Central Live / Mic Floating Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(UltronVividPurple, UltronDeepPurple)
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(UltronOrbMagenta, UltronCyanBright, UltronOrbMagenta)
                        ),
                        shape = CircleShape
                    )
                    .clickable { onCenterLiveMicClick() }
                    .testTag("nav_tab_live_mic")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Live Voice Mic",
                    tint = UltronTextPrimary,
                    modifier = Modifier.size(28.dp)
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
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) accentColor else UltronTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) accentColor else UltronTextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
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
