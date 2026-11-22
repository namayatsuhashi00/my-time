package com.example.mytime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mytime.service.AlarmForegroundService

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtras(intent)
        }
        context.startForegroundService(serviceIntent)
    }
}