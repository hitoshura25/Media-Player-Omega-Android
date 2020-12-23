package com.vmenon.mpo.player.framework

import android.media.MediaPlayer
import android.util.Log
import android.view.SurfaceHolder

import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Wrapper around actual media player mechanism (i.e. [android.media.MediaPlayer])
 */
class MPOMediaPlayer : BaseMPOPlayer(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition: Long = 0
    private var surfaceHolder: SurfaceHolder? = null

    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    override fun doPrepareForPlayback(file: File) {
        createMediaPlayerIfNeeded()
        mediaPlayer?.let { player ->
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(file)
                val fd = fis.fd
                player.setDataSource(fd)
                player.prepareAsync()
            } catch (e: IOException) {
                Log.w("Can't play music", e)
            } finally {
                if (fis != null) {
                    try {
                        fis.close()
                    } catch (e: IOException) {
                    }

                }
            }
        }

    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                currentPosition = player.currentPosition.toLong()
            }
        }
    }

    override fun stop() {
        currentPosition = getCurrentPosition()
        mediaPlayer?.stop()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: currentPosition
    }

    override fun seekTo(position: Long) {
        Log.d("MPO", "seekTo called with $position")

        if (mediaPlayer == null) {
            // If we do not have a current media player, simply update the current position
            currentPosition = position
        } else {
            mediaPlayer?.seekTo(position.toInt())
        }
    }

    override fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    override fun setDisplay(surfaceHolder: SurfaceHolder?) {
        this.surfaceHolder = surfaceHolder
        mediaPlayer?.setDisplay(surfaceHolder)
    }

    override fun doCleanUp() {
        mediaPlayer?.let { player ->
            player.reset()
            player.release()
        }
        mediaPlayer = null
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mListener?.onMediaPrepared()
    }

    override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
        return false
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        mListener?.onMediaFinished()
    }

    override fun onSeekComplete(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
        mListener?.onMediaSeekFinished()
    }

    private fun createMediaPlayerIfNeeded() {
        Log.d("MPO", "createMediaPlayerIfNeeded. needed? " + (mediaPlayer == null))
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()

            /** TODO
             * // Make sure the media player will acquire a wake-lock while
             * // playing. If we don't do that, the CPU might go to sleep while the
             * // song is playing, causing playback to stop.
             * mMediaPlayer.setWakeMode(mService.getApplicationContext(),
             * PowerManager.PARTIAL_WAKE_LOCK); */

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mediaPlayer?.setOnPreparedListener(this)
            mediaPlayer?.setOnCompletionListener(this)
            mediaPlayer?.setOnErrorListener(this)
            mediaPlayer?.setOnSeekCompleteListener(this)
            surfaceHolder?.let {
                mediaPlayer?.setDisplay(it)
            }
        } else {
            mediaPlayer?.reset()
        }
    }
}
