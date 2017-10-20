package com.vmenon.mpo.core;

import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class MPOPlayer {
    public interface MediaPlayerListener {
        void onMediaPrepared();
        void onMediaFinished();
        void onMediaSeekFinished();
    }

    public interface VideoSizeListener {
        void onMediaVideoSizeDetermined(int width, int height);
    }

    protected MediaPlayerListener listener;
    protected VideoSizeListener videoSizeListener;

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private Executor executor = Executors.newSingleThreadExecutor();
    private volatile boolean videoSizeCalculated = false;
    private volatile int videoWidth = -1;
    private volatile int videoHeight = -1;

    public void prepareForPlayback(File file) {
        videoSizeCalculated = false;
        doPrepareForPlayback(file);
        executor.execute(new MediaMetadataRetrieverTask(file.getPath(), videoSizeListener));
    }

    public void setListener(MediaPlayerListener listener) {
        this.listener = listener;
    }

    public void setVideoSizeListener(VideoSizeListener listener) {
        this.videoSizeListener = listener;
    }

    public void cleanup() {
        doCleanUp();
    }

    public int getVideoWidth() {
        return videoSizeCalculated ? videoWidth : 0;
    }

    public int getVideoHeight() {
        return videoSizeCalculated ? videoHeight : 0;
    }

    public abstract void play();

    public abstract void pause();

    public abstract void stop();

    public abstract long getCurrentPosition();

    public abstract boolean isPlaying();

    public abstract void seekTo(long position);

    public abstract void setVolume(float volume);

    public abstract void setDisplay(SurfaceHolder surfaceHolder);

    protected abstract void doCleanUp();

    protected abstract void doPrepareForPlayback(File file);

    private class MediaMetadataRetrieverTask implements Runnable {
        private String filePath;
        private WeakReference<VideoSizeListener> listenerRef;

        MediaMetadataRetrieverTask(String filePath, VideoSizeListener listener) {
            this.filePath = filePath;
            this.listenerRef = listener != null ? new WeakReference<>(listener) : null;
        }

        @Override
        public void run() {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);
            final String widthStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            final String heightStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

            videoWidth = widthStr != null ? Integer.valueOf(widthStr) : 0;
            videoHeight = heightStr != null ? Integer.valueOf(heightStr) : 0;
            videoSizeCalculated = true;

            final VideoSizeListener listener = listenerRef != null ? listenerRef.get() : null;
            if (listener != null) {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onMediaVideoSizeDetermined(videoWidth, videoHeight);
                        }
                    }
                });
            }
        }
    }
}
