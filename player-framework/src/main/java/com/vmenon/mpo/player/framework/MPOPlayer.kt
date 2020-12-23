package com.vmenon.mpo.player.framework

import android.view.SurfaceHolder
import java.io.File

interface MPOPlayer {
    interface MediaPlayerListener {
        fun onMediaPrepared()
        fun onMediaFinished()
        fun onMediaSeekFinished()
    }

    interface VideoSizeListener {
        fun onMediaVideoSizeDetermined(width: Int, height: Int)
    }

    val isPlaying: Boolean
    fun setListener(listener: MediaPlayerListener?)
    fun setVideoSizeListener(listener: VideoSizeListener?)
    fun prepareForPlayback(file: File)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Long)
    fun setVolume(volume: Float)
    fun setDisplay(surfaceHolder: SurfaceHolder?)
    fun getCurrentPosition(): Long
    fun cleanup()
    fun getVideoWidth(): Int
    fun getVideoHeight(): Int
}