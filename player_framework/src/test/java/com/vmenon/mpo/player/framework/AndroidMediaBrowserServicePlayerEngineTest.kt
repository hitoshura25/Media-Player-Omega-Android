package com.vmenon.mpo.player.framework

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.PlaybackState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AndroidMediaBrowserServicePlayerEngineTest {
    private val context: Context = mock()
    private val transportControls: MediaControllerCompat.TransportControls = mock()
    private val metadata: MediaMetadataCompat = mock {
        on { getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) }.doReturn("mediaId")
        on { getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) }.doReturn("uri")
        on { getString(MediaMetadataCompat.METADATA_KEY_TITLE) }.doReturn("title")
        on { getString(MediaMetadataCompat.METADATA_KEY_ALBUM) }.doReturn("album")
        on { getString(MediaMetadataCompat.METADATA_KEY_AUTHOR) }.doReturn("author")
        on { getLong(MediaMetadataCompat.METADATA_KEY_DURATION) }.doReturn(1000L)
    }
    private val playbackState: PlaybackStateCompat = mock()
    private val mediaController: MediaControllerCompat = mock {
        on { transportControls }.doReturn(transportControls)
        on { playbackState }.doReturn(playbackState)
        on { metadata }.doReturn(metadata)
    }

    private class TestAndroidMediaBrowserServicePlayerEngine(context: Context) :
        AndroidMediaBrowserServicePlayerEngine(context) {
        override fun createCallback(): MediaControllerCompat.Callback = mock()
    }

    private val engine = TestAndroidMediaBrowserServicePlayerEngine(context)

    @Test(expected = IllegalStateException::class)
    fun playThrowsIllegalStateExceptionIfNotConnected() = runTest {
        engine.connected = false
        engine.play(mock())
    }

    @Test
    fun resumeCallsTransportControlsIfNotNull() = runTest {
        engine.mediaController = mediaController
        engine.resume()
        verify(transportControls).play()
    }

    @Test
    fun resumeDoesNotCallTransportControlIfNull() = runTest {
        engine.mediaController = mediaController
        whenever(mediaController.transportControls).thenReturn(null)
        engine.resume()
        verifyNoInteractions(transportControls)
    }

    @Test
    fun resumeDoesNothingIfMediaControllerAndTransportControlsNull() = runTest {
        engine.resume()
        verifyNoInteractions(mediaController)
        verifyNoInteractions(transportControls)
    }

    @Test
    fun pauseCallsTransportControlsIfNotNull() = runTest {
        engine.mediaController = mediaController
        engine.pause()
        verify(transportControls).pause()
    }

    @Test
    fun pauseDoesNotCallTransportControlIfNull() = runTest {
        engine.mediaController = mediaController
        whenever(mediaController.transportControls).thenReturn(null)
        engine.pause()
        verifyNoInteractions(transportControls)
    }

    @Test
    fun pauseDoesNothingIfMediaControllerAndTransportControlsNull() = runTest {
        engine.pause()
        verifyNoInteractions(mediaController)
        verifyNoInteractions(transportControls)
    }

    @Test
    fun stopCallsTransportControlsIfNotNull() = runTest {
        engine.mediaController = mediaController
        engine.stop()
        verify(transportControls).stop()
    }

    @Test
    fun stopDoesNotCallTransportControlIfNull() = runTest {
        engine.mediaController = mediaController
        whenever(mediaController.transportControls).thenReturn(null)
        engine.stop()
        verifyNoInteractions(transportControls)
    }

    @Test
    fun stopDoesNothingIfMediaControllerAndTransportControlsNull() = runTest {
        engine.stop()
        verifyNoInteractions(mediaController)
        verifyNoInteractions(transportControls)
    }

    @Test
    fun seekToCallsTransportControlsIfNotNull() = runTest {
        engine.mediaController = mediaController
        engine.seekTo(100L)
        verify(transportControls).seekTo(100L)
    }

    @Test
    fun seekToDoesNotCallTransportControlIfNull() = runTest {
        engine.mediaController = mediaController
        whenever(mediaController.transportControls).thenReturn(null)
        engine.seekTo(100L)
        verifyNoInteractions(transportControls)
    }

    @Test
    fun seekToDoesNothingIfMediaControllerNull() = runTest {
        engine.seekTo(100L)
        verifyNoInteractions(mediaController)
        verifyNoInteractions(transportControls)
    }

    @Test
    fun currentPlaybackStateIsPlayingIfMediaControllerIsStatePlaying() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_PLAYING)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.PLAYING, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsBufferingIfMediaControllerIsStateBuffering() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_BUFFERING)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.BUFFERING, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsPausedIfMediaControllerIsStatePaused() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_PAUSED)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.PAUSED, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsStoppedIfMediaControllerIsStateStopped() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_STOPPED)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.STOPPED, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsFastForwardingIfMediaControllerIsStateFastForwarding() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_FAST_FORWARDING)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.FAST_FORWARDING, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsRewindingIfMediaControllerIsStateRewinding() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_REWINDING)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.REWINDING, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsErrorIfMediaControllerIsStateError() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_ERROR)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.ERROR, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsNoneIfMediaControllerIsStateNone() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_NONE)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.NONE, state!!.state)
    }

    @Test
    fun currentPlaybackStateIsUnknownIfMediaControllerIsStateConnecting() = runTest {
        engine.mediaController = mediaController
        whenever(playbackState.state).thenReturn(PlaybackStateCompat.STATE_CONNECTING)
        val state = engine.getCurrentPlaybackState()
        assertEquals(PlaybackState.State.UNKNOWN, state!!.state)
    }
}

