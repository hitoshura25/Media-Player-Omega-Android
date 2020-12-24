package com.vmenon.mpo.player.view.activity

import android.content.Context
import android.content.Intent
import android.os.*
import android.text.format.DateUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer

import com.bumptech.glide.Glide
import com.vmenon.mpo.navigation.domain.NavigationSource
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.framework.MPOPlayer.VideoSizeListener
import com.vmenon.mpo.player.R
import com.vmenon.mpo.player.di.dagger.PlayerComponent
import com.vmenon.mpo.player.di.dagger.PlayerComponentProvider
import com.vmenon.mpo.player.domain.*
import com.vmenon.mpo.player.domain.PlaybackState.*
import com.vmenon.mpo.player.viewmodel.MediaPlayerViewModel
import com.vmenon.mpo.view.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_media_player.*

import javax.inject.Inject

const val REPLAY_DURATION_SECONDS = -10
const val SKIP_DURATION_SECONDS = 30

class MediaPlayerActivity : BaseActivity<PlayerComponent>(), SurfaceHolder.Callback,
    VideoSizeListener, PlayerClient, NavigationSource<PlayerNavigationParams> {
    @Inject
    lateinit var player: MPOPlayer

    val viewModel: MediaPlayerViewModel by viewModel()

    private var lastPlaybackState: PlaybackState? = null

    private var playOnStart = false
    private var fromNotification = false
    private lateinit var playbackMediaRequest: PlaybackMediaRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        if (intent.hasExtra(EXTRA_NOTIFICATION_MEDIA_ID)) {
            fromNotification = true
            playbackMediaRequest =
                intent.getSerializableExtra(EXTRA_NOTIFICATION_MEDIA_ID) as PlaybackMediaRequest
        } else {
            playbackMediaRequest = navigationController.getParams(this).playbackMediaRequest
        }

        playOnStart = savedInstanceState == null

        actionButton.setOnClickListener {
            viewModel.togglePlaybackState(playbackMediaRequest)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                position.text = DateUtils.formatElapsedTime(seekBar.progress.toLong() / 1000)
                remaining.text =
                    "-" + DateUtils.formatElapsedTime((seekBar.max - seekBar.progress).toLong() / 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.seekToPosition(seekBar.progress.toLong())
            }
        })

        replayButton.setOnClickListener { viewModel.skipPlayback(REPLAY_DURATION_SECONDS.toLong()) }
        skipButton.setOnClickListener { viewModel.skipPlayback(SKIP_DURATION_SECONDS.toLong()) }

        surfaceView.holder.addCallback(this)
        player.setVideoSizeListener(this@MediaPlayerActivity)

        viewModel.connected.observe(this, Observer {
            updateMediaDisplay()
            if (!fromNotification) {
                playbackMediaRequest.let {
                    viewModel.playMedia(it)
                }
            }
        })
        viewModel.playBackState.observe(this, Observer { playbackState ->
            updatePlaybackState(playbackState.state)
            updateProgress(playbackState)
            updateDuration(playbackState)
            updateUIFromMedia(playbackState.media)
            lastPlaybackState = playbackState
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.connectClient(this)
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnectClient(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.setVideoSizeListener(null)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(EXTRA_NOTIFICATION_MEDIA_ID)) {
            fromNotification = true
            playbackMediaRequest =
                intent.getSerializableExtra(EXTRA_NOTIFICATION_MEDIA_ID) as PlaybackMediaRequest
            intent.removeExtra(EXTRA_NOTIFICATION_MEDIA_ID)
        }
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

    private fun updateUIFromMedia(playbackMedia: PlaybackMedia) {
        if (playbackMedia != lastPlaybackState?.media) {
            Glide.with(this)
                .load(playbackMedia.artworkUrl)
                .fitCenter()
                .into(artworkImage)
            mediaTitle.text = playbackMedia.title
        }
    }

    private fun updatePlaybackState(state: State) {
        when (state) {
            State.PLAYING -> {
                actionButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp)
            }
            State.PAUSED -> {
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
            }
            State.NONE, State.STOPPED -> {
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
            }
            State.BUFFERING -> {
            }
            else -> Log.d("MPO", "Unhandled state $state")
        }
    }

    private fun updateProgress(playbackState: PlaybackState) {
        seekBar.progress = playbackState.positionInMillis.toInt()
    }

    private fun updateDuration(playbackState: PlaybackState) {
        Log.d("MPO", "updateDuration called ")
        val duration = playbackState.media.durationInMillis.toInt()
        seekBar.max = duration
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

    override fun setupComponent(context: Context): PlayerComponent =
        (context as PlayerComponentProvider).playerComponent()

    override fun inject(component: PlayerComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    companion object {
        const val EXTRA_NOTIFICATION_MEDIA_ID = "extraNotificationMediaId"
    }
}
