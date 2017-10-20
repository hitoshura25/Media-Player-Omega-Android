package com.vmenon.mpo.core;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_SPEECH;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;

/**
 * Uses ExoPlayer under the hood
 */
public class MPOExoPlayer extends MPOPlayer {
    private SimpleExoPlayer exoPlayer;
    private long currentPosition;
    private Context appContext;
    private boolean seekRequested = false;
    private boolean prepareRequested = false;
    private SurfaceHolder surfaceHolder;
    private ExoPlayerEventListener eventListener = new ExoPlayerEventListener();
    private MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

    public MPOExoPlayer(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void play() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void pause() {
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            exoPlayer.setPlayWhenReady(false);
            currentPosition = exoPlayer.getCurrentPosition();
        }
    }

    @Override
    public void stop() {
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            exoPlayer.stop();
            currentPosition = exoPlayer.getCurrentPosition();
        }
    }

    @Override
    public long getCurrentPosition() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : currentPosition;
    }

    @Override
    public boolean isPlaying() {
        return exoPlayer != null ? exoPlayer.getPlayWhenReady() : false;
    }

    @Override
    public void seekTo(long position) {
        if (exoPlayer == null) {
            currentPosition = position;
        } else {
            seekRequested = true;
            exoPlayer.seekTo(position);
        }
    }

    @Override
    public void setVolume(float volume) {
        if (exoPlayer != null) {
            exoPlayer.setVolume(volume);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;

        if (exoPlayer != null) {
            exoPlayer.setVideoSurfaceHolder(surfaceHolder);
        }
    }

    @Override
    protected void doPrepareForPlayback(File file) {
        createMediaPlayerIfNeeded();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(appContext,
                Util.getUserAgent(appContext, "MPO"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse("file://" + file.getPath()),
                dataSourceFactory, extractorsFactory, null, null);
        exoPlayer.setPlayWhenReady(false);
        mediaMetadataRetriever.setDataSource(file.getPath());
        prepareRequested = true;
        exoPlayer.prepare(videoSource);
    }

    @Override
    protected void doCleanUp() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer.removeListener(eventListener);
            exoPlayer = null;
        }
    }

    private void createMediaPlayerIfNeeded() {
        Log.d("MPO", "createMediaPlayerIfNeeded. needed? " + (exoPlayer == null));
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(appContext, new DefaultTrackSelector());
            exoPlayer.addListener(new ExoPlayerEventListener());
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_SPEECH)
                    .setUsage(USAGE_MEDIA)
                    .build();
            exoPlayer.setAudioAttributes(audioAttributes);

            /** TODO
             // Make sure the media player will acquire a wake-lock while
             // playing. If we don't do that, the CPU might go to sleep while the
             // song is playing, causing playback to stop.
             mMediaPlayer.setWakeMode(mService.getApplicationContext(),
             PowerManager.PARTIAL_WAKE_LOCK);*/

            if (surfaceHolder != null) {
                exoPlayer.setVideoSurfaceHolder(surfaceHolder);
            }

        }
    }

    private class ExoPlayerEventListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_READY:
                    if (seekRequested) {
                        seekRequested = false;
                        if (listener != null) {
                            listener.onMediaSeekFinished();
                        }
                    }

                    if (prepareRequested) {
                        prepareRequested = false;

                        Log.d("MPO", "Prepared, currentPosition: " + exoPlayer.getCurrentPosition());

                        if (listener != null) {
                            listener.onMediaPrepared();
                        }
                    }

                    break;
                case Player.STATE_ENDED:
                    if (listener != null) {
                        listener.onMediaFinished();
                    }
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    }
}
