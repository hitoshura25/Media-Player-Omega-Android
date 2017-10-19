package com.vmenon.mpo.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.vmenon.mpo.MPOApplication;
import com.vmenon.mpo.R;
import com.vmenon.mpo.activity.MediaPlayerActivity;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.persistence.MPORepository;
import com.vmenon.mpo.util.MediaHelper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MPOMediaService extends MediaBrowserServiceCompat implements MPOMediaPlayer.MediaPlayerListener,
        AudioManager.OnAudioFocusChangeListener {

    // The action of the incoming Intent indicating that it contains a command
    // to be executed (see {@link #onStartCommand})
    public static final String ACTION_CMD = "com.vmennon.mpo.core.ACTION_CMD";

    // The key in the extras of the incoming Intent indicating the command that
    // should be executed (see {@link #onStartCommand})
    public static final String CMD_NAME = "CMD_NAME";
    // A value of a CMD_NAME key in the extras of the incoming Intent that
    // indicates that the music playback should be paused (see {@link #onStartCommand})
    public static final String CMD_PAUSE = "CMD_PAUSE";

    // we don't have audio focus, and can't duck (play at a low volume)
    public static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    public static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    public static final int AUDIO_FOCUSED  = 2;

    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    public static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    public static final float VOLUME_NORMAL = 1.0f;

    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 30000;

    private static final String TAG = "MPOMediaService";
    private static final String MEDIA_ROOT_ID = "com.vmenon.mpo.media_root_id";
    private static final String EMPTY_MEDIA_ROOT_ID = "com.vmenon.mpo.empty_root_id";

    private static final int NOTIFICATION_ID = 414;
    private static final String NOTIFICATION_CHANNEL_ID = "com.vmenon.mpo.MUSIC_CHANNEL_ID";
    private static final int NOTIFICATION_REQUEST_CODE = 100;
    private static final String NOTIFICATION_ACTION_PAUSE = "com.vmenon.mpo.pause";
    private static final String NOTIFICATION_ACTION_PLAY = "com.vmenon.mpo.play";
    private static final String NOTIFICATION_ACTION_PREV = "com.vmenon.mpo.prev";
    private static final String NOTIFICATION_ACTION_NEXT = "com.vmenon.mpo.next";
    private static final String NOTIFICATION_ACTION_STOP = "com.vmenon.mpo.stop";

    @Inject
    protected MPORepository mpoRepository;

    @Inject
    protected MPOMediaPlayer mediaPlayer;

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private AudioManager audioManager;
    private NotificationManager notificationManager;

    private boolean serviceStarted = false;
    private boolean notificationStarted = false;
    private int audioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private int playbackState;
    private boolean playOnFocusGain;
    private boolean audioNoisyReceiverRegistered;
    private String requestedMediaId = "";
    private Bitmap currentMediaBitmap;
    private Bitmap placeholderMediaBitmap;

    private PendingIntent playIntent;
    private PendingIntent pauseIntent;
    private PendingIntent previousIntent;
    private PendingIntent nextIntent;
    private PendingIntent stopIntent;

    private DelayedStopHandler delayedStopHandler = new DelayedStopHandler(this);
    private IntentFilter audioNoisyIntentFilter =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private BroadcastReceiver audioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Log.d(TAG, "Headphones disconnected.");
                if (playOnFocusGain && mediaPlayer.isPlaying()) {
                    Intent i = new Intent(context, MPOMediaService.class);
                    i.setAction(MPOMediaService.ACTION_CMD);
                    i.putExtra(MPOMediaService.CMD_NAME, MPOMediaService.CMD_PAUSE);
                    startService(i);
                }
            }
        }
    };

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            MediaControllerCompat.TransportControls transportControls = mediaSession.getController()
                    .getTransportControls();
            Log.d(TAG, "Received intent with action " + action);
            switch (action) {
                case NOTIFICATION_ACTION_PAUSE:
                    transportControls.pause();
                    break;
                case NOTIFICATION_ACTION_PLAY:
                    transportControls.play();
                    break;
                case NOTIFICATION_ACTION_NEXT:
                    transportControls.skipToNext();
                    break;
                case NOTIFICATION_ACTION_PREV:
                    transportControls.skipToPrevious();
                    break;
                default:
                    Log.w(TAG, "Unknown intent ignored. Action=" + action);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ((MPOApplication) getApplication()).getAppComponent().inject(this);

        playbackState = PlaybackStateCompat.STATE_NONE;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String pkg = getPackageName();
        pauseIntent = PendingIntent.getBroadcast(this, NOTIFICATION_REQUEST_CODE,
                new Intent(NOTIFICATION_ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        playIntent = PendingIntent.getBroadcast(this, NOTIFICATION_REQUEST_CODE,
                new Intent(NOTIFICATION_ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        previousIntent = PendingIntent.getBroadcast(this, NOTIFICATION_REQUEST_CODE,
                new Intent(NOTIFICATION_ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        nextIntent = PendingIntent.getBroadcast(this, NOTIFICATION_REQUEST_CODE,
                new Intent(NOTIFICATION_ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        stopIntent = PendingIntent.getBroadcast(this, NOTIFICATION_REQUEST_CODE,
                new Intent(NOTIFICATION_ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll();

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        /*wifiLock = ((WifiManager) service.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "sample_lock");*/

        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new SessionCallback());
        setSessionToken(mediaSession.getSessionToken());
        mediaPlayer.setListener(this);

        Context context = getApplicationContext();
        Intent intent = new Intent(context, MediaPlayerActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setSessionActivity(pi);
        updatePlaybackState(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            String command = intent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        handlePauseRequest();
                    }
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
        // Service is being killed, so make sure we release our resources
        handleStopRequest(null);
        stopNotification();
        delayedStopHandler.removeCallbacksAndMessages(null);
        // Always release the MediaSession to clean up resources
        // and notify associated MediaController(s).
        mediaSession.release();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (allowBrowsing(clientPackageName, clientUid)) {
            return new BrowserRoot(MEDIA_ROOT_ID, null);
        } else {
            return new BrowserRoot(EMPTY_MEDIA_ROOT_ID, null);
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        if (TextUtils.equals(EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null);
            return;
        }

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (MEDIA_ROOT_ID.equals(parentId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems);

    }

    @Override
    public void onMediaPrepared() {
        configMediaPlayerState();
    }

    @Override
    public void onMediaFinished() {
        handleStopRequest(null);
    }

    @Override
    public void onMediaSeekFinished() {
        if (playbackState == PlaybackState.STATE_BUFFERING) {
            playbackState = PlaybackState.STATE_PLAYING;
        }
        updatePlaybackState(null);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange. focusChange=" + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // We have gained focus:
            audioFocus = AUDIO_FOCUSED;

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            audioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;

            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (playbackState == PlaybackState.STATE_PLAYING && !canDuck) {
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                playOnFocusGain = true;
            }
        } else {
            Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
        }
        configMediaPlayerState();
    }

    private boolean allowBrowsing(@NonNull String clientPackageName, int clientUid) {
        return false;
    }

    private void handlePlayRequest(File mediaFile) {
        delayedStopHandler.removeCallbacksAndMessages(null);

        if (!serviceStarted) {
            Log.v(TAG, "Starting service");
            // The MusicService needs to keep running even after the calling MediaBrowser
            // is disconnected. Call startService(Intent) and then stopSelf(..) when we no longer
            // need to play media.
            startService(new Intent(getApplicationContext(), MPOMediaService.class));
            serviceStarted = true;
        }

        if (!mediaSession.isActive()) {
            mediaSession.setActive(true);
        }

        playOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();
        boolean mediaHasChanged = mediaFile != null;

        if (playbackState == PlaybackState.STATE_PAUSED && !mediaHasChanged && mediaPlayer != null) {
            configMediaPlayerState();
        } else if (mediaFile != null) {
            playbackState = PlaybackState.STATE_STOPPED;
            relaxResources(false); // release everything except MediaPlayer

            playbackState = PlaybackState.STATE_BUFFERING;

            mediaPlayer.prepareForPlayback(mediaFile);

            // If we are streaming from the internet, we want to hold a
            // Wifi lock, which prevents the Wifi radio from going to
            // sleep while the song is playing.
            //mWifiLock.acquire();

            updatePlaybackState(null);
        }
    }

    private void updateNotification() {
        if (!notificationStarted) {
            // The notification must be updated after setting started to true
            Notification notification = createNotification();
            if (notification != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(NOTIFICATION_ACTION_NEXT);
                filter.addAction(NOTIFICATION_ACTION_PAUSE);
                filter.addAction(NOTIFICATION_ACTION_PLAY);
                filter.addAction(NOTIFICATION_ACTION_PREV);
                registerReceiver(notificationReceiver, filter);

                startForeground(NOTIFICATION_ID, notification);
                notificationStarted = true;
            }
        } else {
            Notification notification = createNotification();
            if (notification != null) {
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        }
    }

    private void stopNotification() {
        if (notificationStarted) {
            notificationStarted = false;
            try {
                notificationManager.cancel(NOTIFICATION_ID);
                unregisterReceiver(notificationReceiver);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            stopForeground(true);
        }
    }

    private void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=" + playbackState);
        if (playbackState == PlaybackState.STATE_PLAYING) {
            // Pause media player and cancel the 'foreground service' state.
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            // while paused, retain the MediaPlayer but give up audio focus
            relaxResources(false);
            giveUpAudioFocus();
        }
        playbackState = PlaybackState.STATE_PAUSED;
        updatePlaybackState(null);
        unregisterAudioNoisyReceiver();
        // reset the delayed stop handler.
        delayedStopHandler.removeCallbacksAndMessages(null);
        delayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
    }

    private void handleStopRequest(String withError) {
        Log.d(TAG, "handleStopRequest: mState=" + playbackState + " error=" + withError);
        mediaPlayer.stop();
        playbackState = PlaybackState.STATE_STOPPED;
        // Give up Audio focus
        giveUpAudioFocus();
        unregisterAudioNoisyReceiver();
        // Relax all resources
        relaxResources(true);

        /*if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }*/

        // reset the delayed stop handler.
        delayedStopHandler.removeCallbacksAndMessages(null);
        delayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);

        updatePlaybackState(withError);

        // service is no longer necessary. Will be started again if needed.
        stopSelf();
        serviceStarted = false;
    }

    private void updatePlaybackState(String error) {
        Log.d(TAG, "updatePlaybackState");
        long position = PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        int state = playbackState;
        if (mediaPlayer != null) {
            position = mediaPlayer.getCurrentPosition();
        }

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            state = PlaybackState.STATE_ERROR;
        }
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());
        mediaSession.setPlaybackState(stateBuilder.build());

        if (playbackState == PlaybackStateCompat.STATE_STOPPED ||
                playbackState == PlaybackStateCompat.STATE_NONE) {
            stopNotification();
        } else {
            updateNotification();
        }
    }

    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH;
        if (mediaPlayer.isPlaying()) {
            actions |= PlaybackState.ACTION_PAUSE;
        }

        return actions;
    }

    private void configMediaPlayerState() {
        Log.d("MPO", "configMediaPlayerState. mAudioFocus=" + audioFocus);
        if (audioFocus == MPOMediaService.AUDIO_NO_FOCUS_NO_DUCK) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (playbackState == PlaybackState.STATE_PLAYING) {
                handlePauseRequest();
            }
        } else {  // we have audio focus:
            if (audioFocus == MPOMediaService.AUDIO_NO_FOCUS_CAN_DUCK) {
                mediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            } else {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // we can be loud again
                } // else do something for remote client.
            }
            // If we were playing when we lost focus, we need to resume playing.
            if (playOnFocusGain) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    long currentPosition = mediaPlayer.getCurrentPosition();
                    Log.d("MPO","configMediaPlayerState startMediaPlayer. seeking to " +
                            currentPosition);
                    if (currentPosition == mediaPlayer.getCurrentPosition()) {
                        mediaPlayer.play();
                        playbackState = PlaybackState.STATE_PLAYING;
                    } else {
                        mediaPlayer.seekTo(currentPosition);
                        playbackState = PlaybackState.STATE_BUFFERING;
                    }
                }
                playOnFocusGain = false;
            }
        }
        updatePlaybackState(null);
    }

    private void tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus");
        if (audioFocus != AUDIO_FOCUSED) {
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                audioFocus = AUDIO_FOCUSED;
            }
        }
    }

    private void giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus");
        if (audioFocus == AUDIO_FOCUSED) {
            if (audioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                audioFocus = AUDIO_NO_FOCUS_NO_DUCK;
            }
        }
    }

    private void relaxResources(boolean releaseMediaPlayer) {
        Log.d(TAG, "relaxResources. releaseMediaPlayer=" + releaseMediaPlayer);

        stopForeground(true);

        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mediaPlayer != null) {
            mediaPlayer.cleanup();
        }

        // we can also release the Wifi lock, if we're holding it
        /*if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }*/
    }

    private void registerAudioNoisyReceiver() {
        if (!audioNoisyReceiverRegistered) {
            registerReceiver(audioNoisyReceiver, audioNoisyIntentFilter);
            audioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (audioNoisyReceiverRegistered) {
            unregisterReceiver(audioNoisyReceiver);
            audioNoisyReceiverRegistered = false;
        }
    }

    private void playEpisode(String mediaId, Episode episode, Show show) {
        if (requestedMediaId.equals(mediaId)) {
            File mediaFile = new File(episode.filename);
            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder().putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, show.name)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, show.author)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, episode.length)
                    .putString(MediaMetadataCompat.METADATA_KEY_GENRE, TextUtils.join(" ", show.genres))
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, show.artworkUrl)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.name)
                    .build();
            handlePlayRequest(mediaFile);
            mediaSession.setMetadata(metadata);
        } else {
            Log.w("MPO", "Cannot play incorrect media request: " + mediaId);
            return;
        }
    }

    private Notification createNotification() {
        MediaMetadataCompat mediaMetadata = mediaSession.getController().getMetadata();
        Log.d(TAG, "updateNotificationMetadata. mMetadata=" +  mediaMetadata);
        if (mediaMetadata == null) {
            return null;
        }

        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        final int playPauseButtonPosition = addNotificationActions(notificationBuilder);
        notificationBuilder
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        // show only play/pause in compact view
                        .setShowActionsInCompactView(playPauseButtonPosition)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopIntent)
                        .setMediaSession(getSessionToken()))
                .setDeleteIntent(stopIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_headset)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(createNotificationContentIntent())
                .setContentTitle(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        setNotificationPlaybackState(notificationBuilder);

        String fetchArtUrl = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);

        if (currentMediaBitmap != null) {
            notificationBuilder.setLargeIcon(currentMediaBitmap);
        } else {
            if (placeholderMediaBitmap == null) {
                placeholderMediaBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_headset);
            }
            notificationBuilder.setLargeIcon(placeholderMediaBitmap);
            if (fetchArtUrl != null) {
                Glide.with(this).load(fetchArtUrl).asBitmap().into(new ArtworkTarget(this, fetchArtUrl,
                        notificationBuilder));
            }
        }

        return notificationBuilder.build();
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
        Log.d(TAG, "updateNotificationPlaybackState. mPlaybackState=" + playbackState);
        if (!notificationStarted) {
            Log.d(TAG, "updateNotificationPlaybackState. cancelling notification!");
            stopForeground(true);
            return;
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(playbackState == PlaybackStateCompat.STATE_PLAYING);
    }

    private PendingIntent createNotificationContentIntent() {
        Intent openUI = new Intent(this, MediaPlayerActivity.class);
        openUI.putExtra(MediaPlayerActivity.EXTRA_NOTIFICATION_MEDIA_ID,
                mediaSession.getController().getMetadata().getString(
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, openUI,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private int addNotificationActions(final NotificationCompat.Builder notificationBuilder) {
        Log.d(TAG, "updatePlayPauseAction");

        int playPauseButtonPosition = 0;
        /* TODO: Previous
        // If skip to previous action is enabled
        if ((getAvailableActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_previous_white_24dp,
                    getString(R.string.label_previous), mPreviousIntent);

            // If there is a "skip to previous" button, the play/pause button will
            // be the second one. We need to keep track of it, because the MediaStyle notification
            // requires to specify the index of the buttons (actions) that should be visible
            // when in compact view.
            playPauseButtonPosition = 1;
        }
        */

        // Play or pause button, depending on the current state.
        final String label;
        final int icon;
        final PendingIntent intent;
        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            label = getString(R.string.pause);
            icon = R.drawable.ic_pause_circle_filled_white_48dp;
            intent = pauseIntent;
        } else {
            label = getString(R.string.play);
            icon = R.drawable.ic_play_circle_filled_white_48dp;
            intent = playIntent;
        }
        notificationBuilder.addAction(new NotificationCompat.Action(icon, label, intent));

        /* TODO: Prev/Next
        // If skip to next action is enabled
        if ((getAvailableActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_next_white_24dp,
                    mService.getString(R.string.label_next), mNextIntent);
        }
        */

        return playPauseButtonPosition;
    }

    private void handleNotificationArtwork(String artworkUrl, Bitmap bitmap,
                                           NotificationCompat.Builder notificationBuilder) {
        MediaMetadataCompat mediaMetadata = mediaSession.getController().getMetadata();
        String currentArtworkUrl = mediaMetadata != null ?
                mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) : null;

        if (currentArtworkUrl != null && currentArtworkUrl.equals(artworkUrl)) {
            // If the media is still the same, update the notification:
            Log.d(TAG, "handleNotificationArtwork: set bitmap to " + artworkUrl);
            currentMediaBitmap =  bitmap;
            notificationBuilder.setLargeIcon(bitmap);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            getString(R.string.notification_channel),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    getString(R.string.notification_channel_description));

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private class SessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            handlePlayRequest(null);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if (requestedMediaId.equals(mediaId)) {
                if (playbackState == PlaybackStateCompat.STATE_PLAYING ||
                        playbackState == PlaybackStateCompat.STATE_BUFFERING) {
                    updatePlaybackState(null);
                } else {
                    handlePlayRequest(null);
                }
                return;
            }

            MediaHelper.MediaType mediaType = MediaHelper.getMediaTypeFromMediaId(mediaId);
            if (MediaHelper.MEDIA_TYPE_EPISODE == mediaType.getMediaType()) {
                requestedMediaId = mediaId;
                currentMediaBitmap = null;
                mpoRepository.fetchEpisode(mediaType.getId(), new EpisodeDataHandler(
                        MPOMediaService.this, mediaId));
            } else {
                Log.w("MPO", "Unable to determine how to play media id: " + mediaId);
                return;
            }
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest(null);
        }

        @Override
        public void onSeekTo(long pos) {
            mediaPlayer.seekTo(pos);
            if (mediaPlayer.isPlaying()) {
                playbackState = PlaybackState.STATE_BUFFERING;
            }
            updatePlaybackState(null);
        }
    }

    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MPOMediaService> weakRefService;

        private DelayedStopHandler(MPOMediaService service) {
            weakRefService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MPOMediaService service = weakRefService.get();
            if (service != null && service.mediaPlayer != null) {
                if (service.mediaPlayer.isPlaying()) {
                    Log.d(TAG, "Ignoring delayed stop since the media player is in use.");
                    return;
                }
                Log.d(TAG, "Stopping service with delay handler.");
                service.stopSelf();
                service.serviceStarted = false;
            }
        }
    }

    private static class EpisodeDataHandler implements MPORepository.DataHandler<Episode> {
        WeakReference<MPOMediaService> serviceRef;
        String mediaId;

        EpisodeDataHandler(MPOMediaService service, String mediaId) {
            serviceRef = new WeakReference<>(service);
            this.mediaId = mediaId;
        }

        @Override
        public void onDataReady(Episode episode) {
            MPOMediaService service = serviceRef.get();
            if (service != null) {
                service.mpoRepository.fetchShow(episode.showId, new ShowDataHandler(service, mediaId, episode));
            }
        }
    }

    private static class ShowDataHandler implements MPORepository.DataHandler<Show> {
        WeakReference<MPOMediaService> serviceRef;
        Episode episode;
        String mediaId;

        ShowDataHandler(MPOMediaService service, String mediaId, Episode episode) {
            serviceRef = new WeakReference<>(service);
            this.mediaId = mediaId;
            this.episode = episode;
        }

        @Override
        public void onDataReady(Show show) {
            MPOMediaService service = serviceRef.get();
            if (service != null) {
                service.playEpisode(mediaId, episode, show);
            }
        }
    }

    private static class ArtworkTarget extends SimpleTarget<Bitmap> {
        WeakReference<MPOMediaService> serviceRef;
        NotificationCompat.Builder notificationBuilder;
        String artworkUrl;

        ArtworkTarget(MPOMediaService service, String artworkUrl, NotificationCompat.Builder builder) {
            super(500, 500);
            this.serviceRef = new WeakReference<>(service);
            this.artworkUrl = artworkUrl;
            this.notificationBuilder = builder;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            MPOMediaService service = serviceRef.get();
            if (service != null) {
                service.handleNotificationArtwork(artworkUrl, resource, notificationBuilder);
            }
        }
    }
}
