package com.vmenon.mpo.activity;

import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.core.MPOMediaService;
import com.vmenon.mpo.core.persistence.MPORepository;
import com.vmenon.mpo.util.MediaHelper;

import org.parceler.Parcels;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class MediaPlayerActivity extends BaseActivity {
    public static final String EXTRA_EPISODE = "extraEpisode";
    public static final String EXTRA_FROM_NOTIFICATION = "extraFromNotification";

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    @Inject
    protected MPORepository repository;

    private final Handler handler = new Handler();
    private Episode episode;
    private MediaBrowserCompat mediaBrowser;
    private PlaybackStateCompat playbackState;
    private boolean playOnStart = false;
    private boolean fromNotification = false;

    private ImageView actionButton;
    private ImageView artworkImage;
    private SeekBar seekBar;
    private TextView positionText;
    private TextView remainingText;

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduleFuture;

    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
            try {
                MediaControllerCompat mediaController = new MediaControllerCompat(
                        MediaPlayerActivity.this, token);
                MediaControllerCompat.setMediaController(MediaPlayerActivity.this, mediaController);
                mediaController.registerCallback(controllerCallback);
                PlaybackStateCompat playbackState = mediaController.getPlaybackState();
                updatePlaybackState(playbackState);
                MediaMetadataCompat metadata = mediaController.getMetadata();
                String mediaId;

                if (metadata != null) {
                    updateDuration(metadata);
                }
                updateProgress();
                boolean currentlyPlaying = playbackState != null &&
                        (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING ||
                                playbackState.getState() == PlaybackStateCompat.STATE_BUFFERING);

                if (fromNotification) {
                    mediaId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    MediaHelper.MediaType mediaType = MediaHelper.getMediaTypeFromMediaId(mediaId);
                    switch (mediaType.getMediaType()) {
                        case MediaHelper.MEDIA_TYPE_EPISODE:
                            repository.getLiveEpisode(mediaType.getId()).observe(MediaPlayerActivity.this,
                                    new Observer<Episode>() {
                                        @Override
                                        public void onChanged(@Nullable Episode episode) {
                                            MediaPlayerActivity.this.episode = episode;
                                            updateUIFromMedia();
                                        }
                                    });
                            break;
                    }
                } else {
                    mediaId = MediaHelper.createMediaId(episode);
                }

                if (currentlyPlaying) {
                    scheduleSeekbarUpdate();

                    // Force playing if from notification to trigger callback
                    playOnStart = playOnStart || fromNotification;
                }

                if (playOnStart) {
                    mediaController.getTransportControls().playFromMediaId(mediaId, null);
                    playOnStart = false;
                }

            } catch (RemoteException e) {
                Log.w("MPO", "Error creating mediaController", e);
            }
        }
    };

    private MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    if (metadata != null) {
                        updateDuration(metadata);
                    }
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    updatePlaybackState(state);
                }
            };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        setContentView(R.layout.activity_media_player);
        fromNotification = getIntent().getBooleanExtra(EXTRA_FROM_NOTIFICATION, false);

        actionButton = findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(
                        MediaPlayerActivity.this);
                MediaControllerCompat.TransportControls transportControls = mediaController.getTransportControls();
                int currentState = mediaController.getPlaybackState().getState();

                switch (currentState) {
                    case PlaybackStateCompat.STATE_BUFFERING:
                    case PlaybackStateCompat.STATE_PLAYING:
                        transportControls.pause();
                        stopSeekbarUpdate();
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                    case PlaybackStateCompat.STATE_STOPPED:
                        transportControls.play();
                        scheduleSeekbarUpdate();
                        break;
                    case PlaybackStateCompat.STATE_NONE:
                        transportControls.playFromMediaId(MediaHelper.createMediaId(episode), null);
                        break;
                }
            }
        });

        artworkImage = findViewById(R.id.artworkImage);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                positionText.setText(DateUtils.formatElapsedTime(seekBar.getProgress()));
                remainingText.setText("-" + DateUtils.formatElapsedTime(seekBar.getMax() - seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MediaControllerCompat.getMediaController(MediaPlayerActivity.this).getTransportControls()
                        .seekTo(seekBar.getProgress() * 1000);
                scheduleSeekbarUpdate();
            }
        });

        positionText = findViewById(R.id.position);
        remainingText = findViewById(R.id.remaining);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MPOMediaService.class),
                connectionCallback,
                null); // optional Bundle

        if (!fromNotification) {
            episode = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_EPISODE));
            if (savedInstanceState == null) {
                playOnStart = true;
            }
            updateUIFromMedia();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(MediaPlayerActivity.this) != null) {
            MediaControllerCompat.getMediaController(MediaPlayerActivity.this)
                    .unregisterCallback(controllerCallback);
        }

        mediaBrowser.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        executorService.shutdown();
    }

    private void updateUIFromMedia() {
        Glide.with(this).load(episode.artworkUrl).fitCenter().into(artworkImage);
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!executorService.isShutdown()) {
            scheduleFuture = executorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            handler.post(updateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL, PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (scheduleFuture != null) {
            scheduleFuture.cancel(false);
        }
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        playbackState = state;

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                actionButton.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp);
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                actionButton.setImageResource(R.drawable.ic_play_circle_filled_white_48dp);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                stopSeekbarUpdate();
                break;
            default:
                Log.d("MPO", "Unhandled state " + state.getState());
        }
    }

    private void updateProgress() {
        if (playbackState == null) {
            return;
        }
        long currentPosition = playbackState.getPosition() / 1000;
        if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = (SystemClock.elapsedRealtime() -
                    playbackState.getLastPositionUpdateTime()) / 1000;
            currentPosition += (int) timeDelta * playbackState.getPlaybackSpeed();
        }
        seekBar.setProgress((int) currentPosition);
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        Log.d("MPO", "updateDuration called ");
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        seekBar.setMax(duration);
        remainingText.setText(DateUtils.formatElapsedTime(duration));
    }
}
