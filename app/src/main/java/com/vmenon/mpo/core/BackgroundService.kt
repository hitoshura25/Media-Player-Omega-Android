package com.vmenon.mpo.core

import android.app.AlarmManager
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log

import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.api.Show
import com.vmenon.mpo.core.persistence.MPORepository
import com.vmenon.mpo.core.receiver.AlarmReceiver
import com.vmenon.mpo.service.MediaPlayerOmegaService

import org.parceler.Parcels

import java.util.Date

import javax.inject.Inject

import androidx.core.app.NotificationCompat
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BackgroundService : IntentService(BackgroundService::class.java.name) {


    @Inject
    lateinit var service: MediaPlayerOmegaService

    @Inject
    lateinit var downloadManager: DownloadManager

    @Inject
    lateinit var mpoRepository: MPORepository

    override fun onCreate() {
        super.onCreate()
        (application as MPOApplication).appComponent.inject(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "My Background Service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName, NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            startForeground(
                NOTIFICATION_ID, NotificationCompat.Builder(
                    this,
                    NOTIFICATION_CHANNEL_ID
                ).build()
            )
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (ACTION_UPDATE == intent!!.action) {
            Log.d("MPO", "Calling update...")
            val shows = mpoRepository.notUpdatedInLast((1000 * 60 * 5).toLong())
            for (show in shows) {
                Log.d(
                    "MPO", "Got saved show: " + show.name + ", " + show.feedUrl + ", "
                            + show.lastEpisodePublished
                )
                fetchShowUpdate(show, show.lastEpisodePublished)
            }
        } else if (ACTION_DOWNLOAD == intent.action) {
            Log.d("MPO", "Downloading...")
            val download =
                Parcels.unwrap<Download>(intent.getParcelableExtra<Parcelable>(EXTRA_DOWNLOAD))
            /*
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(downloadUrl));
            request.setVisibleInDownloadsUi(false);
            long requestId = downloadManager.enqueue(request);
            Log.d("MPO", "RequestId: " + requestId);*/
            downloadManager.queueDownload(download)
        }
    }

    private fun fetchShowUpdate(show: Show, lastEpisodePublished: Long?) {
        service.getPodcastUpdate(show.feedUrl!!, lastEpisodePublished!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith<Observer<Episode>>(object : Observer<Episode> {
                override fun onSubscribe(@NonNull d: Disposable) {

                }

                override fun onNext(@NonNull episode: Episode) {
                    if (TextUtils.isEmpty(episode.artworkUrl)) {
                        episode.artworkUrl = show.artworkUrl
                    }
                    val download = Download(show, episode)
                    downloadManager.queueDownload(download)
                    show.lastUpdate = Date().time
                    mpoRepository.save(show)
                }

                override fun onError(@NonNull e: Throwable) {
                    Log.w("MPO", "Error getting show update", e)
                }

                override fun onComplete() {

                }
            })
    }

    companion object {
        const val ACTION_UPDATE = "com.vmenon.mpo.UPDATE"
        const val ACTION_DOWNLOAD = "com.vmenon.mpo.DOWNLOAD"

        const val EXTRA_DOWNLOAD = "EXTRA_DOWNLOAD"

        private const val EXEC_INTERVAL = (2 * 60 * 1000).toLong()
        private const val PREFS_NAME = "MPOBackgroundService"
        private const val INITIALIZED_KEY = "Initialized"

        private const val NOTIFICATION_ID = 514
        private const val NOTIFICATION_CHANNEL_ID = "com.vmenon.mpo.BACKGROUND_CHANNEL_ID"

        fun initialize(context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
            if (sharedPreferences.getBoolean(INITIALIZED_KEY, false)) {
                Log.d("MPO", "Service already scheduled, skipping")
            } else {
                setupSchedule(context)
                sharedPreferences.edit().putBoolean(INITIALIZED_KEY, true).apply()
            }
        }

        fun setupSchedule(context: Context) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            alarmIntent.action = ACTION_UPDATE

            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, EXEC_INTERVAL,
                EXEC_INTERVAL, pendingIntent
            )

            Log.d("MPO", "Initialized service")
        }

        fun startDownload(
            context: Context,
            show: Show,
            episode: Episode
        ) {

            val download = Download(show, episode)

            val intent = Intent(context, BackgroundService::class.java)
            intent.action = ACTION_DOWNLOAD
            intent.putExtra(EXTRA_DOWNLOAD, Parcels.wrap(Download::class.java, download))
            context.startService(intent)
        }
    }
}