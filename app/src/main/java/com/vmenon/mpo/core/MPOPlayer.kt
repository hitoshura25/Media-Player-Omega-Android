package com.vmenon.mpo.core

import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder

import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

abstract class MPOPlayer {

    protected var mListener: MediaPlayerListener? = null
    protected var mVideoSizeListener: VideoSizeListener? = null

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    @Volatile
    private var videoSizeCalculated = false
    @Volatile
    private var videoWidth = -1
    @Volatile
    private var videoHeight = -1

    protected var currentPos: Long = 0

    abstract val isPlaying: Boolean

    interface MediaPlayerListener {
        fun onMediaPrepared()
        fun onMediaFinished()
        fun onMediaSeekFinished()
    }

    interface VideoSizeListener {
        fun onMediaVideoSizeDetermined(width: Int, height: Int)
    }

    fun prepareForPlayback(file: File) {
        videoSizeCalculated = false
        doPrepareForPlayback(file)
        executor.execute(MediaMetadataRetrieverTask(file.path, mVideoSizeListener))
    }

    fun setListener(listener: MediaPlayerListener?) {
        this.mListener = listener
    }

    fun setVideoSizeListener(listener: VideoSizeListener?) {
        this.mVideoSizeListener = listener
    }

    fun cleanup() {
        doCleanUp()
    }

    fun getVideoWidth(): Int {
        return if (videoSizeCalculated) videoWidth else 0
    }

    fun getVideoHeight(): Int {
        return if (videoSizeCalculated) videoHeight else 0
    }

    abstract fun play()

    abstract fun pause()

    abstract fun stop()

    abstract fun seekTo(position: Long)

    abstract fun setVolume(volume: Float)

    abstract fun setDisplay(surfaceHolder: SurfaceHolder?)

    abstract fun getCurrentPosition(): Long

    protected abstract fun doCleanUp()

    protected abstract fun doPrepareForPlayback(file: File)

    private inner class MediaMetadataRetrieverTask internal constructor(private val filePath: String, listener: VideoSizeListener?) : Runnable {
        private val listenerRef = if (listener != null) WeakReference(listener) else null

        override fun run() {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val widthStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            val heightStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)

            videoWidth = if (widthStr != null) Integer.valueOf(widthStr) else 0
            videoHeight = if (heightStr != null) Integer.valueOf(heightStr) else 0
            videoSizeCalculated = true

            val listener = listenerRef?.get()
            if (listener != null) {
                mainThreadHandler.post {
                    listener.onMediaVideoSizeDetermined(videoWidth, videoHeight)
                }
            }
        }
    }
}
