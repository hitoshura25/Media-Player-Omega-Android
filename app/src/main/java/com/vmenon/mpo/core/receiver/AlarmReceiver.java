package com.vmenon.mpo.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vmenon.mpo.core.BackgroundService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MPO", "In AlarmReceiver");
        Intent serviceIntent = new Intent(context, BackgroundService.class);
        serviceIntent.setAction(BackgroundService.ACTION_UPDATE);
        context.startService(serviceIntent);
    }
}
