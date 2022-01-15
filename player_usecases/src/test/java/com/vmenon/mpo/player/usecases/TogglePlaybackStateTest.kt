package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlaybackState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class TogglePlaybackStateTest {
    private val playerEngine: MediaPlayerEngine = mock()

    @Test
    fun pausesIfCurrentlyPlaying() = runBlockingTest {
        val request = TestData.playbackMediaRequest
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.PLAYING)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        val usecase = TogglePlaybackState(playerEngine)
        usecase.invoke(request)
        verify(playerEngine).pause()
    }

    @Test
    fun pausesIfCurrentlyBuffering() = runBlockingTest {
        val request = TestData.playbackMediaRequest
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.BUFFERING)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        val usecase = TogglePlaybackState(playerEngine)
        usecase.invoke(request)
        verify(playerEngine).pause()
    }

    @Test
    fun resumesIfCurrentlyPaused() = runBlockingTest {
        val request = TestData.playbackMediaRequest
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.PAUSED)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        val usecase = TogglePlaybackState(playerEngine)
        usecase.invoke(request)
        verify(playerEngine).resume()
    }

    @Test
    fun playsRequestIfCurrentlyStopped() = runBlockingTest {
        val request = TestData.playbackMediaRequest
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.STOPPED)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        val usecase = TogglePlaybackState(playerEngine)
        usecase.invoke(request)
        verify(playerEngine).play(request)
    }

    @Test
    fun playsRequestIfCurrentlyNoState() = runBlockingTest {
        val request = TestData.playbackMediaRequest
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.NONE)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        val usecase = TogglePlaybackState(playerEngine)
        usecase.invoke(request)
        verify(playerEngine).play(request)
    }

    @Test
    fun doesNothingIfStateIsUnknown() = runBlockingTest {
        val request = TestData.playbackMediaRequest
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.UNKNOWN)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        val usecase = TogglePlaybackState(playerEngine)
        usecase.invoke(request)
        verify(playerEngine).getCurrentPlaybackState()
        verifyNoMoreInteractions(playerEngine)
    }


    @Test
    fun doesNothingIfStateIsNull() = runBlockingTest {
        val request = TestData.playbackMediaRequest
        val usecase = TogglePlaybackState(playerEngine)
        usecase.invoke(request)
        verify(playerEngine).getCurrentPlaybackState()
        verifyNoMoreInteractions(playerEngine)
    }
}