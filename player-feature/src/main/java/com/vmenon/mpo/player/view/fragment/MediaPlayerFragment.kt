package com.vmenon.mpo.player.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.player.R
import com.vmenon.mpo.player.di.dagger.PlayerComponent
import com.vmenon.mpo.player.di.dagger.PlayerComponentProvider
import com.vmenon.mpo.player.domain.*
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.viewmodel.MediaPlayerViewModel
import com.vmenon.mpo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_media_player.*
import javax.inject.Inject

class MediaPlayerFragment : BaseFragment<PlayerComponent>(),
    NavigationOrigin<PlayerNavigationParams> by NavigationOrigin.from(PlayerNavigationLocation),
    SurfaceHolder.Callback, MPOPlayer.VideoSizeListener, PlayerClient {

    @Inject
    lateinit var player: MPOPlayer

    private val viewModel: MediaPlayerViewModel by viewModel()

    private var lastPlaybackState: PlaybackState? = null

    private var playOnStart = false
    private var playbackMediaRequest: PlaybackMediaRequest? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media_player, container, false)
    }

    override fun setupComponent(context: Context): PlayerComponent =
        (context as PlayerComponentProvider).playerComponent()

    override fun inject(component: PlayerComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        playbackMediaRequest = navigationController.getParams(this).playbackMediaRequest
        playOnStart = savedInstanceState == null

        actionButton.setOnClickListener {
            playbackMediaRequest?.let {
                viewModel.togglePlaybackState(it)
            }
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
        player.setVideoSizeListener(this)
        viewModel.playBackState.observe(viewLifecycleOwner, Observer { playbackState ->
            updatePlaybackState(playbackState.state)
            updateProgress(playbackState)
            updateDuration(playbackState)
            updateUIFromMedia(playbackState.media)
            lastPlaybackState = playbackState
        })
    }


    override fun onStart() {
        super.onStart()
        viewModel.connectClient(this).observe(viewLifecycleOwner, Observer {
            updateMediaDisplay()
            playbackMediaRequest?.let {
                viewModel.playMedia(it)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnectClient(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.setVideoSizeListener(null)
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
            Glide.with(requireActivity())
                .load(playbackMedia.artworkUrl)
                .fitCenter()
                .into(artworkImage)
            mediaTitle.text = playbackMedia.title
        }
    }

    private fun updatePlaybackState(state: PlaybackState.State) {
        when (state) {
            PlaybackState.State.PLAYING -> {
                actionButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp)
            }
            PlaybackState.State.PAUSED -> {
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
            }
            PlaybackState.State.NONE, PlaybackState.State.STOPPED -> {
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
            }
            PlaybackState.State.BUFFERING -> {
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
            val surfaceWidth = playerContent.width

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

    companion object {
        private const val REPLAY_DURATION_SECONDS = 10
        private const val SKIP_DURATION_SECONDS = 30
    }
}