package com.vmenon.mpo.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

import com.vmenon.mpo.core.BackgroundService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MPO", "In AlarmReceiver")
        val serviceIntent = Intent(context, BackgroundService::class.java)
        serviceIntent.action = BackgroundService.ACTION_UPDATE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
