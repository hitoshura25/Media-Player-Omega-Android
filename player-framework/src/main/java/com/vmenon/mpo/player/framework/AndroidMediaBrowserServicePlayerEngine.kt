package com.vmenon.mpo.player.framework

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.vmenon.mpo.player.domain.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidMediaBrowserServicePlayerEngine(
    private val context: Context,
    configuration: MPOMediaBrowserService.Configuration
) : PlayerEngine {
    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null
    private var connected = false

    init {
        MPOMediaBrowserService.configuration = configuration
    }

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

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {

        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        }
    }

    override suspend fun connectClient(playerClient: PlayerClient): Boolean {
        if (playerClient !is Activity) {
            throw IllegalArgumentException("playerClient needs to be an activity!")
        }
        connected = connectToSession()
        if (connected) {
            mediaBrowser?.let { mediaBrowser ->
                val mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
                MediaControllerCompat.setMediaController(playerClient, mediaController)
                mediaController.registerCallback(controllerCallback)
                this.mediaController = mediaController
            }
        }
        return connected
    }

    override suspend fun play(mediaId: String) {
        if (!connected) {
            throw IllegalStateException("Cannot play media before connected")
        }

        mediaController?.transportControls?.playFromMediaId(mediaId, null)
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
        if (MediaControllerCompat.getMediaController(playerClient as Activity) != null) {
            MediaControllerCompat.getMediaController(playerClient)
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
                        author = metadata.getString(MediaMetadataCompat.METADATA_KEY_AUTHOR)
                    ),
                    duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
                    position = playbackState.position,
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
}