package com.vmenon.mpo.core;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.vmenon.mpo.MPOApplication;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.persistence.MPORepository;
import com.vmenon.mpo.core.receiver.AlarmReceiver;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BackgroundService extends IntentService {
    public static final String ACTION_UPDATE = "com.vmenon.mpo.UPDATE";
    public static final String ACTION_DOWNLOAD = "com.vmenon.mpo.DOWNLOAD";

    public static final String EXTRA_DOWNLOAD = "EXTRA_DOWNLOAD";

    private static final long EXEC_INTERVAL = 2 * 60 * 1000;
    private static final String PREFS_NAME = "MPOBackgroundService";
    private static final String INITIALIZED_KEY = "Initialized";

    private static final int NOTIFICATION_ID = 514;
    private static final String NOTIFICATION_CHANNEL_ID = "com.vmenon.mpo.BACKGROUND_CHANNEL_ID";


    @Inject
    protected MediaPlayerOmegaService service;

    @Inject
    protected DownloadManager downloadManager;

    @Inject
    protected MPORepository mpoRepository;

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
                                     final Show show,
                                     final Episode episode) {

        final Download download = new Download(show, episode);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            startForeground(NOTIFICATION_ID, new NotificationCompat.Builder(this,
                    NOTIFICATION_CHANNEL_ID).build());
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_UPDATE.equals(intent.getAction())) {
            Log.d("MPO", "Calling update...");
            List<Show> shows = mpoRepository.notUpdatedInLast(1000 * 60 * 5);
            for (Show show : shows) {
                Log.d("MPO", "Got saved show: " + show.name +  ", " + show.feedUrl + ", "
                        + show.lastEpisodePublished);
                fetchShowUpdate(show, show.lastEpisodePublished);
            }
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

    private void fetchShowUpdate(final Show show, Long lastEpisodePublished) {
        service.getPodcastUpdate(show.feedUrl, lastEpisodePublished)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Episode>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Episode episode) {
                        if (TextUtils.isEmpty(episode.artworkUrl)) {
                            episode.artworkUrl = show.artworkUrl;
                        }
                        Download download = new Download(show, episode);
                        downloadManager.queueDownload(download);
                        show.lastUpdate = new Date().getTime();
                        mpoRepository.save(show);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.w("MPO", "Error getting show update", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
