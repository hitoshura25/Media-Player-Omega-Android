package com.vmenon.mpo.core;

import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.vmenon.mpo.MPOApplication;
import com.vmenon.mpo.R;
import com.vmenon.mpo.activity.MediaPlayerActivity;
import com.vmenon.mpo.api.Episode;
import com.vmenon.mpo.api.Show;
import com.vmenon.mpo.core.persistence.MPORepository;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MPOMediaService extends MediaBrowserServiceCompat implements MPOMediaPlayer.MediaPlayerListener,
        AudioManager.OnAudioFocusChangeListener {

    public static String createMediaId(Episode episode) {
        return EPISODE_MEDIA_PREFIX + ":" + episode.id;
    }

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

    private static final String EPISODE_MEDIA_PREFIX = "episode";

    @Inject
    protected MPORepository mpoRepository;

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private MPOMediaPlayer mediaPlayer;
    private AudioManager audioManager;

    private boolean serviceStarted = false;
    private int audioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private int playbackState;
    private boolean playOnFocusGain;
    private boolean audioNoisyReceiverRegistered;
    private String requestedMediaId = "";

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

    @Override
    public void onCreate() {
        super.onCreate();
        ((MPOApplication) getApplication()).getAppComponent().inject(this);

        playbackState = PlaybackStateCompat.STATE_NONE;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        mediaPlayer = new MPOMediaPlayer();
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

        /** TODO
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();
         **/

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

    private void startNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MPOMediaService.this);

        builder
                // Add the metadata for the currently playing track
                .setContentTitle("the title")
                .setContentText("the subtitle")
                .setSubText("the description")
                .setLargeIcon(null)

                // Enable launching the player by clicking the notification
                .setContentIntent(mediaSession.getController().getSessionActivity())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(MPOMediaService.this,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.drawable.ic_headset)
                .setColor(ContextCompat.getColor(MPOMediaService.this, R.color.colorPrimary))

                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_play_arrow_white_48dp, getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(MPOMediaService.this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))

                // Take advantage of MediaStyle features
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                                MPOMediaService.this, PlaybackStateCompat.ACTION_STOP)));

        // Display the notification and place the service in the foreground
        startForeground(100, builder.build());
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

        if (state == PlaybackState.STATE_PLAYING || state == PlaybackState.STATE_PAUSED) {
            startNotification();
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
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, episode.artworkUrl)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.name)
                    .build();
            handlePlayRequest(mediaFile);
            mediaSession.setMetadata(metadata);
        } else {
            Log.w("MPO", "Cannot play incorrect media request: " + mediaId);
            return;
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

            String[] mediaIdParts = mediaId.split(":");
            if (EPISODE_MEDIA_PREFIX.equals(mediaIdParts[0])) {
                long episodeId = Long.parseLong(mediaIdParts[1]);
                requestedMediaId = mediaId;
                mpoRepository.fetchEpisode(episodeId, new EpisodeDataHandler(MPOMediaService.this, mediaId));
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
}
