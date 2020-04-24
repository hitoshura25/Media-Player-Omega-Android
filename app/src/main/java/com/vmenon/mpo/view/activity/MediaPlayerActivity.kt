package com.vmenon.mpo.view.activity

import android.content.ComponentName
import android.content.Intent
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar

import com.bumptech.glide.Glide
import com.vmenom.mpo.model.EpisodeModel
import com.vmenon.mpo.Constants
import com.vmenon.mpo.R
import com.vmenon.mpo.core.MPOMediaService
import com.vmenon.mpo.player.MPOPlayer
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.player.MPOPlayer.VideoSizeListener
import com.vmenon.mpo.util.MediaHelper
import com.vmenon.mpo.viewmodel.EpisodeDetailsViewModel
import kotlinx.android.synthetic.main.activity_media_player.*

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

import javax.inject.Inject

class MediaPlayerActivity : BaseActivity(), SurfaceHolder.Callback, VideoSizeListener {
    @Inject
    lateinit var player: MPOPlayer

    @Inject
    lateinit var viewModel: EpisodeDetailsViewModel

    private val handler = Handler()
    private lateinit var episodeWithShowDetails: EpisodeModel
    private lateinit var mediaBrowser: MediaBrowserCompat
    private var playbackState: PlaybackStateCompat? = null
    private var playOnStart = false
    private var fromNotification = false
    private var requestedMediaId: String? = null

    private val updateProgressTask = Runnable { updateProgress() }

    private val executorService = Executors.newSingleThreadScheduledExecutor()
    private var scheduleFuture: ScheduledFuture<*>? = null

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            val token = mediaBrowser.sessionToken
            try {
                var currentlyPlayingMediaId = ""
                val mediaController = MediaControllerCompat(
                    this@MediaPlayerActivity, token
                )
                MediaControllerCompat.setMediaController(this@MediaPlayerActivity, mediaController)
                mediaController.registerCallback(controllerCallback)
                val playbackState = mediaController.playbackState
                val currentlyPlaying =
                    playbackState != null && (playbackState.state == PlaybackStateCompat.STATE_PLAYING || playbackState.state == PlaybackStateCompat.STATE_BUFFERING)

                val metadata = mediaController.metadata
                if (metadata != null) {
                    currentlyPlayingMediaId =
                        metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                    if (requestedMediaId == currentlyPlayingMediaId) {
                        updateDuration(metadata)
                    }
                }

                if (requestedMediaId == currentlyPlayingMediaId) {
                    updateMediaDisplay()
                    updatePlaybackState(playbackState)
                    updateProgress()
                    if (currentlyPlaying) {
                        scheduleSeekbarUpdate()

                        // Force playing if from notification to trigger callback
                        playOnStart = playOnStart || fromNotification
                    }
                }

                if (fromNotification) {
                    requestedMediaId?.let {
                        val mediaType = MediaHelper.getMediaTypeFromMediaId(it)
                        when (mediaType?.mediaType) {
                            MediaHelper.MEDIA_TYPE_EPISODE -> {
                                subscriptions.add(
                                    viewModel.getEpisodeDetails(mediaType.id)
                                        .firstElement()
                                        .subscribe(
                                            { episode ->
                                                this@MediaPlayerActivity.episodeWithShowDetails = episode
                                                updateUIFromMedia()
                                            },
                                            { error -> }
                                        )
                                )
                            }
                            else -> {
                            }
                        }
                    }
                }

                if (playOnStart) {
                    mediaController.transportControls.playFromMediaId(requestedMediaId, null)
                    playOnStart = false
                }

            } catch (e: RemoteException) {
                Log.w("MPO", "Error creating mediaController", e)
            }

        }
    }

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            if (metadata != null) {
                updateDuration(metadata)
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            updatePlaybackState(state)
        }
    }

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)
        if (intent.hasExtra(EXTRA_NOTIFICATION_MEDIA_ID)) {
            fromNotification = true
            requestedMediaId = intent.getStringExtra(EXTRA_NOTIFICATION_MEDIA_ID)
        }

        if (savedInstanceState != null) {
            requestedMediaId = savedInstanceState.getString(EXTRA_MEDIA_ID)
        }

        playOnStart = savedInstanceState == null

        actionButton.setOnClickListener {
            val mediaController = MediaControllerCompat.getMediaController(
                this@MediaPlayerActivity
            )
            val transportControls = mediaController.transportControls
            when (mediaController.playbackState.state) {
                PlaybackStateCompat.STATE_BUFFERING, PlaybackStateCompat.STATE_PLAYING -> {
                    transportControls.pause()
                    stopSeekbarUpdate()
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    transportControls.play()
                    scheduleSeekbarUpdate()
                }
                PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.STATE_NONE -> transportControls.playFromMediaId(
                    requestedMediaId,
                    null
                )
            }
        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                position.text = DateUtils.formatElapsedTime(seekBar.progress.toLong())
                remaining.text =
                    "-" + DateUtils.formatElapsedTime((seekBar.max - seekBar.progress).toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                stopSeekbarUpdate()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MediaControllerCompat.getMediaController(this@MediaPlayerActivity).transportControls
                    .seekTo((seekBar.progress * 1000).toLong())
                scheduleSeekbarUpdate()
            }
        })

        replayButton.setOnClickListener { handleSkipOrReplay(Constants.REPLAY_DURATION) }
        skipButton.setOnClickListener { handleSkipOrReplay(Constants.SKIP_DURATION) }

        surfaceView.holder.addCallback(this)

        mediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, MPOMediaService::class.java),
            connectionCallback,
            null
        ) // optional Bundle

        player.setVideoSizeListener(this@MediaPlayerActivity)
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
        if (!fromNotification) {
            val episodeId = intent.getLongExtra(EXTRA_EPISODE, -1L)
            subscriptions.add(
                viewModel.getEpisodeDetails(episodeId)
                    .firstElement()
                    .subscribe(
                        { episodeWithShowDetails ->
                            this@MediaPlayerActivity.episodeWithShowDetails = episodeWithShowDetails
                            requestedMediaId = MediaHelper.createMediaId(episodeWithShowDetails)
                            updateUIFromMedia()
                        },
                        { error ->

                        }
                    )
            )
        }
    }

    override fun onStop() {
        super.onStop()
        if (MediaControllerCompat.getMediaController(this@MediaPlayerActivity) != null) {
            MediaControllerCompat.getMediaController(this@MediaPlayerActivity)
                .unregisterCallback(controllerCallback)
        }

        mediaBrowser.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSeekbarUpdate()
        executorService.shutdown()
        player.setVideoSizeListener(null)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(EXTRA_NOTIFICATION_MEDIA_ID)) {
            fromNotification = true
            requestedMediaId = intent.getStringExtra(EXTRA_NOTIFICATION_MEDIA_ID)
            intent.removeExtra(EXTRA_NOTIFICATION_MEDIA_ID)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_MEDIA_ID, requestedMediaId)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        player.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        player.setDisplay(null)
        surfaceView.visibility = View.GONE
    }

    override fun onMediaVideoSizeDetermined(width: Int, height: Int) {
        updateMediaDisplay()
    }

    private fun updateUIFromMedia() {
        Glide.with(this)
            .load(episodeWithShowDetails.artworkUrl)
            .fitCenter()
            .into(artworkImage!!)
        mediaTitle.text = episodeWithShowDetails.name
    }

    private fun scheduleSeekbarUpdate() {
        stopSeekbarUpdate()
        if (!executorService.isShutdown) {
            scheduleFuture = executorService.scheduleAtFixedRate(
                { handler.post(updateProgressTask) },
                PROGRESS_UPDATE_INITIAL_INTERVAL,
                PROGRESS_UPDATE_INTERNAL,
                TimeUnit.MILLISECONDS
            )
        }
    }

    private fun stopSeekbarUpdate() {
        scheduleFuture?.cancel(false)
    }

    private fun updatePlaybackState(state: PlaybackStateCompat?) {
        if (state == null) {
            return
        }
        playbackState = state

        when (state.state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                actionButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp)
                scheduleSeekbarUpdate()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
                stopSeekbarUpdate()
            }
            PlaybackStateCompat.STATE_NONE, PlaybackStateCompat.STATE_STOPPED -> {
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
                stopSeekbarUpdate()
            }
            PlaybackStateCompat.STATE_BUFFERING -> stopSeekbarUpdate()
            else -> Log.d("MPO", "Unhandled state " + state.state)
        }
    }

    private fun updateProgress() {
        playbackState?.let {
            var currentPosition = it.position / 1000
            if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                // Calculate the elapsed time between the last position update and now and unless
                // paused, we can assume (delta * speed) + current position is approximately the
                // latest position. This ensure that we do not repeatedly call the getPlaybackState()
                // on MediaControllerCompat.
                val timeDelta =
                    (SystemClock.elapsedRealtime() - it.lastPositionUpdateTime) / 1000
                currentPosition += (timeDelta.toInt() * it.playbackSpeed).toLong()
            }
            seekBar.progress = currentPosition.toInt()
        }
    }

    private fun updateDuration(metadata: MediaMetadataCompat?) {
        if (metadata == null) {
            return
        }
        Log.d("MPO", "updateDuration called ")
        val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
        seekBar.max = duration
        remaining.text = DateUtils.formatElapsedTime(duration.toLong())
    }

    private fun updateMediaDisplay() {
        val videoWidth = player.getVideoWidth()
        val videoHeight = player.getVideoHeight()

        if (videoWidth == 0) {
            episodeImageContainer.visibility = View.VISIBLE
            surfaceView.visibility = View.GONE
        } else {
            val surfaceWidth = findViewById<View>(R.id.playerContent).width

            val lp = surfaceView.layoutParams
            // Set the height of the SurfaceView to match the aspect ratio of the video
            // be sure to cast these as floats otherwise the calculation will likely be 0
            lp.height =
                (videoHeight.toFloat() / videoWidth.toFloat() * surfaceWidth.toFloat()).toInt()

            Log.d("MPO", "surface view width: $surfaceWidth")
            Log.d("MPO", "surface view height: " + lp.height)

            surfaceView.layoutParams = lp
            surfaceView.visibility = View.VISIBLE
            episodeImageContainer.visibility = View.INVISIBLE
        }
    }

    private fun handleSkipOrReplay(interval: Int) {
        val mediaController = MediaControllerCompat.getMediaController(this)
        if (mediaController != null) {
            val playbackState = mediaController.playbackState
            val metadata = mediaController.metadata
            if (playbackState != null && metadata != null) {
                val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
                when (playbackState.state) {
                    PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.STATE_BUFFERING, PlaybackStateCompat.STATE_STOPPED -> {
                        val currentPosition = seekBar.progress
                        var newPosition = currentPosition + interval

                        if (newPosition < 0) {
                            newPosition = 0
                        } else if (newPosition > duration) {
                            // Grace period for too much skipping?
                            if (currentPosition > duration - Constants.MEDIA_SKIP_GRACE_PERIOD) {
                                return
                            }
                            newPosition = duration - Constants.MEDIA_SKIP_GRACE_PERIOD
                        }
                        mediaController.transportControls.seekTo((newPosition * 1000).toLong())
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_EPISODE = "extraEpisode"
        const val EXTRA_NOTIFICATION_MEDIA_ID = "extraNotificationMediaId"

        private const val EXTRA_MEDIA_ID = "extraMediaId"

        private const val PROGRESS_UPDATE_INTERNAL: Long = 1000
        private const val PROGRESS_UPDATE_INITIAL_INTERVAL: Long = 100
    }
}
