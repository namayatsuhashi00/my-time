package com.example.mytime.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.mytime.domain.model.AudioOutputMode

class AlarmPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val audioRoutingManager = AudioRoutingManager(context)

    fun startRing(
        soundUri: Uri?,
        mode: AudioOutputMode,
        isVibrationEnabled: Boolean
    ) {
        stopRing()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            isLooping = true
            if (soundUri != null) {
                setDataSource(context, soundUri)
            } else {
                val defaultUri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
                setDataSource(context, defaultUri)
            }
            prepare()
        }

        mediaPlayer?.let { player ->
            audioRoutingManager.configureAudioOutput(player, mode)
            player.start()
        }

        if (isVibrationEnabled) {
            startVibration()
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 500, 1000)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    fun stopRing() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null
    }
}