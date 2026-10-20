package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AssistantPersona
import com.example.domain.model.CornerThemeStyle
import com.example.domain.model.GenderVerbStyle
import com.example.domain.model.LanguageMode
import com.example.domain.model.RoutingMode
import com.example.domain.model.SubAgentRoutingStrategy
import com.example.domain.model.SurfaceThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ultron_preferences")

data class UltronSettingsState(
    val activePersona: AssistantPersona = AssistantPersona.MAX,
    val assistantName: String = "MAX",
    val configuredCreator: String = "Fardeen", // Protected configured creator record
    val userAddress: String = "Boss", // Default Boss polite address
    val genderVerbStyle: GenderVerbStyle = GenderVerbStyle.MALE,
    val userPhoneNumber: String = "",
    val preferredMusicApp: String = "YouTube",
    val favouriteSong: String = "Pasoori",
    val customGeminiApiKey: String = "",
    val languageMode: LanguageMode = LanguageMode.HINGLISH,
    val routingMode: RoutingMode = RoutingMode.AUTO,
    val subAgentStrategy: SubAgentRoutingStrategy = SubAgentRoutingStrategy.BEST_FIRST,
    val companionToneMode: String = "Tactical Companion",
    val wakeWordPhrase: String = "MAX",
    val wakeWordEnabled: Boolean = false,
    // Active persona voice parameters (resolved from Ultron vs Max profiles)
    val selectedVoiceId: String = "",
    val speechSpeed: Float = 1.02f,
    val speechPitch: Float = 1.02f,
    val speechStyle: String = "Natural",
    // Distinct Ultron vs Max saved voice profiles
    val ultronVoiceId: String = "",
    val ultronSpeed: Float = 0.98f,
    val ultronPitch: Float = 0.88f,
    val maxVoiceId: String = "",
    val maxSpeed: Float = 1.06f,
    val maxPitch: Float = 1.05f,
    // Look & Feel (Glassmorphism & Cyberpunk customization)
    val surfaceStyle: SurfaceThemeStyle = SurfaceThemeStyle.GLASS,
    val cornerStyle: CornerThemeStyle = CornerThemeStyle.ROUND,
    val realisticTypingEnabled: Boolean = true,
    val realisticTypingSpeed: String = "Tez", // "Dheere", "Normal", "Tez"
    val selectedAvatarId: String = "ultron_prime_core",
    val weatherCity: String = "New Delhi",
    val reducedMotion: Boolean = false,
    val batterySaverMode: Boolean = false,
    val cloudVisionConsent: Boolean = true,
    val autoSpeakReplies: Boolean = true,
    val silenceSleepTimeout: com.example.domain.model.LiveSilenceSleepTimeout = com.example.domain.model.LiveSilenceSleepTimeout.FIVE_MINUTES,
    val reduceMediaInterference: Boolean = true,
    val pcPairingHost: String = "192.168.1.100",
    val pcPairingPort: String = "8765",
    val backendProxyUrl: String = ""
)

class UltronPreferences(private val context: Context) {

    companion object {
        // Protected creator constant per master specification
        const val PROTECTED_CREATOR_NAME = "Fardeen"

        private val KEY_ACTIVE_PERSONA = stringPreferencesKey("active_persona")
        private val KEY_USER_ADDRESS = stringPreferencesKey("user_address")
        private val KEY_GENDER_VERB = stringPreferencesKey("gender_verb")
        private val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        private val KEY_MUSIC_APP = stringPreferencesKey("music_app")
        private val KEY_FAV_SONG = stringPreferencesKey("fav_song")
        private val KEY_CUSTOM_GEMINI_KEY = stringPreferencesKey("custom_gemini_key")
        private val KEY_LANGUAGE_MODE = stringPreferencesKey("language_mode")
        private val KEY_ROUTING_MODE = stringPreferencesKey("routing_mode")
        private val KEY_SUBAGENT_STRATEGY = stringPreferencesKey("subagent_strategy")
        private val KEY_COMPANION_TONE = stringPreferencesKey("companion_tone")
        private val KEY_WAKE_PHRASE = stringPreferencesKey("wake_phrase")
        private val KEY_WAKE_ENABLED = booleanPreferencesKey("wake_enabled")

        private val KEY_ULTRON_VOICE_ID = stringPreferencesKey("ultron_voice_id")
        private val KEY_ULTRON_SPEED = floatPreferencesKey("ultron_speed")
        private val KEY_ULTRON_PITCH = floatPreferencesKey("ultron_pitch")
        private val KEY_MAX_VOICE_ID = stringPreferencesKey("max_voice_id")
        private val KEY_MAX_SPEED = floatPreferencesKey("max_speed")
        private val KEY_MAX_PITCH = floatPreferencesKey("max_pitch")
        private val KEY_SPEECH_STYLE = stringPreferencesKey("speech_style")

        private val KEY_SURFACE_STYLE = stringPreferencesKey("surface_style")
        private val KEY_CORNER_STYLE = stringPreferencesKey("corner_style")
        private val KEY_REALISTIC_TYPING = booleanPreferencesKey("realistic_typing")
        private val KEY_TYPING_SPEED = stringPreferencesKey("typing_speed")
        private val KEY_SELECTED_AVATAR_ID = stringPreferencesKey("selected_avatar_id")
        private val KEY_WEATHER_CITY = stringPreferencesKey("weather_city")
        private val KEY_REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        private val KEY_BATTERY_SAVER = booleanPreferencesKey("battery_saver")
        private val KEY_CLOUD_VISION_CONSENT = booleanPreferencesKey("cloud_vision_consent")
        private val KEY_AUTO_SPEAK = booleanPreferencesKey("auto_speak_replies")
        private val KEY_SILENCE_SLEEP_TIMEOUT = stringPreferencesKey("silence_sleep_timeout")
        private val KEY_REDUCE_MEDIA_INTERFERENCE = booleanPreferencesKey("reduce_media_interference")
        private val KEY_PC_HOST = stringPreferencesKey("pc_host")
        private val KEY_PC_PORT = stringPreferencesKey("pc_port")
        private val KEY_BACKEND_PROXY_URL = stringPreferencesKey("backend_proxy_url")
    }

    val settingsFlow: Flow<UltronSettingsState> = context.dataStore.data.map { prefs ->
        val personaName = prefs[KEY_ACTIVE_PERSONA] ?: AssistantPersona.ULTRON.name
        val persona = AssistantPersona.entries.find { it.name == personaName } ?: AssistantPersona.ULTRON

        val langCode = prefs[KEY_LANGUAGE_MODE] ?: LanguageMode.HINGLISH.code
        val lang = LanguageMode.entries.find { it.code == langCode } ?: LanguageMode.HINGLISH

        val routeName = prefs[KEY_ROUTING_MODE] ?: RoutingMode.AUTO.name
        val route = RoutingMode.entries.find { it.name == routeName } ?: RoutingMode.AUTO

        val genderCode = prefs[KEY_GENDER_VERB] ?: GenderVerbStyle.MALE.code
        val gender = GenderVerbStyle.entries.find { it.code == genderCode } ?: GenderVerbStyle.MALE

        val surfaceCode = prefs[KEY_SURFACE_STYLE] ?: SurfaceThemeStyle.GLASS.code
        val surface = SurfaceThemeStyle.entries.find { it.code == surfaceCode } ?: SurfaceThemeStyle.GLASS

        val cornerCode = prefs[KEY_CORNER_STYLE] ?: CornerThemeStyle.ROUND.code
        val corner = CornerThemeStyle.entries.find { it.code == cornerCode } ?: CornerThemeStyle.ROUND

        val stratCode = prefs[KEY_SUBAGENT_STRATEGY] ?: SubAgentRoutingStrategy.BEST_FIRST.code
        val strat = SubAgentRoutingStrategy.entries.find { it.code == stratCode } ?: SubAgentRoutingStrategy.BEST_FIRST

        val uVoice = prefs[KEY_ULTRON_VOICE_ID] ?: ""
        val uSpeed = (prefs[KEY_ULTRON_SPEED] ?: 0.98f).coerceIn(0.8f, 1.2f)
        val uPitch = (prefs[KEY_ULTRON_PITCH] ?: 0.88f).coerceIn(0.75f, 1.25f)

        val mVoice = prefs[KEY_MAX_VOICE_ID] ?: ""
        val mSpeed = (prefs[KEY_MAX_SPEED] ?: 1.06f).coerceIn(0.8f, 1.2f)
        val mPitch = (prefs[KEY_MAX_PITCH] ?: 1.05f).coerceIn(0.75f, 1.25f)

        val activeVoice = if (persona == AssistantPersona.ULTRON) uVoice else mVoice
        val activeSpeed = if (persona == AssistantPersona.ULTRON) uSpeed else mSpeed
        val activePitch = if (persona == AssistantPersona.ULTRON) uPitch else mPitch
        val activeStyle = prefs[KEY_SPEECH_STYLE] ?: if (persona == AssistantPersona.ULTRON) "Deep Cinematic" else "Natural"

        UltronSettingsState(
            activePersona = persona,
            assistantName = persona.displayName,
            configuredCreator = PROTECTED_CREATOR_NAME,
            userAddress = prefs[KEY_USER_ADDRESS] ?: "Fardeen",
            genderVerbStyle = gender,
            userPhoneNumber = prefs[KEY_USER_PHONE] ?: "",
            preferredMusicApp = prefs[KEY_MUSIC_APP] ?: "YouTube",
            favouriteSong = prefs[KEY_FAV_SONG] ?: "Pasoori",
            customGeminiApiKey = prefs[KEY_CUSTOM_GEMINI_KEY] ?: "",
            languageMode = lang,
            routingMode = route,
            subAgentStrategy = strat,
            companionToneMode = prefs[KEY_COMPANION_TONE] ?: "Cinematic Sci-Fi",
            wakeWordPhrase = prefs[KEY_WAKE_PHRASE] ?: persona.name,
            wakeWordEnabled = prefs[KEY_WAKE_ENABLED] ?: false,
            selectedVoiceId = activeVoice,
            speechSpeed = activeSpeed,
            speechPitch = activePitch,
            speechStyle = activeStyle,
            ultronVoiceId = uVoice,
            ultronSpeed = uSpeed,
            ultronPitch = uPitch,
            maxVoiceId = mVoice,
            maxSpeed = mSpeed,
            maxPitch = mPitch,
            surfaceStyle = surface,
            cornerStyle = corner,
            realisticTypingEnabled = prefs[KEY_REALISTIC_TYPING] ?: true,
            realisticTypingSpeed = prefs[KEY_TYPING_SPEED] ?: "Tez",
            selectedAvatarId = prefs[KEY_SELECTED_AVATAR_ID] ?: "ultron_prime_core",
            weatherCity = prefs[KEY_WEATHER_CITY] ?: "New Delhi",
            reducedMotion = prefs[KEY_REDUCED_MOTION] ?: false,
            batterySaverMode = prefs[KEY_BATTERY_SAVER] ?: false,
            cloudVisionConsent = prefs[KEY_CLOUD_VISION_CONSENT] ?: true,
            autoSpeakReplies = prefs[KEY_AUTO_SPEAK] ?: true,
            silenceSleepTimeout = prefs[KEY_SILENCE_SLEEP_TIMEOUT]?.let {
                try { com.example.domain.model.LiveSilenceSleepTimeout.valueOf(it) } catch (_: Exception) { null }
            } ?: com.example.domain.model.LiveSilenceSleepTimeout.FIVE_MINUTES,
            reduceMediaInterference = prefs[KEY_REDUCE_MEDIA_INTERFERENCE] ?: true,
            pcPairingHost = prefs[KEY_PC_HOST] ?: "192.168.1.100",
            pcPairingPort = prefs[KEY_PC_PORT] ?: "8765",
            backendProxyUrl = prefs[KEY_BACKEND_PROXY_URL] ?: ""
        )
    }

    suspend fun setActivePersona(persona: AssistantPersona) {
        context.dataStore.edit {
            it[KEY_ACTIVE_PERSONA] = persona.name
            it[KEY_WAKE_PHRASE] = persona.name
        }
    }

    suspend fun setUserAddress(address: String) {
        context.dataStore.edit { it[KEY_USER_ADDRESS] = address.trim().ifBlank { "Fardeen" } }
    }

    suspend fun setPersonalConfig(
        address: String,
        gender: GenderVerbStyle,
        phone: String,
        musicApp: String,
        favSong: String,
        customKey: String
    ) {
        context.dataStore.edit {
            it[KEY_USER_ADDRESS] = address.trim().ifBlank { "Fardeen" }
            it[KEY_GENDER_VERB] = gender.code
            it[KEY_USER_PHONE] = phone.trim()
            it[KEY_MUSIC_APP] = musicApp
            it[KEY_FAV_SONG] = favSong.trim()
            it[KEY_CUSTOM_GEMINI_KEY] = customKey.trim()
        }
    }

    suspend fun setLanguageMode(mode: LanguageMode) {
        context.dataStore.edit { it[KEY_LANGUAGE_MODE] = mode.code }
    }

    suspend fun setRoutingMode(mode: RoutingMode) {
        context.dataStore.edit { it[KEY_ROUTING_MODE] = mode.name }
    }

    suspend fun setSubAgentStrategy(strategy: SubAgentRoutingStrategy) {
        context.dataStore.edit { it[KEY_SUBAGENT_STRATEGY] = strategy.code }
    }

    suspend fun setCompanionToneMode(tone: String) {
        context.dataStore.edit { it[KEY_COMPANION_TONE] = tone }
    }

    suspend fun setWakeWordPhrase(phrase: String) {
        context.dataStore.edit { it[KEY_WAKE_PHRASE] = phrase }
    }

    suspend fun setWakeWordEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_WAKE_ENABLED] = enabled }
    }

    suspend fun setVoiceConfig(
        persona: AssistantPersona,
        voiceId: String,
        speed: Float,
        pitch: Float,
        style: String
    ) {
        context.dataStore.edit {
            if (persona == AssistantPersona.ULTRON) {
                it[KEY_ULTRON_VOICE_ID] = voiceId
                it[KEY_ULTRON_SPEED] = speed.coerceIn(0.8f, 1.2f)
                it[KEY_ULTRON_PITCH] = pitch.coerceIn(0.75f, 1.25f)
            } else {
                it[KEY_MAX_VOICE_ID] = voiceId
                it[KEY_MAX_SPEED] = speed.coerceIn(0.8f, 1.2f)
                it[KEY_MAX_PITCH] = pitch.coerceIn(0.75f, 1.25f)
            }
            it[KEY_SPEECH_STYLE] = style
        }
    }

    suspend fun setLookAndFeel(surface: SurfaceThemeStyle, corner: CornerThemeStyle) {
        context.dataStore.edit {
            it[KEY_SURFACE_STYLE] = surface.code
            it[KEY_CORNER_STYLE] = corner.code
        }
    }

    suspend fun setRealisticTyping(enabled: Boolean, speed: String) {
        context.dataStore.edit {
            it[KEY_REALISTIC_TYPING] = enabled
            it[KEY_TYPING_SPEED] = speed
        }
    }

    suspend fun setSelectedAvatar(avatarId: String) {
        context.dataStore.edit { it[KEY_SELECTED_AVATAR_ID] = avatarId }
    }

    suspend fun setWeatherCity(city: String) {
        context.dataStore.edit { it[KEY_WEATHER_CITY] = city.trim().ifBlank { "New Delhi" } }
    }

    suspend fun setReducedMotion(reduced: Boolean) {
        context.dataStore.edit { it[KEY_REDUCED_MOTION] = reduced }
    }

    suspend fun setBatterySaverMode(enabled: Boolean) {
        context.dataStore.edit {
            it[KEY_BATTERY_SAVER] = enabled
            if (enabled) it[KEY_REDUCED_MOTION] = true
        }
    }

    suspend fun setCloudVisionConsent(consent: Boolean) {
        context.dataStore.edit { it[KEY_CLOUD_VISION_CONSENT] = consent }
    }

    suspend fun setAutoSpeakReplies(autoSpeak: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_SPEAK] = autoSpeak }
    }

    suspend fun setPcPairingConfig(host: String, port: String) {
        context.dataStore.edit {
            it[KEY_PC_HOST] = host.trim().ifBlank { "192.168.1.100" }
            it[KEY_PC_PORT] = port.trim().ifBlank { "8765" }
        }
    }

    suspend fun setSilenceSleepTimeout(timeout: com.example.domain.model.LiveSilenceSleepTimeout) {
        context.dataStore.edit { it[KEY_SILENCE_SLEEP_TIMEOUT] = timeout.name }
    }

    suspend fun setReduceMediaInterference(enabled: Boolean) {
        context.dataStore.edit { it[KEY_REDUCE_MEDIA_INTERFERENCE] = enabled }
    }
}
