package com.vmenon.mpo.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import com.vmenon.mpo.core.BackgroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MPO", "In BootReceiver")
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            BackgroundService.setupSchedule(context)
        }
    }
}
