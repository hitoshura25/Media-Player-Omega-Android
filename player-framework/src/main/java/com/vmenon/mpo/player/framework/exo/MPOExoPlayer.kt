package com.vmenon.mpo.player.framework.exo

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import com.google.android.exoplayer2.C.CONTENT_TYPE_SPEECH
import com.google.android.exoplayer2.C.USAGE_MEDIA
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.vmenon.mpo.extensions.useFileDescriptor
import com.vmenon.mpo.player.framework.BaseMPOPlayer
import java.io.File
import javax.inject.Inject

/**
 * Uses ExoPlayer under the hood
 */
class MPOExoPlayer @Inject constructor(context: Context) : BaseMPOPlayer() {
    private var exoPlayer: SimpleExoPlayer? = null
    private val appContext: Context = context.applicationContext
    private var seekRequested = false
    private var prepareRequested = false
    private var surfaceHolder: SurfaceHolder? = null
    private val eventListener = ExoPlayerEventListener()
    private val mediaMetadataRetriever = MediaMetadataRetriever()

    override val isPlaying: Boolean
        get() = exoPlayer?.playWhenReady ?: false

    override fun play() {
        exoPlayer?.playWhenReady = true
    }

    override fun pause() {
        exoPlayer?.let { player ->
            if (player.playWhenReady) {
                player.playWhenReady = false
                currentPos = player.currentPosition
            }
        }
    }

    override fun stop() {
        exoPlayer?.let { player ->
            if (player.playWhenReady) {
                player.stop()
                currentPos = player.currentPosition
            }
        }

    }

    override fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: currentPos
    }

    override fun seekTo(position: Long) {
        if (exoPlayer == null) {
            currentPos = position
        } else {
            seekRequested = true
            exoPlayer?.seekTo(position)
        }
    }

    override fun setVolume(volume: Float) {
        exoPlayer?.volume = volume
    }

    override fun setDisplay(surfaceHolder: SurfaceHolder?) {
        this.surfaceHolder = surfaceHolder

        exoPlayer?.setVideoSurfaceHolder(surfaceHolder)
    }

    override fun doPrepareForPlayback(file: File) {
        createMediaPlayerIfNeeded()
        val bandwidthMeter = DefaultBandwidthMeter.Builder(appContext).build()
        val dataSourceFactory = DefaultDataSourceFactory(
            appContext,
            Util.getUserAgent(appContext, "MPO"), bandwidthMeter
        )
        val extractorsFactory = DefaultExtractorsFactory()
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
            .createMediaSource(Uri.fromFile(file))
        exoPlayer?.playWhenReady = false

        file.useFileDescriptor { fileDescriptor ->
            mediaMetadataRetriever.setDataSource(fileDescriptor)
        }
        prepareRequested = true
        exoPlayer?.prepare(videoSource)
    }

    override fun doCleanUp() {
        exoPlayer?.release()
        exoPlayer?.removeListener(eventListener)
        exoPlayer = null
    }

    private fun createMediaPlayerIfNeeded() {
        Log.d("MPO", "createMediaPlayerIfNeeded. needed? " + (exoPlayer == null))
        if (exoPlayer == null) {
            exoPlayer = SimpleExoPlayer.Builder(appContext).build()
            exoPlayer?.addListener(ExoPlayerEventListener())
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(CONTENT_TYPE_SPEECH)
                .setUsage(USAGE_MEDIA)
                .build()
            exoPlayer?.audioAttributes = audioAttributes

            /** TODO
             * // Make sure the media player will acquire a wake-lock while
             * // playing. If we don't do that, the CPU might go to sleep while the
             * // song is playing, causing playback to stop.
             * mMediaPlayer.setWakeMode(mService.getApplicationContext(),
             * PowerManager.PARTIAL_WAKE_LOCK); */

            if (surfaceHolder != null) {
                exoPlayer?.setVideoSurfaceHolder(surfaceHolder)
            }

        }
    }

    private inner class ExoPlayerEventListener : Player.EventListener {
        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {

        }

        override fun onLoadingChanged(isLoading: Boolean) {

        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    if (seekRequested) {
                        seekRequested = false
                        mListener?.onMediaSeekFinished()
                    }

                    if (prepareRequested) {
                        prepareRequested = false

                        Log.d("MPO", "Prepared, currentPosition: " + exoPlayer?.currentPosition)

                        mListener?.onMediaPrepared()
                    }
                }
                Player.STATE_ENDED -> mListener?.onMediaFinished()
                else -> {
                }
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {

        }

        override fun onPlayerError(error: ExoPlaybackException) {
            val what: String? = when (error.type) {
                ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message
                ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message
                ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message
                else -> "Unknown: $error"
            }

            Log.w("MPO", "ExoPlayer error: what=$what")
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

        }
    }
}
