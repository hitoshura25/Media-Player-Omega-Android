package com.vmenon.mpo.player.framework

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log

import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.vmenon.mpo.player.domain.PlaybackMediaRequest

import java.io.File
import java.lang.ref.WeakReference
import java.util.ArrayList

class MPOMediaBrowserService : MediaBrowserServiceCompat(), MPOPlayer.MediaPlayerListener,
    AudioManager.OnAudioFocusChangeListener {

    data class Configuration(
        val player: MPOPlayer,
        val playerUiIntentCreator: (PlaybackMediaRequest?) -> Intent,
        val notificationBuilderProcessor: (NotificationCompat.Builder) -> Unit
    )

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var audioManager: AudioManager
    private lateinit var notificationManager: NotificationManager

    private var serviceStarted = false
    private var notificationStarted = false
    private var audioFocus =
        AUDIO_NO_FOCUS_NO_DUCK
    private var playbackState: Int = 0
    private var playOnFocusGain: Boolean = false
    private var audioNoisyReceiverRegistered: Boolean = false
    private var requestedMedia: PlaybackMediaRequest? = null
    private var currentMediaBitmap: Bitmap? = null
    private var placeholderMediaBitmap: Bitmap? = null

    private var playIntent: PendingIntent? = null
    private var pauseIntent: PendingIntent? = null
    private var previousIntent: PendingIntent? = null
    private var nextIntent: PendingIntent? = null
    private var stopIntent: PendingIntent? = null

    private val delayedStopHandler =
        DelayedStopHandler(
            this
        )
    private val audioNoisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val audioNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                Log.d(TAG, "Headphones disconnected.")
                if (playOnFocusGain && configuration.player.isPlaying) {
                    val i = Intent(context, MPOMediaBrowserService::class.java)
                    i.action =
                        ACTION_CMD
                    i.putExtra(
                        CMD_NAME,
                        CMD_PAUSE
                    )
                    startService(i)
                }
            }
        }
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val transportControls = mediaSession.controller
                .transportControls
            Log.d(TAG, "Received intent with action $action")
            when (action) {
                NOTIFICATION_ACTION_PAUSE -> transportControls.pause()
                NOTIFICATION_ACTION_PLAY -> transportControls.play()
                NOTIFICATION_ACTION_NEXT -> transportControls.skipToNext()
                NOTIFICATION_ACTION_PREV -> transportControls.skipToPrevious()
                else -> Log.w(TAG, "Unknown intent ignored. Action=$action")
            }
        }
    }

    private val availableActions: Long
        get() {
            var actions =
                PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
            if (configuration.player.isPlaying) {
                actions = actions or PlaybackStateCompat.ACTION_PAUSE
            }

            return actions
        }

    override fun onCreate() {
        super.onCreate()
        playbackState = PlaybackStateCompat.STATE_NONE
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pkg = packageName
        pauseIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_REQUEST_CODE,
            Intent(NOTIFICATION_ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT
        )
        playIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_REQUEST_CODE,
            Intent(NOTIFICATION_ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT
        )
        previousIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_REQUEST_CODE,
            Intent(NOTIFICATION_ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT
        )
        nextIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_REQUEST_CODE,
            Intent(NOTIFICATION_ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT
        )
        stopIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_REQUEST_CODE,
            Intent(NOTIFICATION_ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll()

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        /*wifiLock = ((WifiManager) service.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "sample_lock");*/

        mediaSession = MediaSessionCompat(
            this,
            TAG
        )
        stateBuilder = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
        )
        mediaSession.setPlaybackState(stateBuilder.build())
        mediaSession.setCallback(SessionCallback())
        sessionToken = mediaSession.sessionToken
        configuration.player.setListener(this)

        val context = applicationContext
        val intent = configuration.playerUiIntentCreator(null)
        val pi = PendingIntent.getActivity(
            context, 99 /*request code*/,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        mediaSession.setSessionActivity(pi)
        updatePlaybackState(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            val command = intent.getStringExtra(CMD_NAME)
            if (ACTION_CMD == action) {
                if (CMD_PAUSE == command) {
                    if (configuration.player.isPlaying) {
                        handlePauseRequest()
                    }
                }
            }
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        // Service is being killed, so make sure we release our resources
        handleStopRequest(null)
        stopNotification()
        delayedStopHandler.removeCallbacksAndMessages(null)
        // Always release the MediaSession to clean up resources
        // and notify associated MediaController(s).
        mediaSession.release()
        configuration.player.setListener(null)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (allowBrowsing(clientPackageName, clientUid)) {
            BrowserRoot(MEDIA_ROOT_ID, null)
        } else {
            BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        if (TextUtils.equals(EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null)
            return
        }

        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()

        // Check if this is the root menu:
        if (MEDIA_ROOT_ID == parentId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems)

    }

    override fun onMediaPrepared() {
        configMediaPlayerState()
    }

    override fun onMediaFinished() {
        handleStopRequest(null)
    }

    override fun onMediaSeekFinished() {
        if (playbackState == PlaybackStateCompat.STATE_BUFFERING) {
            playbackState = PlaybackStateCompat.STATE_PLAYING
        }
        updatePlaybackState(null)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        Log.d(TAG, "onAudioFocusChange. focusChange=$focusChange")
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // We have gained focus:
            audioFocus =
                AUDIO_FOCUSED

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
        ) {
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            val canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
            audioFocus = if (canDuck) AUDIO_NO_FOCUS_CAN_DUCK else AUDIO_NO_FOCUS_NO_DUCK

            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (playbackState == PlaybackStateCompat.STATE_PLAYING && !canDuck) {
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                playOnFocusGain = true
            }
        } else {
            Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: $focusChange")
        }
        configMediaPlayerState()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        return false
    }

    private fun handlePlayRequest(mediaFile: File?) {
        delayedStopHandler.removeCallbacksAndMessages(null)

        if (!serviceStarted) {
            Log.v(TAG, "Starting service")
            // The MusicService needs to keep running even after the calling MediaBrowser
            // is disconnected. Call startService(Intent) and then stopSelf(..) when we no longer
            // need to play media.
            startService(Intent(applicationContext, MPOMediaBrowserService::class.java))
            serviceStarted = true
        }

        if (!mediaSession.isActive) {
            mediaSession.isActive = true
        }

        playOnFocusGain = true
        tryToGetAudioFocus()
        registerAudioNoisyReceiver()
        val mediaHasChanged = mediaFile != null

        if (playbackState == PlaybackStateCompat.STATE_PAUSED && !mediaHasChanged) {
            configMediaPlayerState()
        } else {
            playbackState = PlaybackStateCompat.STATE_STOPPED
            relaxResources(false) // release everything except MediaPlayer

            if (mediaFile != null) {
                playbackState = PlaybackStateCompat.STATE_BUFFERING
                configuration.player.prepareForPlayback(mediaFile)
            } else {
                configMediaPlayerState()
            }

            // If we are streaming from the internet, we want to hold a
            // Wifi lock, which prevents the Wifi radio from going to
            // sleep while the song is playing.
            //mWifiLock.acquire();

            updatePlaybackState(null)
        }
    }

    private fun updateNotification() {
        if (!notificationStarted) {
            // The notification must be updated after setting started to true
            val notification = createNotification()
            if (notification != null) {
                val filter = IntentFilter()
                filter.addAction(NOTIFICATION_ACTION_NEXT)
                filter.addAction(NOTIFICATION_ACTION_PAUSE)
                filter.addAction(NOTIFICATION_ACTION_PLAY)
                filter.addAction(NOTIFICATION_ACTION_PREV)
                registerReceiver(notificationReceiver, filter)

                startForeground(NOTIFICATION_ID, notification)
                notificationStarted = true
            }
        } else {
            val notification = createNotification()
            if (notification != null) {
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun stopNotification() {
        if (notificationStarted) {
            notificationStarted = false
            try {
                notificationManager.cancel(NOTIFICATION_ID)
                unregisterReceiver(notificationReceiver)
            } catch (ex: IllegalArgumentException) {
                // ignore if the receiver is not registered.
            }

            stopForeground(true)
        }
    }

    private fun handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=$playbackState")
        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            // Pause media player and cancel the 'foreground service' state.
            if (configuration.player.isPlaying) {
                configuration.player.pause()
            }
            // while paused, retain the MediaPlayer but give up audio focus
            relaxResources(false)
            giveUpAudioFocus()
        }
        playbackState = PlaybackStateCompat.STATE_PAUSED
        updatePlaybackState(null)
        unregisterAudioNoisyReceiver()
        // reset the delayed stop handler.
        delayedStopHandler.removeCallbacksAndMessages(null)
        delayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY.toLong())
    }

    private fun handleStopRequest(withError: String?) {
        Log.d(TAG, "handleStopRequest: mState=$playbackState error=$withError")
        configuration.player.stop()
        playbackState = PlaybackStateCompat.STATE_STOPPED
        // Give up Audio focus
        giveUpAudioFocus()
        unregisterAudioNoisyReceiver()
        // Relax all resources
        relaxResources(true)

        /*if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }*/

        // reset the delayed stop handler.
        delayedStopHandler.removeCallbacksAndMessages(null)
        delayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY.toLong())

        updatePlaybackState(withError)

        // service is no longer necessary. Will be started again if needed.
        stopSelf()
        serviceStarted = false
    }

    private fun updatePlaybackState(error: String?) {
        Log.d(TAG, "updatePlaybackState")
        val position = configuration.player.getCurrentPosition()
        var state = playbackState
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(availableActions)

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR, error)
            state = PlaybackStateCompat.STATE_ERROR
        }
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime())
        mediaSession.setPlaybackState(stateBuilder.build())

        if (playbackState == PlaybackStateCompat.STATE_STOPPED || playbackState == PlaybackStateCompat.STATE_NONE) {
            stopNotification()
        } else {
            updateNotification()
        }
    }

    private fun configMediaPlayerState() {
        Log.d("MPO", "configMediaPlayerState. mAudioFocus=$audioFocus")
        if (audioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                handlePauseRequest()
            }
        } else {  // we have audio focus:
            if (audioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                configuration.player.setVolume(VOLUME_DUCK) // we'll be relatively quiet
            } else {
                configuration.player.setVolume(VOLUME_NORMAL) // we can be loud again
            }
            // If we were playing when we lost focus, we need to resume playing.
            if (playOnFocusGain) {
                if (!configuration.player.isPlaying) {
                    val currentPosition = configuration.player.getCurrentPosition()
                    Log.d(
                        "MPO",
                        "configMediaPlayerState startMediaPlayer. seeking to $currentPosition"
                    )
                    playbackState =
                        if (currentPosition == configuration.player.getCurrentPosition()) {
                            configuration.player.play()
                            PlaybackStateCompat.STATE_PLAYING
                        } else {
                            configuration.player.seekTo(currentPosition)
                            PlaybackStateCompat.STATE_BUFFERING
                        }
                }
                playOnFocusGain = false
            }
        }
        updatePlaybackState(null)
    }

    private fun tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus")
        if (audioFocus != AUDIO_FOCUSED) {
            @Suppress("DEPRECATION")
            val result = audioManager.requestAudioFocus(
                this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                audioFocus =
                    AUDIO_FOCUSED
            }
        }
    }

    private fun giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus")
        if (audioFocus == AUDIO_FOCUSED) {
            @Suppress("DEPRECATION")
            if (audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                audioFocus =
                    AUDIO_NO_FOCUS_NO_DUCK
            }
        }
    }

    private fun relaxResources(releaseMediaPlayer: Boolean) {
        Log.d(TAG, "relaxResources. releaseMediaPlayer=$releaseMediaPlayer")

        stopForeground(true)

        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer) {
            configuration.player.cleanup()
        }

        // we can also release the Wifi lock, if we're holding it
        /*if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }*/
    }

    private fun registerAudioNoisyReceiver() {
        if (!audioNoisyReceiverRegistered) {
            registerReceiver(audioNoisyReceiver, audioNoisyIntentFilter)
            audioNoisyReceiverRegistered = true
        }
    }

    private fun unregisterAudioNoisyReceiver() {
        if (audioNoisyReceiverRegistered) {
            unregisterReceiver(audioNoisyReceiver)
            audioNoisyReceiverRegistered = false
        }
    }

    private fun playMedia(request: PlaybackMediaRequest) {
        if (requestedMedia == request) {
            request.mediaFile?.let { filename ->
                val mediaFile = File(filename)
                val metadata = MediaMetadataCompat.Builder().putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_ID, request.media.mediaId
                )
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, request.media.album)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, request.media.author)
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        request.media.durationInMillis
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        TextUtils.join(" ", request.media.genres ?: emptyList<String>())
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        request.media.artworkUrl
                    )
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, request.media.title)
                    .build()
                handlePlayRequest(mediaFile)
                mediaSession.setMetadata(metadata)
            }
        } else {
            Log.w("MPO", "Cannot play incorrect media request: ${request.media.mediaId}")
            return
        }
    }

    private fun createNotification(): Notification? {
        val mediaMetadata = mediaSession.controller.metadata ?: return null
        Log.d(TAG, "updateNotificationMetadata. mMetadata=$mediaMetadata")

        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )

        configuration.notificationBuilderProcessor(notificationBuilder)

        val playPauseButtonPosition = addNotificationActions(notificationBuilder)
        notificationBuilder
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    // show only play/pause in compact view
                    .setShowActionsInCompactView(playPauseButtonPosition)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(stopIntent)
                    .setMediaSession(sessionToken)
            )
            .setDeleteIntent(stopIntent)
            .setSmallIcon(R.drawable.ic_headset)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setContentIntent(createNotificationContentIntent())
            .setContentTitle(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))

        setNotificationPlaybackState(notificationBuilder)

        val fetchArtUrl = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)

        if (currentMediaBitmap != null) {
            notificationBuilder.setLargeIcon(currentMediaBitmap)
        } else {
            if (placeholderMediaBitmap == null) {
                placeholderMediaBitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.ic_headset)
            }
            notificationBuilder.setLargeIcon(placeholderMediaBitmap)
            if (fetchArtUrl != null) {
                Glide.with(this).load(fetchArtUrl).asBitmap().into(
                    ArtworkTarget(
                        this, fetchArtUrl,
                        notificationBuilder
                    )
                )
            }
        }

        return notificationBuilder.build()
    }

    private fun setNotificationPlaybackState(builder: NotificationCompat.Builder) {
        Log.d(TAG, "updateNotificationPlaybackState. mPlaybackState=$playbackState")
        if (!notificationStarted) {
            Log.d(TAG, "updateNotificationPlaybackState. cancelling notification!")
            stopForeground(true)
            return
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(playbackState == PlaybackStateCompat.STATE_PLAYING)
    }

    private fun createNotificationContentIntent(): PendingIntent {
        val openUI = configuration.playerUiIntentCreator(requestedMedia)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            this,
            NOTIFICATION_REQUEST_CODE, openUI,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun addNotificationActions(notificationBuilder: NotificationCompat.Builder): Int {
        Log.d(TAG, "updatePlayPauseAction")

        val playPauseButtonPosition = 0
        /* TODO: Previous
        // If skip to previous action is enabled
        if ((getAvailableActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_previous_white_24dp,
                    getString(R.string.label_previous), mPreviousIntent);

            // If there is a "skip to previous" button, the play/pause button will
            // be the second one. We need to keep track of it, because the MediaStyle notification
            // requires to specify the index of the buttons (actions) that should be visible
            // when in compact view.
            playPauseButtonPosition = 1;
        }
        */

        // Play or pause button, depending on the current state.
        val label: String
        val icon: Int
        val intent: PendingIntent?
        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            label = getString(R.string.pause)
            icon = R.drawable.ic_pause_circle_filled_white_48dp
            intent = pauseIntent
        } else {
            label = getString(R.string.play)
            icon = R.drawable.ic_play_circle_filled_white_48dp
            intent = playIntent
        }
        notificationBuilder.addAction(NotificationCompat.Action(icon, label, intent))

        /* TODO: Prev/Next
        // If skip to next action is enabled
        if ((getAvailableActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_next_white_24dp,
                    mService.getString(R.string.label_next), mNextIntent);
        }
        */

        return playPauseButtonPosition
    }

    private fun handleNotificationArtwork(
        artworkUrl: String, bitmap: Bitmap,
        notificationBuilder: NotificationCompat.Builder
    ) {
        val mediaMetadata = mediaSession.controller.metadata
        val currentArtworkUrl =
            mediaMetadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)

        if (currentArtworkUrl != null && currentArtworkUrl == artworkUrl) {
            // If the media is still the same, update the notification:
            Log.d(TAG, "handleNotificationArtwork: set bitmap to $artworkUrl")
            currentMediaBitmap = bitmap
            notificationBuilder.setLargeIcon(bitmap)
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.description = getString(R.string.notification_channel_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private inner class SessionCallback : MediaSessionCompat.Callback() {

        override fun onPlay() {
            handlePlayRequest(null)
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            val playbackMediaRequest =
                extras?.getSerializable(PLAYBACK_MEDIA_REQUEST_EXTRA) as? PlaybackMediaRequest

            if (requestedMedia == playbackMediaRequest &&
                (playbackState == PlaybackStateCompat.STATE_PLAYING
                        || playbackState == PlaybackStateCompat.STATE_BUFFERING)
            ) {
                updatePlaybackState(null)
                return
            }

            playbackMediaRequest?.let { request ->
                requestedMedia = request
                currentMediaBitmap = null
                playMedia(request)
            }
        }

        override fun onPause() {
            handlePauseRequest()
        }

        override fun onStop() {
            handleStopRequest(null)
        }

        override fun onSeekTo(pos: Long) {
            configuration.player.seekTo(pos)
            if (configuration.player.isPlaying) {
                playbackState = PlaybackStateCompat.STATE_BUFFERING
            }
            updatePlaybackState(null)
        }
    }

    class DelayedStopHandler(service: MPOMediaBrowserService) : Handler(Looper.getMainLooper()) {
        private val weakRefService: WeakReference<MPOMediaBrowserService> = WeakReference(service)

        override fun handleMessage(msg: Message) {
            val service = weakRefService.get()
            if (service != null) {
                if (configuration.player.isPlaying) {
                    Log.d(TAG, "Ignoring delayed stop since the media player is in use.")
                    return
                }
                Log.d(TAG, "Stopping service with delay handler.")
                service.stopSelf()
                service.serviceStarted = false
            }
        }
    }


    private class ArtworkTarget(
        service: MPOMediaBrowserService,
        var artworkUrl: String,
        var notificationBuilder: NotificationCompat.Builder
    ) : SimpleTarget<Bitmap>(500, 500) {
        var serviceRef: WeakReference<MPOMediaBrowserService> = WeakReference(service)

        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
            val service = serviceRef.get()
            service?.handleNotificationArtwork(artworkUrl, resource, notificationBuilder)
        }
    }

    companion object {

        /**
         * Not very elegant, but want to keep this service in the framework layer as much as
         * possible, and not put it into the presentation layer. This allows customization via the
         * [com.vmenon.mpo.player.framework.AndroidMediaBrowserServicePlayerEngine], which is otherwise
         * difficult to do as a [android.app.Service] need to have no constructors as it's created
         * by the platform
         */
        lateinit var configuration: Configuration

        const val PLAYBACK_MEDIA_REQUEST_EXTRA = "playbackMediaRequest"

        // The action of the incoming Intent indicating that it contains a command
        // to be executed (see {@link #onStartCommand})
        const val ACTION_CMD = "com.vmenon.mpo.core.ACTION_CMD"

        // The key in the extras of the incoming Intent indicating the command that
        // should be executed (see {@link #onStartCommand})
        const val CMD_NAME = "CMD_NAME"

        // A value of a CMD_NAME key in the extras of the incoming Intent that
        // indicates that the music playback should be paused (see {@link #onStartCommand})
        const val CMD_PAUSE = "CMD_PAUSE"

        // we don't have audio focus, and can't duck (play at a low volume)
        const val AUDIO_NO_FOCUS_NO_DUCK = 0

        // we don't have focus, but can duck (play at a low volume)
        const val AUDIO_NO_FOCUS_CAN_DUCK = 1

        // we have full audio focus
        const val AUDIO_FOCUSED = 2

        // The volume we set the media player to when we lose audio focus, but are
        // allowed to reduce the volume instead of stopping playback.
        const val VOLUME_DUCK = 0.2f

        // The volume we set the media player when we have audio focus.
        const val VOLUME_NORMAL = 1.0f

        // Delay stopSelf by using a handler.
        private const val STOP_DELAY = 30000

        private const val TAG = "MPOMediaService"
        private const val MEDIA_ROOT_ID = "com.vmenon.mpo.media_root_id"
        private const val EMPTY_MEDIA_ROOT_ID = "com.vmenon.mpo.empty_root_id"

        private const val NOTIFICATION_ID = 414
        private const val NOTIFICATION_CHANNEL_ID = "com.vmenon.mpo.MUSIC_CHANNEL_ID"
        private const val NOTIFICATION_REQUEST_CODE = 100
        private const val NOTIFICATION_ACTION_PAUSE = "com.vmenon.mpo.pause"
        private const val NOTIFICATION_ACTION_PLAY = "com.vmenon.mpo.play"
        private const val NOTIFICATION_ACTION_PREV = "com.vmenon.mpo.prev"
        private const val NOTIFICATION_ACTION_NEXT = "com.vmenon.mpo.next"
        private const val NOTIFICATION_ACTION_STOP = "com.vmenon.mpo.stop"
    }
}
