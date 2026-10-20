package com.example.platform

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import com.example.domain.model.AssistantPersona
import com.example.domain.model.DeviceTelemetry
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.IntentCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ToolExecutionResult(
    val handledDirectly: Boolean,
    val category: IntentCategory,
    val responseTextHinglish: String = "",
    val switchedPersona: AssistantPersona? = null,
    val requiresConfirmation: Boolean = false,
    val pendingActionLabel: String? = null,
    val pendingActionLambda: (() -> String)? = null
)

class DeviceControlAndToolRouter(private val context: Context) {

    private var isFlashlightActive = false

    fun readRealDeviceTelemetry(): DeviceTelemetry {
        // 1. Real RAM via ActivityManager.MemoryInfo
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        val totalRamMb = (memInfo.totalMem / (1024L * 1024L)).coerceAtLeast(1L)
        val availRamMb = (memInfo.availMem / (1024L * 1024L)).coerceAtLeast(0L)
        val usedRamMb = (totalRamMb - availRamMb).coerceAtLeast(1L)
        val ramPct = ((usedRamMb * 100L) / totalRamMb).toInt().coerceIn(1, 100)

        // 2. Real Internal Storage via StatFs
        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalBytes = statFs.totalBytes
        val freeBytes = statFs.availableBytes
        val totalGb = totalBytes / (1024f * 1024f * 1024f)
        val freeGb = freeBytes / (1024f * 1024f * 1024f)
        val usedStoragePct = if (totalBytes > 0) {
            (((totalBytes - freeBytes) * 100L) / totalBytes).toInt().coerceIn(1, 100)
        } else 0

        // 3. Real Battery via BatteryManager & Sticky Broadcast
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryPct = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            .let { if (it in 1..100) it else 85 }
        val batteryStatusIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        // 4. Real Network via ConnectivityManager
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNet = connManager.activeNetwork
        val caps = activeNet?.let { connManager.getNetworkCapabilities(it) }
        val isOnline = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val netLabel = when {
            !isOnline -> "OFFLINE"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "ONLINE • Wi-Fi"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "ONLINE • Mobile Data"
            else -> "ONLINE"
        }

        // 5. Real CPU Core Count & JVM Heap (System-wide /proc/stat CPU % is restricted on Android 8+)
        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        val runtime = Runtime.getRuntime()
        val heapUsedMb = ((runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L)).coerceAtLeast(1L)
        val heapMaxMb = (runtime.maxMemory() / (1024L * 1024L)).coerceAtLeast(heapUsedMb)

        // 6. Real Audio Volume via AudioManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).coerceIn(0, maxVol)
        val volPct = ((curVol * 100) / maxVol).coerceIn(0, 100)

        return DeviceTelemetry(
            ramUsedMb = usedRamMb,
            ramTotalMb = totalRamMb,
            ramPercent = ramPct,
            storageFreeGb = freeGb,
            storageTotalGb = totalGb,
            storagePercent = usedStoragePct,
            batteryPercent = batteryPct,
            isCharging = isCharging,
            isOnline = isOnline,
            networkLabel = netLabel,
            cpuCores = cores,
            appHeapUsedMb = heapUsedMb,
            appHeapMaxMb = heapMaxMb,
            flashlightOn = isFlashlightActive,
            mediaVolumePercent = volPct,
            maxMediaVolume = maxVol,
            currentMediaVolume = curVol
        )
    }

    fun getTelemetrySummaryHinglish(): String {
        val t = readRealDeviceTelemetry()
        return "Battery ${t.batteryPercent}%${if (t.isCharging) " (Charging)" else ""}, RAM ${t.formattedRam}, CPU ${t.cpuCores} cores (JVM Heap ${t.appHeapUsedMb}/${t.appHeapMaxMb} MB; system-wide CPU % Android 8+ par restricted hai), Storage ${String.format(java.util.Locale.US, "%.1f", t.storageFreeGb)} GB free, Network ${t.networkLabel}."
    }

    fun setMediaVolume(level: Int): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level.coerceIn(0, max), 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun toggleFlashlight(enable: Boolean): Pair<Boolean, String> {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull()
                ?: return false to "Is device/emulator par camera flash hardware nahi mila."
            cameraManager.setTorchMode(cameraId, enable)
            isFlashlightActive = enable
            true to if (enable) "Flashlight ON kar diya hai." else "Flashlight OFF kar diya hai."
        } catch (e: Exception) {
            false to "Flashlight control is device par unavailable hai (${e.localizedMessage ?: "Hardware restriction"})."
        }
    }

    fun openAndroidSettingsPanel(action: String): Pair<Boolean, String> {
        return try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true to "Android Settings screen khol diya hai."
        } catch (e: Exception) {
            try {
                val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
                true to "Main Android Settings khol diya hai."
            } catch (_: Exception) {
                false to "Settings screen open nahi ho paya."
            }
        }
    }

    fun openAppPermissionSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openAccessibilitySettings(): Pair<Boolean, String> {
        return openAndroidSettingsPanel(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }

    fun openBatteryOptimizationSettings(): Pair<Boolean, String> {
        return openAndroidSettingsPanel(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    }

    fun sendEmailIntent(toEmail: String, subject: String, body: String): Pair<Boolean, String> {
        return try {
            val uri = Uri.parse("mailto:${Uri.encode(toEmail.trim())}?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true to "Email composer khol diya hai ($toEmail)."
        } catch (e: Exception) {
            false to "Is device par Email client app nahi mila."
        }
    }

    fun sendWhatsAppOrSmsIntent(phoneNumber: String, message: String): Pair<Boolean, String> {
        return try {
            val cleanPhone = phoneNumber.filter { it.isDigit() || it == '+' }
            val url = if (cleanPhone.isNotEmpty()) {
                "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
            } else {
                "sms:?body=${Uri.encode(message)}"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true to "Message handoff screen khol diya hai."
        } catch (e: Exception) {
            false to "Messaging app open nahi ho paya."
        }
    }

    fun triggerEmergencyDialIntent(emergencyNumber: String = "112"): Pair<Boolean, String> {
        return try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${emergencyNumber.trim()}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true to "Emergency dialer ($emergencyNumber) khol diya hai."
        } catch (_: Exception) {
            false to "Dialer app open nahi ho paya."
        }
    }

    fun queryInstalledLaunchableApps(): List<InstalledAppInfo> {
        return try {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
            resolveInfos.mapNotNull { ri ->
                val pkg = ri.activityInfo?.packageName ?: return@mapNotNull null
                val label = ri.loadLabel(pm)?.toString()?.trim().orEmpty()
                if (label.isNotEmpty() && pkg != context.packageName) {
                    InstalledAppInfo(appName = label, packageName = pkg)
                } else null
            }.distinctBy { it.packageName }.sortedBy { it.appName.lowercase() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun launchAppByNameOrAlias(rawQuery: String, userAddress: String = "Fardeen"): Pair<Boolean, String> {
        val clean = rawQuery.lowercase().trim()
        val pm = context.packageManager

        val knownPackages = mapOf(
            "youtube" to "com.google.android.youtube",
            "whatsapp" to "com.whatsapp",
            "chrome" to "com.android.chrome",
            "browser" to "com.android.chrome",
            "spotify" to "com.spotify.music",
            "gmail" to "com.google.android.gm",
            "maps" to "com.google.android.apps.maps",
            "calculator" to "com.google.android.calculator",
            "calendar" to "com.google.android.calendar",
            "camera" to "com.android.camera",
            "phone" to "com.google.android.dialer",
            "messages" to "com.google.android.apps.messaging",
            "files" to "com.google.android.documentsui"
        )

        if (clean.contains("settings") || clean.contains("setting")) {
            return openAndroidSettingsPanel(Settings.ACTION_SETTINGS)
        }

        val installed = queryInstalledLaunchableApps()
        val exactOrPartial = installed.filter {
            it.appName.lowercase().contains(clean) || clean.contains(it.appName.lowercase())
        }

        if (exactOrPartial.isNotEmpty()) {
            val chosen = exactOrPartial.first()
            val launchIntent = pm.getLaunchIntentForPackage(chosen.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return try {
                    context.startActivity(launchIntent)
                    true to "Yes $userAddress. ${chosen.appName} khol raha hoon."
                } catch (_: Exception) {
                    false to "${chosen.appName} launch karne mein system restriction aayi."
                }
            }
        }

        val targetPkg = knownPackages.entries.firstOrNull { clean.contains(it.key) }?.value
        if (targetPkg != null) {
            val launchIntent = pm.getLaunchIntentForPackage(targetPkg)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return try {
                    context.startActivity(launchIntent)
                    true to "Yes $userAddress. $rawQuery khol raha hoon."
                } catch (_: Exception) {
                    false to "$rawQuery open nahi ho paya."
                }
            }
        }

        if (clean.contains("youtube")) {
            return openYouTubeSearchOrHome("", userAddress)
        }

        return false to "$userAddress, '$rawQuery' is device par installed nahi mila. Aap Control Center mein installed apps ki list dekh sakte hain."
    }

    fun openYouTubeSearchOrHome(searchQuery: String, userAddress: String = "Fardeen"): Pair<Boolean, String> {
        val encoded = Uri.encode(searchQuery.trim())
        val url = if (searchQuery.isBlank()) {
            "https://www.youtube.com"
        } else {
            "https://www.youtube.com/results?search_query=$encoded"
        }

        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            if (searchQuery.isBlank()) {
                true to "Yes $userAddress. YouTube khol raha hoon."
            } else {
                true to "$userAddress, YouTube par '$searchQuery' ka official search khol diya hai. (Playback tab shuru hoga jab aap video select karenge.)"
            }
        } catch (e: Exception) {
            false to "Is device par YouTube ya Web Browser app installed nahi mila."
        }
    }

    fun classifyAndRouteInput(
        userInput: String,
        userAddress: String,
        activePersona: AssistantPersona = AssistantPersona.ULTRON
    ): ToolExecutionResult {
        val raw = userInput.trim()
        val rawLower = raw.lowercase()

        // 0. Check for Persona Handoff ("Max ko bhej", "Ultron ko bhej", "Switch to Max", "Switch to Ultron")
        val wantsMax = rawLower.contains("max ko bhej") ||
            rawLower.contains("max ko bula") ||
            rawLower.contains("max se baat") ||
            rawLower.contains("switch to max") ||
            rawLower.contains("max mode") ||
            rawLower.contains("bhej max ko") ||
            rawLower == "max" ||
            rawLower == "hey max"

        val wantsUltron = rawLower.contains("ultron ko bhej") ||
            rawLower.contains("ultron ko bula") ||
            rawLower.contains("ultron se baat") ||
            rawLower.contains("switch to ultron") ||
            rawLower.contains("ultron mode") ||
            rawLower.contains("bhej ultron ko") ||
            rawLower == "ultron" ||
            rawLower == "hey ultron"

        if (wantsMax && activePersona != AssistantPersona.MAX) {
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.PERSONA_SWITCH,
                switchedPersona = AssistantPersona.MAX,
                responseTextHinglish = "Theek hai $userAddress, main Max ko bhej raha hoon. Hey Boss! MAX ab online hai—batao kya mission hai?"
            )
        }
        if (wantsUltron && activePersona != AssistantPersona.ULTRON) {
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.PERSONA_SWITCH,
                switchedPersona = AssistantPersona.ULTRON,
                responseTextHinglish = "Bilkul Boss! Ultron ko wapas bula raha hoon. Systems online, $userAddress. Main ULTRON hoon—batao kya solve karna hai?"
            )
        }

        val lower = rawLower
            .removePrefix("ultron ")
            .removePrefix("hey ultron ")
            .removePrefix("max ")
            .removePrefix("hey max ")
            .trim()

        // 1. Wi-Fi / Bluetooth / Brightness / Flashlight / Volume controls
        if (lower.contains("wifi") || lower.contains("wi-fi")) {
            val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Panel.ACTION_WIFI
            } else {
                Settings.ACTION_WIFI_SETTINGS
            }
            val (_, msg) = openAndroidSettingsPanel(action)
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.DEVICE_CONTROL,
                responseTextHinglish = "$userAddress, Android security rules ke mutabik Wi-Fi panel khol diya hai—wahan se 1-tap mein toggle karein. ($msg)"
            )
        }

        if (lower.contains("bluetooth")) {
            val (_, msg) = openAndroidSettingsPanel(Settings.ACTION_BLUETOOTH_SETTINGS)
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.DEVICE_CONTROL,
                responseTextHinglish = "$userAddress, Bluetooth Settings khol diya hai. ($msg)"
            )
        }

        if (lower.contains("brightness") || lower.contains("display setting")) {
            val (_, msg) = openAndroidSettingsPanel(Settings.ACTION_DISPLAY_SETTINGS)
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.DEVICE_CONTROL,
                responseTextHinglish = "$userAddress, Display & Brightness settings khol diya hai. ($msg)"
            )
        }

        if (lower.contains("flashlight") || lower.contains("torch")) {
            val turnOn = !lower.contains("off") && !lower.contains("band")
            val (_, msg) = toggleFlashlight(turnOn)
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.DEVICE_CONTROL,
                responseTextHinglish = msg
            )
        }

        if (lower.contains("volume")) {
            val telemetry = readRealDeviceTelemetry()
            val newVol = when {
                lower.contains("full") || lower.contains("max") -> telemetry.maxMediaVolume
                lower.contains("mute") || lower.contains("zero") || lower.contains("band") -> 0
                lower.contains("kam") || lower.contains("down") || lower.contains("low") ->
                    (telemetry.currentMediaVolume - 3).coerceAtLeast(0)
                else -> (telemetry.currentMediaVolume + 3).coerceAtMost(telemetry.maxMediaVolume)
            }
            setMediaVolume(newVol)
            val updatedPct = ((newVol * 100) / telemetry.maxMediaVolume).coerceIn(0, 100)
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.DEVICE_CONTROL,
                responseTextHinglish = "Media volume $updatedPct% par set kar diya hai, $userAddress."
            )
        }

        // 2. YouTube / Gaane search or open
        if (lower.contains("gaana") || lower.contains("song") || lower.contains("play ") ||
            (lower.contains("youtube") && (lower.contains("search") || lower.contains("dhoondo") || lower.contains("chalao")))
        ) {
            val query = lower
                .replace("youtube par", "")
                .replace("youtube", "")
                .replace("gaana chalao", "")
                .replace("ye gaana chalao", "")
                .replace("chalao", "")
                .replace("search karo", "")
                .replace("dhoondo", "")
                .replace("play", "")
                .trim()
            val (_, msg) = openYouTubeSearchOrHome(query.ifBlank { "Bollywood Hindi Hits" }, userAddress)
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.SEARCH,
                responseTextHinglish = msg
            )
        }

        // 3. App Launching ("WhatsApp kholo", "YouTube kholo", "open Chrome", etc.)
        if (lower.endsWith("kholo") || lower.endsWith("open karo") || lower.startsWith("open ")) {
            val appTarget = lower
                .removeSuffix("kholo")
                .removeSuffix("open karo")
                .removePrefix("open ")
                .trim()
            if (appTarget.isNotEmpty()) {
                val (_, msg) = launchAppByNameOrAlias(appTarget, userAddress)
                return ToolExecutionResult(
                    handledDirectly = true,
                    category = IntentCategory.COMMAND,
                    responseTextHinglish = msg
                )
            }
        }

        // 4. Common Conversational Questions & Real Telemetry queries
        if (lower.contains("india") && (lower.contains("capital") || lower.contains("rajdhani"))) {
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.QUESTION,
                responseTextHinglish = "New Delhi, $userAddress."
            )
        }

        if (lower.contains("gravity") && (lower.contains("kya") || lower.contains("explain") || lower.contains("hoti"))) {
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.QUESTION,
                responseTextHinglish = "Gravity ek fundamental natural force hai jisse har mass dusre mass ko attract karta hai. Isi force ki wajah se hum Earth par tike rehte hain aur planets Sun ke chakkar lagate hain."
            )
        }

        if (lower.contains("aaj kya date") || lower.contains("aaj ki date") || lower.contains("kya date hai") ||
            lower.contains("tarikh kya hai") || lower.contains("date kya hai")
        ) {
            val dateStr = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US).format(Date())
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.QUESTION,
                responseTextHinglish = "Aaj $dateStr hai, $userAddress."
            )
        }

        if (lower.contains("cpu kitna") || lower.contains("cpu use") || lower.contains("cpu usage")) {
            val telemetry = readRealDeviceTelemetry()
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.DEVICE_CONTROL,
                responseTextHinglish = "$userAddress, CPU mein ${telemetry.cpuCores} active cores hain aur JVM heap ${telemetry.appHeapUsedMb} MB / ${telemetry.appHeapMaxMb} MB active hai."
            )
        }

        if (lower.contains("system status") || lower.contains("ram kitna") ||
            lower.contains("battery kitni") || lower.contains("telemetry")
        ) {
            val telemetry = readRealDeviceTelemetry()
            val reply = if (activePersona == AssistantPersona.MAX) {
                "Boss, systems stable hain. Battery ${telemetry.batteryPercent}%, network ${if (telemetry.isOnline) "online" else "offline"} aur MAX fully operational. 😎"
            } else {
                "$userAddress, systems stable hain. ${getTelemetrySummaryHinglish()}"
            }
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.DEVICE_CONTROL,
                responseTextHinglish = reply
            )
        }

        if (lower == "ek pdf bana" || lower == "pdf bana" || lower == "ek note bana" || lower == "note bana" || lower == "note bana do") {
            return ToolExecutionResult(
                handledDirectly = true,
                category = IntentCategory.FILE_TASK,
                responseTextHinglish = "Kis topic par, $userAddress?"
            )
        }

        // 5. Classify remaining prompts for AI Router
        val category = when {
            lower.contains("samjha") || lower.contains("explain") || lower.contains("padhai") ||
                lower.contains("formula") || lower.contains("quiz") -> IntentCategory.STUDY
            lower.contains("sapna") || lower.contains("dream") -> IntentCategory.DREAM
            lower.contains("screen") -> IntentCategory.SCREEN_TASK
            lower.contains("?") || lower.contains("kya") || lower.contains("kaun") ||
                lower.contains("kaise") || lower.contains("kyu") || lower.contains("what") ||
                lower.contains("how") || lower.contains("why") -> IntentCategory.QUESTION
            else -> IntentCategory.CONVERSATION
        }

        return ToolExecutionResult(
            handledDirectly = false,
            category = category
        )
    }
}
