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
import android.text.TextUtils
import android.util.Log

import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.core.receiver.AlarmReceiver
import com.vmenon.mpo.service.MediaPlayerOmegaService

import java.util.Date

import javax.inject.Inject

import androidx.core.app.NotificationCompat
import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.core.repository.EpisodeRepository
import com.vmenon.mpo.core.repository.ShowRepository
import com.vmenon.mpo.model.EpisodeModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BackgroundService : IntentService(BackgroundService::class.java.name) {


    @Inject
    lateinit var service: MediaPlayerOmegaService

    @Inject
    lateinit var downloadManager: DownloadManager

    @Inject
    lateinit var episodeRepository: EpisodeRepository

    @Inject
    lateinit var showRepository: ShowRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private val subscriptions = CompositeDisposable()

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

    override fun onDestroy() {
        super.onDestroy()
        //subscriptions.clear()
    }

    override fun onHandleIntent(intent: Intent) {
        if (ACTION_UPDATE == intent.action) {
            Log.d("MPO", "Calling update...")
            subscriptions.add(showRepository.notUpdatedInLast((1000 * 60 * 5).toLong())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.main())
                .subscribe(
                    { shows ->
                        for (show in shows) {
                            Log.d(
                                "MPO",
                                "Got saved show: " + show.showDetails.name + ", " + show.showDetails.feedUrl + ", "
                                        + show.lastEpisodePublished
                            )
                            fetchShowUpdate(show, show.lastEpisodePublished)
                        }
                    },
                    { error ->

                    }
                )
            )
        }
    }

    private fun fetchShowUpdate(show: ShowModel, lastEpisodePublished: Long) {
        subscriptions.add(
            service.getPodcastUpdate(show.showDetails.feedUrl, lastEpisodePublished)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { episode ->
                        saveEpisodeAndQueueDownload(show, episode)
                    },
                    { error ->
                        Log.w("MPO", "Error getting show update", error)
                    }
                )
        )
    }

    private fun saveEpisodeAndQueueDownload(show: ShowModel, episode: Episode) {
        if (TextUtils.isEmpty(episode.artworkUrl)) {
            episode.artworkUrl = show.showDetails.artworkUrl
        }

        subscriptions.add(episodeRepository.save(
            EpisodeModel(
                name = episode.name,
                artworkUrl = episode.artworkUrl,
                description = episode.description,
                downloadUrl = episode.downloadUrl,
                filename = "",
                length = episode.length,
                published = episode.published,
                showId = show.id,
                type = episode.type
            )
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { savedEpisode ->
                    downloadManager.queueDownload(show, savedEpisode)
                    show.lastUpdate = Date().time
                    show.lastEpisodePublished = episode.published

                    subscriptions.add(showRepository.save(show)
                        .ignoreElement()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe {

                        }
                    )
                },
                { error ->
                    error.printStackTrace()
                }
            )
        )
    }

    companion object {
        const val ACTION_UPDATE = "com.vmenon.mpo.UPDATE"

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
    }
}
