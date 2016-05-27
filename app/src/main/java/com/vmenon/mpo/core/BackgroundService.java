package com.vmenon.mpo.core;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.vmenon.mpo.MPOApplication;
import com.vmenon.mpo.core.receiver.AlarmReceiver;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import org.parceler.Parcels;

import javax.inject.Inject;

public class BackgroundService extends IntentService {
    public static final String ACTION_UPDATE = "com.vmenon.mpo.UPDATE";
    public static final String ACTION_DOWNLOAD = "com.vmenon.mpo.DOWNLOAD";

    public static final String EXTRA_DOWNLOAD = "EXTRA_DOWNLOAD";

    private static final long EXEC_INTERVAL = 2 * 60 * 1000;
    private static final String PREFS_NAME = "MPOBackgroundService";
    private static final String INITIALIZED_KEY = "Initialized";

    @Inject
    protected MediaPlayerOmegaService service;

    @Inject
    protected DownloadManager downloadManager;

    public static void initialize(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        if (sharedPreferences.getBoolean(INITIALIZED_KEY, false)) {
            Log.d("MPO", "Service already scheduled, skipping");
        } else {
            setupSchedule(context);
            sharedPreferences.edit().putBoolean(INITIALIZED_KEY, true).commit();
        }
    }

    public static void setupSchedule(final Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(ACTION_UPDATE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, EXEC_INTERVAL,
                EXEC_INTERVAL, pendingIntent);

        Log.d("MPO", "Initialized service");
    }

    public static void startDownload(final Context context,
                                     final String showName,
                                     final String episodeName,
                                     final String url) {

        final Download download = new Download(showName, episodeName, url);

        Intent intent = new Intent(context, BackgroundService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_DOWNLOAD, Parcels.wrap(Download.class, download));
        context.startService(intent);
    }

    public BackgroundService() {
        super(BackgroundService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MPOApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_UPDATE.equals(intent.getAction())) {
            Log.d("MPO", "Calling update...");
        } else if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            Log.d("MPO", "Downloading...");
            final Download download = Parcels.unwrap(intent.getParcelableExtra(EXTRA_DOWNLOAD));
            /*
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(downloadUrl));
            request.setVisibleInDownloadsUi(false);
            long requestId = downloadManager.enqueue(request);
            Log.d("MPO", "RequestId: " + requestId);*/
            downloadManager.queueDownload(download);
        }
    }
}
