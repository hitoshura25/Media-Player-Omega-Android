package com.vmenon.mpo.player.framework

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import com.vmenon.mpo.player.domain.*
import com.vmenon.mpo.player.domain.PlaybackState.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidMediaBrowserServicePlayerEngine(
    private val context: Context,
    configuration: MPOMediaBrowserService.Configuration
) : MediaPlayerEngine {
    init {
        MPOMediaBrowserService.configuration = configuration
    }

    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null
    private var connected = false

    private val mainScope = MainScope()

    private suspend fun connectToSession(): Boolean =
        suspendCoroutine { cont ->
            val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    cont.resume(true)
                }
            }
            val mediaBrowser = MediaBrowserCompat(
                context,
                ComponentName(context, MPOMediaBrowserService::class.java),
                connectionCallback,
                null
            )
            mediaBrowser.connect()
            AndroidXMediaPlayerEngine@ this.mediaBrowser = mediaBrowser
        }

    private val playbackStateFlow = MutableSharedFlow<PlaybackState>()
    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mainScope.launch { sendPlaybackState() }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            mainScope.launch { sendPlaybackState() }
        }
    }

    override suspend fun connectClient(playerClient: PlayerClient): Boolean {
        val activity = getActivityFromClient(playerClient)
        connected = connectToSession()
        if (connected) {
            mediaBrowser?.let { mediaBrowser ->
                val mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
                MediaControllerCompat.setMediaController(activity, mediaController)
                mediaController.registerCallback(controllerCallback)
                this.mediaController = mediaController
            }
            // Emit a PlaybackState in the event there were no metadata or playback state changes
            sendPlaybackState()
        }
        return connected
    }

    override suspend fun play(request: PlaybackMediaRequest) {
        if (!connected) {
            throw IllegalStateException("Cannot play media before connected")
        }

        val bundle = Bundle()
        bundle.putSerializable(
            MPOMediaBrowserService.PLAYBACK_MEDIA_REQUEST_EXTRA,
            Json.encodeToString(request)
        )
        mediaController?.transportControls?.playFromMediaId(request.media.mediaId, bundle)
    }

    override suspend fun resume() {
        mediaController?.transportControls?.play()
    }

    override suspend fun pause() {
        mediaController?.transportControls?.pause()
    }

    override suspend fun stop() {
        mediaController?.transportControls?.stop()
    }

    override suspend fun seekTo(position: Long) {
        mediaController?.transportControls?.seekTo(position)
    }

    override suspend fun disconnectClient(playerClient: PlayerClient) {
        val activity = getActivityFromClient(playerClient)
        if (MediaControllerCompat.getMediaController(activity) != null) {
            MediaControllerCompat.getMediaController(activity)
                .unregisterCallback(controllerCallback)
        }

        mediaBrowser?.disconnect()
        mediaBrowser = null
        connected = false
    }

    override suspend fun getCurrentPlaybackState(): PlaybackState? =
        mediaController?.let { controller ->
            val metadata = controller.metadata
            val playbackState = controller.playbackState
            if (metadata != null && playbackState != null) {
                return PlaybackState(
                    media = PlaybackMedia(
                        mediaId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                        artworkUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
                        title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                        album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM),
                        author = metadata.getString(MediaMetadataCompat.METADATA_KEY_AUTHOR),
                        durationInMillis = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                    ),
                    positionInMillis = playbackState.position,
                    playbackSpeed = playbackState.playbackSpeed,
                    state = when (playbackState.state) {
                        PlaybackStateCompat.STATE_PLAYING -> State.PLAYING
                        PlaybackStateCompat.STATE_BUFFERING -> State.BUFFERING
                        PlaybackStateCompat.STATE_PAUSED -> State.PAUSED
                        PlaybackStateCompat.STATE_STOPPED -> State.STOPPED
                        PlaybackStateCompat.STATE_FAST_FORWARDING -> State.FAST_FORWARDING
                        PlaybackStateCompat.STATE_REWINDING -> State.REWINDING
                        PlaybackStateCompat.STATE_ERROR -> State.ERROR
                        PlaybackStateCompat.STATE_NONE -> State.NONE
                        else -> State.UNKNOWN
                    }
                )
            }
            return null
        }

    override val playbackStateChanges: Flow<PlaybackState>
        get() = playbackStateFlow.asSharedFlow()

    private suspend fun sendPlaybackState() {
        getCurrentPlaybackState()?.let { playbackStateFlow.emit(it) }
    }

    private fun getActivityFromClient(playerClient: PlayerClient) = when (playerClient) {
        is Activity -> playerClient
        is Fragment -> playerClient.requireActivity()
        else -> throw IllegalArgumentException("playerClient needs to be an activity or fragment!")
    }
}