package com.vmenon.mpo.core;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.vmenon.mpo.MPOApplication;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Podcast;
import com.vmenon.mpo.core.receiver.AlarmReceiver;
import com.vmenon.mpo.service.MediaPlayerOmegaService;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @Inject
    protected SubscriptionDao subscriptionDao;

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
                                     final Podcast podcast,
                                     final Episode episode) {

        final Download download = new Download(podcast, episode);

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
            List<Podcast> podcasts = subscriptionDao.notUpdatedInLast(1000 * 60 * 5);
            for (Podcast podcast : podcasts) {
                Log.d("MPO", "Got saved podcast: " + podcast.name + ", " + podcast.id + ", " + podcast.lastEpisodePublished);
                fetchPodcastUpdate(podcast, podcast.lastEpisodePublished);
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

    private void fetchPodcastUpdate(final Podcast podcast, Long lastEpisodePublished) {
        service.getPodcastUpdate(podcast.feedUrl, lastEpisodePublished)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Episode>() {
                    @Override
                    public final void onCompleted() {

                    }

                    @Override
                    public final void onError(Throwable e) {
                        Log.e("Error getting podcasts", e.getMessage());
                    }

                    @Override
                    public final void onNext(Episode episode) {
                        if (episode != null) {
                            Download download = new Download(podcast, episode);
                            downloadManager.queueDownload(download);
                        }

                        podcast.lastUpdate = new Date().getTime();
                        subscriptionDao.save(podcast);
                    }
                });
    }
}
