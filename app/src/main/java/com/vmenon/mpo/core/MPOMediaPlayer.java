package com.vmenon.mpo.core;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Wrapper around actual media player mechanism (i.e. {@link android.media.MediaPlayer})
 */
public class MPOMediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    public interface MediaPlayerListener {
        void onMediaPrepared();
        void onMediaFinished();
        void onMediaSeekFinished();
    }

    private MediaPlayer mediaPlayer;
    private long currentPosition;
    private MediaPlayerListener listener;
    private SurfaceHolder surfaceHolder;

    public MPOMediaPlayer() {

    }

    public void setListener(MediaPlayerListener listener) {
        this.listener = listener;
    }

    public void prepareForPlayback(File file) {
        createMediaPlayerIfNeeded();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileDescriptor fd = fis.getFD();
            mediaPlayer.setDataSource(fd);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.w("Can't play music", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void play() {
        mediaPlayer.start();
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            currentPosition = getCurrentPosition();
            mediaPlayer.stop();
        }
    }

    public long getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : currentPosition;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void seekTo(long position) {
        Log.d("MPO", "seekTo called with " + position);

        if (mediaPlayer == null) {
            // If we do not have a current media player, simply update the current position
            currentPosition = position;
        } else {
            mediaPlayer.seekTo((int) position);
        }
    }

    public void setVolume(float leftVolume, float rightVolume) {
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        listener = null;
    }

    public void setDisplay(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(surfaceHolder);
        }
    }

    public int getVideoWidth() {
        return mediaPlayer != null ? mediaPlayer.getVideoWidth() : 0;
    }

    public int getVideoHeight() {
        return mediaPlayer != null ? mediaPlayer.getVideoHeight() : 0;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (listener != null) {
            listener.onMediaPrepared();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (listener != null) {
            listener.onMediaFinished();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        if (listener != null) {
            listener.onMediaSeekFinished();
        }
    }

    private void createMediaPlayerIfNeeded() {
        Log.d("MPO", "createMediaPlayerIfNeeded. needed? " + (mediaPlayer == null));
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            /** TODO
             // Make sure the media player will acquire a wake-lock while
             // playing. If we don't do that, the CPU might go to sleep while the
             // song is playing, causing playback to stop.
             mMediaPlayer.setWakeMode(mService.getApplicationContext(),
             PowerManager.PARTIAL_WAKE_LOCK);*/

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            if (surfaceHolder != null) {
                mediaPlayer.setDisplay(surfaceHolder);
            }
        } else {
            mediaPlayer.reset();
        }
    }
}
