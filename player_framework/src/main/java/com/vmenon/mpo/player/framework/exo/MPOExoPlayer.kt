package com.vmenon.mpo.player.framework.exo

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.view.SurfaceHolder
import androidx.annotation.VisibleForTesting
import com.google.android.exoplayer2.C.CONTENT_TYPE_SPEECH
import com.google.android.exoplayer2.C.USAGE_MEDIA
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.vmenon.mpo.extensions.useFileDescriptor
import com.vmenon.mpo.player.framework.BaseMPOPlayer
import com.vmenon.mpo.system.domain.Logger
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * Uses ExoPlayer under the hood
 */
class MPOExoPlayer @Inject constructor(
    context: Context,
    mainThreadHandler: Handler,
    executor: Executor,
    private val exoPlayerBuilder: SimpleExoPlayer.Builder,
    private val logger: Logger,
) : BaseMPOPlayer(mainThreadHandler, executor) {

    @VisibleForTesting
    internal var exoPlayer: SimpleExoPlayer? = null

    @VisibleForTesting
    internal var seekRequested = false

    @VisibleForTesting
    internal var prepareRequested = false

    @VisibleForTesting
    internal var surfaceHolder: SurfaceHolder? = null

    @VisibleForTesting
    internal val eventListener = ExoPlayerEventListener()

    private val appContext: Context = context.applicationContext
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
        exoPlayer?.let { player ->
            seekRequested = true
            player.seekTo(position)
        } ?: run {
            currentPos = position
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
            .createMediaSource(MediaItem.Builder().setUri(Uri.fromFile(file)).build())
        exoPlayer?.playWhenReady = false

        file.useFileDescriptor { fileDescriptor ->
            mediaMetadataRetriever.setDataSource(fileDescriptor)
        }
        prepareRequested = true
        exoPlayer?.setMediaSource(videoSource)
        exoPlayer?.prepare()
    }

    override fun doCleanUp() {
        exoPlayer?.release()
        exoPlayer?.removeListener(eventListener)
        exoPlayer = null
    }

    private fun createMediaPlayerIfNeeded() {
        logger.println("createMediaPlayerIfNeeded. needed? " + (exoPlayer == null))
        if (exoPlayer == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(CONTENT_TYPE_SPEECH)
                .setUsage(USAGE_MEDIA)
                .build()
            val player = exoPlayerBuilder.setAudioAttributes(audioAttributes, false)
                .build()
            player.addListener(ExoPlayerEventListener())

            /** TODO
             * // Make sure the media player will acquire a wake-lock while
             * // playing. If we don't do that, the CPU might go to sleep while the
             * // song is playing, causing playback to stop.
             * mMediaPlayer.setWakeMode(mService.getApplicationContext(),
             * PowerManager.PARTIAL_WAKE_LOCK); */

            if (surfaceHolder != null) {
                player.setVideoSurfaceHolder(surfaceHolder)
            }

            exoPlayer = player
        }
    }

    internal inner class ExoPlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    if (seekRequested) {
                        seekRequested = false
                        mListener?.onMediaSeekFinished()
                    }
                    if (prepareRequested) {
                        prepareRequested = false
                        logger.println("Prepared, currentPosition: " + exoPlayer?.currentPosition)
                        mListener?.onMediaPrepared()
                    }
                }
                Player.STATE_ENDED -> mListener?.onMediaFinished()
                else -> {
                    // no-op
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            logger.println("ExoPlayer error", error)
        }
    }
}
