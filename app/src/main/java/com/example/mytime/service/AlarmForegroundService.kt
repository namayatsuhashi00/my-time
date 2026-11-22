package com.example.mytime.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.mytime.audio.AlarmPlayer
import com.example.mytime.domain.model.AudioOutputMode

class AlarmForegroundService : Service() {

    private lateinit var alarmPlayer: AlarmPlayer

    override fun onCreate() {
        super.onCreate()
        alarmPlayer = AlarmPlayer(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_ALARM) {
            alarmPlayer.stopRing()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val soundUriStr = intent?.getStringExtra(EXTRA_SOUND_URI)
        val soundUri = soundUriStr?.let { Uri.parse(it) }
        val modeName = intent?.getStringExtra(EXTRA_AUDIO_MODE) ?: AudioOutputMode.SPEAKER_ONLY.name
        val audioMode = AudioOutputMode.valueOf(modeName)
        val isVib = intent?.getBooleanExtra(EXTRA_VIBRATION, true) ?: true

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My Time アラーム")
            .setContentText("アラームが鳴動しています")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        alarmPlayer.startRing(soundUri, audioMode, isVib)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Service Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My Time アラーム発火用の通知チャンネル"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "alarm_service_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_ALARM = "com.example.mytime.ACTION_STOP_ALARM"
        const val EXTRA_SOUND_URI = "extra_sound_uri"
        const val EXTRA_AUDIO_MODE = "extra_audio_mode"
        const val EXTRA_VIBRATION = "extra_vibration"
    }
}