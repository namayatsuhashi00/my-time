package com.example.mytime.audio

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import com.example.mytime.domain.model.AudioOutputMode

class AudioRoutingManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun configureAudioOutput(mediaPlayer: MediaPlayer, mode: AudioOutputMode) {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        when (mode) {
            AudioOutputMode.SPEAKER_ONLY -> {
                val speakerDevice = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
                speakerDevice?.let { mediaPlayer.setPreferredDevice(it) }
            }
            AudioOutputMode.BLUETOOTH_PREFERRED -> {
                val bluetoothDevice = devices.firstOrNull {
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                            it.type == AudioDeviceInfo.TYPE_BLE_HEADSET
                }
                if (bluetoothDevice != null) {
                    mediaPlayer.setPreferredDevice(bluetoothDevice)
                } else {
                    val speakerDevice = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
                    speakerDevice?.let { mediaPlayer.setPreferredDevice(it) }
                }
            }
        }
    }
}