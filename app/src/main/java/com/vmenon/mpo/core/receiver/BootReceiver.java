package com.vmenon.mpo.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vmenon.mpo.core.BackgroundService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MPO", "In BootReceiver");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            BackgroundService.setupSchedule(context);
        }
    }
}
