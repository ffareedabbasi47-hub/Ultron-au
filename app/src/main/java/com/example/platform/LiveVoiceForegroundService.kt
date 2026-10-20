package com.example.platform

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity

/**
 * Persistent Android Foreground Service for Live Voice Conversation.
 * Keeps the microphone session alive while user-initiated live conversation is active,
 * displays an honest persistent notification with an immediate "STOP" action,
 * and obeys Android 14+ foregroundServiceType="microphone" rules.
 */
class LiveVoiceForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "ultron_live_voice_channel"
        const val NOTIFICATION_ID = 4102
        const val ACTION_START_LIVE = "com.example.action.START_LIVE_VOICE"
        const val ACTION_UPDATE_STATUS = "com.example.action.UPDATE_LIVE_STATUS"
        const val ACTION_STOP_LIVE = "com.example.action.STOP_LIVE_VOICE"
        const val EXTRA_STATUS_TEXT = "extra_status_text"

        @Volatile
        var onStopRequestedFromNotification: (() -> Unit)? = null

        fun startOrUpdate(context: Context, statusText: String = "Live Conversation — Microphone Active (AEC & Media Filter ON)") {
            try {
                val intent = Intent(context, LiveVoiceForegroundService::class.java).apply {
                    action = ACTION_START_LIVE
                    putExtra(EXTRA_STATUS_TEXT, statusText)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
                // Gracefully handle background service start restrictions on Android 12+
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, LiveVoiceForegroundService::class.java)
                context.stopService(intent)
            } catch (_: Exception) {
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_LIVE) {
            onStopRequestedFromNotification?.invoke()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val statusText = intent?.getStringExtra(EXTRA_STATUS_TEXT)
            ?: "ULTRON / MAX Live Conversation — Microphone Active"
        val notification = buildNotification(statusText)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (_: Exception) {
            stopSelf()
        }

        return START_STICKY
    }

    private fun buildNotification(statusText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPending = PendingIntent.getActivity(
            this,
            100,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, LiveVoiceForegroundService::class.java).apply {
            action = ACTION_STOP_LIVE
        }
        val stopPending = PendingIntent.getService(
            this,
            101,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("MAX / ULTRON Live Voice Active")
            .setContentText(statusText)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(openPending)
            .addAction(
                android.R.drawable.ic_media_pause,
                "STOP MIC",
                stopPending
            )
            .build()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MAX Live Voice Session",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Persistent microphone session for MAX / ULTRON live conversation"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
