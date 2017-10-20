package com.vmenon.mpo.activity;

import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Intent;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vmenon.mpo.Constants;
import com.vmenon.mpo.R;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.MPOMediaService;
import com.vmenon.mpo.core.MPOPlayer;
import com.vmenon.mpo.core.persistence.MPORepository;
import com.vmenon.mpo.util.MediaHelper;

import org.parceler.Parcels;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class MediaPlayerActivity extends BaseActivity implements SurfaceHolder.Callback,
        MPOPlayer.VideoSizeListener {
    public static final String EXTRA_EPISODE = "extraEpisode";
    public static final String EXTRA_NOTIFICATION_MEDIA_ID = "extraNotificationMediaId";

    private static final String EXTRA_MEDIA_ID = "extraMediaId";

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    @Inject
    protected MPORepository repository;

    @Inject
    protected MPOPlayer player;

    private final Handler handler = new Handler();
    private Episode episode;
    private Show show;
    private MediaBrowserCompat mediaBrowser;
    private PlaybackStateCompat playbackState;
    private boolean playOnStart = false;
    private boolean fromNotification = false;
    private String requestedMediaId;

    private ImageView actionButton;
    private ImageView artworkImage;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SeekBar seekBar;
    private TextView positionText;
    private TextView remainingText;
    private TextView titleText;
    private View episodeImageContainer;
    private View skipButton;
    private View replayButton;

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduleFuture;

    private final MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
            try {
                String currentlyPlayingMediaId = "";
                MediaControllerCompat mediaController = new MediaControllerCompat(
                        MediaPlayerActivity.this, token);
                MediaControllerCompat.setMediaController(MediaPlayerActivity.this, mediaController);
                mediaController.registerCallback(controllerCallback);
                PlaybackStateCompat playbackState = mediaController.getPlaybackState();
                boolean currentlyPlaying = playbackState != null &&
                        (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING ||
                                playbackState.getState() == PlaybackStateCompat.STATE_BUFFERING);

                MediaMetadataCompat metadata = mediaController.getMetadata();
                if (metadata != null) {
                    currentlyPlayingMediaId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    if (requestedMediaId.equals(currentlyPlayingMediaId)) {
                        updateDuration(metadata);
                    }
                }

                if (requestedMediaId.equals(currentlyPlayingMediaId)) {
                    updateMediaDisplay();
                    updatePlaybackState(playbackState);
                    updateProgress();
                    if (currentlyPlaying) {
                        scheduleSeekbarUpdate();

                        // Force playing if from notification to trigger callback
                        playOnStart = playOnStart || fromNotification;
                    }
                }

                if (fromNotification) {
                    MediaHelper.MediaType mediaType = MediaHelper.getMediaTypeFromMediaId(requestedMediaId);
                    switch (mediaType.getMediaType()) {
                        case MediaHelper.MEDIA_TYPE_EPISODE:
                            repository.getLiveEpisode(mediaType.getId()).observe(MediaPlayerActivity.this,
                                    new Observer<Episode>() {
                                        @Override
                                        public void onChanged(@Nullable Episode episode) {
                                            MediaPlayerActivity.this.episode = episode;
                                            repository.getLiveShow(episode.showId).observe(
                                                    MediaPlayerActivity.this,
                                                    new Observer<Show>() {
                                                        @Override
                                                        public void onChanged(@Nullable Show show) {
                                                            MediaPlayerActivity.this.show = show;
                                                            updateUIFromMedia();
                                                        }
                                                    });
                                        }
                                    });
                            break;
                    }
                }

                if (playOnStart) {
                    mediaController.getTransportControls().playFromMediaId(requestedMediaId, null);
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
        if (getIntent().hasExtra(EXTRA_NOTIFICATION_MEDIA_ID)) {
            fromNotification = true;
            requestedMediaId = getIntent().getStringExtra(EXTRA_NOTIFICATION_MEDIA_ID);
        }

        if (savedInstanceState != null) {
            requestedMediaId = savedInstanceState.getString(EXTRA_MEDIA_ID);
        }

        episodeImageContainer = findViewById(R.id.episodeImageContainer);
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
                        transportControls.play();
                        scheduleSeekbarUpdate();
                        break;
                    case PlaybackStateCompat.STATE_STOPPED:
                    case PlaybackStateCompat.STATE_NONE:
                        transportControls.playFromMediaId(requestedMediaId, null);
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

        replayButton = findViewById(R.id.replayButton);
        skipButton = findViewById(R.id.skipButton);

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSkipOrReplay(Constants.REPLAY_DURATION);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSkipOrReplay(Constants.SKIP_DURATION);
            }
        });

        positionText = findViewById(R.id.position);
        remainingText = findViewById(R.id.remaining);
        titleText = findViewById(R.id.title);

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MPOMediaService.class),
                connectionCallback,
                null); // optional Bundle

        if (!fromNotification) {
            episode = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_EPISODE));
            requestedMediaId = MediaHelper.createMediaId(episode);
            playOnStart = savedInstanceState == null;

            repository.getLiveShow(episode.showId).observe(this, new Observer<Show>() {
                @Override
                public void onChanged(@Nullable Show show) {
                    MediaPlayerActivity.this.show = show;
                    updateUIFromMedia();
                }
            });
        }

        player.setVideoSizeListener(MediaPlayerActivity.this);
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
        player.setVideoSizeListener(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(EXTRA_NOTIFICATION_MEDIA_ID)) {
            fromNotification = true;
            requestedMediaId = intent.getStringExtra(EXTRA_NOTIFICATION_MEDIA_ID);
            intent.removeExtra(EXTRA_NOTIFICATION_MEDIA_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_MEDIA_ID, requestedMediaId);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        player.setDisplay(null);
        surfaceView.setVisibility(View.GONE);
    }

    @Override
    public void onMediaVideoSizeDetermined(int width, int height) {
        updateMediaDisplay();
    }

    private void updateUIFromMedia() {
        Glide.with(this).load(show.artworkUrl).fitCenter().into(artworkImage);
        titleText.setText(episode.name);
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

    private void updateMediaDisplay() {
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();

        if (videoWidth == 0) {
            episodeImageContainer.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.GONE);
        } else {
            int surfaceWidth = findViewById(R.id.playerContent).getWidth();

            ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
            // Set the height of the SurfaceView to match the aspect ratio of the video
            // be sure to cast these as floats otherwise the calculation will likely be 0
            lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) surfaceWidth);

            Log.d("MPO", "surface view width: " + surfaceWidth);
            Log.d("MPO", "surface view height: " + lp.height);

            surfaceView.setLayoutParams(lp);
            surfaceView.setVisibility(View.VISIBLE);
            episodeImageContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void handleSkipOrReplay(int interval) {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController != null) {
            PlaybackStateCompat playbackState = mediaController.getPlaybackState();
            MediaMetadataCompat metadata = mediaController.getMetadata();
            if (playbackState != null && metadata != null) {
                int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                switch (playbackState.getState()) {
                    case PlaybackStateCompat.STATE_PLAYING:
                    case PlaybackStateCompat.STATE_PAUSED:
                    case PlaybackStateCompat.STATE_BUFFERING:
                    case PlaybackStateCompat.STATE_STOPPED:
                        int currentPosition = seekBar.getProgress();
                        int newPosition = currentPosition + interval;

                        if (newPosition < 0) {
                            newPosition = 0;
                        } else if (newPosition > duration) {
                            // Grace period for too much skipping?
                            if (currentPosition > duration - Constants.MEDIA_SKIP_GRACE_PERIOD) {
                                return;
                            }
                            newPosition = duration - Constants.MEDIA_SKIP_GRACE_PERIOD;
                        }
                        mediaController.getTransportControls().seekTo(newPosition * 1000);
                        break;

                }
            }
        }
    }
}
