# ULTRON AI — Cinematic Personal AI Assistant for Android

**Configured Creator Record:** Fardeen (`"Mujhe Fardeen ne banaya hai."`)  
**Primary Persona & UI Language:** Natural Roman Hinglish (with selectable Hindi & English modes)  
**Target Platform:** Android (Kotlin + Jetpack Compose + Material 3 + Room + DataStore + Retrofit/OkHttp + Android TTS/STT + MediaProjection)

---

## 1. Architecture Diagram

```text
┌────────────────────────────────────────────────────────────────────────────┐
│                        ULTRON AI — PRESENTATION LAYER                      │
│  ┌──────────────────┐  ┌────────────────────────┐  ┌────────────────────┐  │
│  │ Compact Top Bar  │  │ Categorized Hamburger  │  │ 5-Tab Bottom Nav   │  │
│  │ (Status/Battery) │  │ Navigation Drawer      │  │ (Ghar/Scan/Mic/..) │  │
│  └────────┬─────────┘  └───────────┬────────────┘  └─────────┬──────────┘  │
│           ▼                        ▼                         ▼             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │               Central Session & UI State Coordinator                 │  │
│  │   [Ghar / Home]   [Chat & History]  [Awaaz Studio]   [Live Voice]    │  │
│  │   [Control Ctr]   [Screen Samjho]   [Avatar Studio]  [Scan / OCR]    │  │
│  │   [Padhai/Gaane]  [Journal/Dreams]  [Mausam/Aaj/Mood][Yaadein/Mem]   │  │
│  └───────────────────────────────┬──────────────────────────────────────┘  │
└──────────────────────────────────┼─────────────────────────────────────────┘
                                   ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                      DOMAIN & STATE MACHINE LAYER                          │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ AssistantCoreState: IDLE -> WAKE_DETECTED -> LISTENING ->            │  │
│  │                     TRANSCRIBING -> THINKING -> EXECUTING ->         │  │
│  │                     SPEAKING -> IDLE (or ERROR / OFFLINE)            │  │
│  └──────────────┬───────────────────────────────────┬───────────────────┘  │
│                 ▼                                   ▼                      │
│  ┌──────────────────────────────┐    ┌──────────────────────────────────┐  │
│  │ Smart Model & Intent Router  │    │ Typed Tool & Automation Router   │  │
│  │ - Question / Chat / Hinglish │    │ - App Launch (PackageManager)    │  │
│  │ - Padhai / Dream / Vision    │    │ - YouTube / Gaane Intent Handoff │  │
│  │ - Context Memory Injection   │    │ - Flashlight / Volume / Settings │  │
│  └──────────────┬───────────────┘    └──────────────┬───────────────────┘  │
└─────────────────┼───────────────────────────────────┼──────────────────────┘
                  ▼                                   ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                    DATA & ANDROID PLATFORM LAYER                           │
│  ┌────────────────────────┐  ┌─────────────────────┐  ┌─────────────────┐  │
│  │ AI & Weather Clients   │  │ Audio & Vision SDKs │  │ Local Storage   │  │
│  │ - Gemini REST / Stream │  │ - Android TTS Queue │  │ - Room Database │  │
│  │ - Fallback Retry Logic │  │ - SpeechRecognizer  │  │ - DataStore Pref│  │
│  │ - Open-Meteo Weather   │  │ - MediaProjection   │  │ - GLB Validator │  │
│  └────────────────────────┘  └─────────────────────┘  └─────────────────┘  │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Feature-to-Permission & Platform Boundary Map

| Feature | Android Permission / API | Behavior & Platform Boundary |
|---|---|---|
| **Voice Input & Push-to-Talk** | `RECORD_AUDIO` + `SpeechRecognizer` | Just-in-time permission prompt; supports partial/final transcripts and barge-in interruption |
| **Wake-Word Session (`MAX` / `ULTRON`)** | `RECORD_AUDIO` + Foreground Speech Session | Separate from push-to-talk mic; replies *"Yes, Boss"* / *"Yes, Fardeen"* and transitions to `LISTENING` |
| **Awaaz Studio (TTS)** | `android.speech.tts.TextToSpeech` + `AudioFocusRequest` | Enumerates real installed voices, strips markdown symbols for natural speech, supports `0.8x–1.2x` speed, pitch, and immediate `STOP` |
| **App Launching (`Apps kholna`)** | `PackageManager` + `<queries>` in Manifest | Launches installed apps (YouTube, WhatsApp, Chrome, Settings, etc.) and reports honestly if an app is not installed |
| **Phone Control & Telemetry** | `ActivityManager`, `BatteryManager`, `StatFs`, `ConnectivityManager`, `CameraManager`, `AudioManager` | Real RAM (`GB / GB (%)`), CPU cores/load, Battery %, Storage, Volume slider, Torch toggle, and Android Settings panel intents for Wi-Fi/Bluetooth/Brightness |
| **Screen Samjho (Vision Center)** | `MediaProjectionManager` consent / Photo Picker + `gemini-3.5-flash` | Explicit user consent required per session; displays animated element highlights and `STOP` control |
| **Scan / OCR** | `CAMERA` / `PickVisualMedia` | Requests camera only when opened; extracts text and explains objects in Hinglish |
| **Mausam (Weather)** | `INTERNET` + Open-Meteo Live API | Fetches real live temperature, humidity, and wind speed for any city |
| **Yaadein, Journal, Dreams, Mood, Aaj** | Local Room SQLite (`ultron_ai_database.db`) + DataStore | Persistent CRUD, search, clipboard export, and Jungian Dream reflection |

---

## 3. Setup & Build Commands

1. **Configure Gemini API Key**:
   - In Google AI Studio, open the **Secrets panel** and add your `GEMINI_API_KEY`.
   - `.env.example` contains `GEMINI_API_KEY=MY_GEMINI_API_KEY` which the Secrets Gradle Plugin maps to `BuildConfig.GEMINI_API_KEY`.
2. **Build Debug APK**:
   ```bash
   gradle :app:assembleDebug
   ```
3. **Run Unit & Robolectric Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 4. Troubleshooting Guide

- **Microphone Blocked / Speech Recognition Unavailable**: Tap `OPEN SETTINGS` in the banner or in `Settings & Diagnostics` to grant `RECORD_AUDIO`. On emulators without Google Speech Services installed, use the `TEST "MAX / ULTRON"` button or the text composer to verify the full wake-to-response pipeline.
- **Cloud AI Offline / Missing Key**: If `GEMINI_API_KEY` is not yet configured in the Secrets panel or the device is offline, ULTRON automatically falls back to its `ultron-local-tactical-v1` engine so device commands, telemetry, creator attribution, weather (when online), and notes continue working without interruption.
