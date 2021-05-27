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
import com.vmenon.mpo.player.databinding.FragmentMediaPlayerBinding
import com.vmenon.mpo.player.di.dagger.PlayerComponent
import com.vmenon.mpo.player.di.dagger.PlayerComponentProvider
import com.vmenon.mpo.player.domain.*
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.player.viewmodel.MediaPlayerViewModel
import com.vmenon.mpo.view.BaseViewBindingFragment
import javax.inject.Inject

class MediaPlayerFragment : BaseViewBindingFragment<PlayerComponent, FragmentMediaPlayerBinding>(),
    NavigationOrigin<PlayerNavigationParams> by NavigationOrigin.from(PlayerNavigationLocation),
    SurfaceHolder.Callback, MPOPlayer.VideoSizeListener, PlayerClient {

    @Inject
    lateinit var player: MPOPlayer

    private val viewModel: MediaPlayerViewModel by viewModel()

    private var lastPlaybackState: PlaybackState? = null

    private var playOnStart = false
    private var playbackMediaRequest: PlaybackMediaRequest? = null

    override fun setupComponent(context: Context): PlayerComponent =
        (context as PlayerComponentProvider).playerComponent()

    override fun inject(component: PlayerComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playbackMediaRequest = navigationController.getParams(this).playbackMediaRequest
        playOnStart = savedInstanceState == null
        lastPlaybackState = null

        binding.actionButton.setOnClickListener {
            playbackMediaRequest?.let {
                viewModel.togglePlaybackState(it)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                binding.position.text = DateUtils.formatElapsedTime(seekBar.progress.toLong() / 1000)
                binding.remaining.text =
                    "-" + DateUtils.formatElapsedTime((seekBar.max - seekBar.progress).toLong() / 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.seekToPosition(seekBar.progress.toLong())
            }
        })

        binding.replayButton.setOnClickListener { viewModel.skipPlayback(REPLAY_DURATION_SECONDS.toLong()) }
        binding.skipButton.setOnClickListener { viewModel.skipPlayback(SKIP_DURATION_SECONDS.toLong()) }

        binding.surfaceView.holder.addCallback(this)
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
        binding.surfaceView.visibility = View.GONE
    }

    override fun onMediaVideoSizeDetermined(width: Int, height: Int) {
        updateMediaDisplay()
    }

    private fun updateUIFromMedia(playbackMedia: PlaybackMedia) {
        if (playbackMedia != lastPlaybackState?.media) {
            Glide.with(requireActivity())
                .load(playbackMedia.artworkUrl)
                .fitCenter()
                .into(binding.artworkImage)
            binding.mediaTitle.text = playbackMedia.title
        }
    }

    private fun updatePlaybackState(state: PlaybackState.State) {
        when (state) {
            PlaybackState.State.PLAYING -> {
                binding.actionButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp)
            }
            PlaybackState.State.PAUSED -> {
                binding.actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
            }
            PlaybackState.State.NONE, PlaybackState.State.STOPPED -> {
                binding.actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp)
            }
            PlaybackState.State.BUFFERING -> {
            }
            else -> Log.d("MPO", "Unhandled state $state")
        }
    }

    private fun updateProgress(playbackState: PlaybackState) {
        binding.seekBar.progress = playbackState.positionInMillis.toInt()
    }

    private fun updateDuration(playbackState: PlaybackState) {
        Log.d("MPO", "updateDuration called ")
        val duration = playbackState.media.durationInMillis.toInt()
        binding.seekBar.max = duration
    }

    private fun updateMediaDisplay() {
        val videoWidth = player.getVideoWidth()
        val videoHeight = player.getVideoHeight()

        if (videoWidth == 0) {
            binding.episodeImageContainer.visibility = View.VISIBLE
            binding.surfaceView.visibility = View.GONE
        } else {
            val surfaceWidth = binding.playerContent.width

            val lp = binding.surfaceView.layoutParams
            // Set the height of the SurfaceView to match the aspect ratio of the video
            // be sure to cast these as floats otherwise the calculation will likely be 0
            lp.height =
                (videoHeight.toFloat() / videoWidth.toFloat() * surfaceWidth.toFloat()).toInt()

            Log.d("MPO", "surface view width: $surfaceWidth")
            Log.d("MPO", "surface view height: " + lp.height)

            binding.surfaceView.layoutParams = lp
            binding.surfaceView.visibility = View.VISIBLE
            binding.episodeImageContainer.visibility = View.INVISIBLE
        }
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentMediaPlayerBinding =
        FragmentMediaPlayerBinding.inflate(inflater, container, false)

    companion object {
        private const val REPLAY_DURATION_SECONDS = 10
        private const val SKIP_DURATION_SECONDS = 30
    }
}