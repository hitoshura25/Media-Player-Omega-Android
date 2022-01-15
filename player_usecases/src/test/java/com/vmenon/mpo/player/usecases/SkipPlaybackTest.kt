package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlaybackState
import com.vmenon.mpo.player.usecases.SkipPlayback.Companion.MEDIA_SKIP_GRACE_PERIOD_MS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class SkipPlaybackTest {
    private val playerEngine: MediaPlayerEngine = mock()

    @Test
    fun skipPlaybackDoesNothingIfPlaybackStateIsNull() = runBlockingTest {
        val useCase = SkipPlayback(playerEngine)
        useCase.invoke(100L)
        verify(playerEngine, times(0)).seekTo(any())
    }

    @Test
    fun skipPlaybackDoesNothingIfPlaybackStateIsNotValid() = runBlockingTest {
        val useCase = SkipPlayback(playerEngine)
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.NONE)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        useCase.invoke(100L)
        verify(playerEngine, times(0)).seekTo(any())
    }

    @Test
    fun skipPlaybackIfCurrentlyPlayingAndWithinGracePeriod() = runBlockingTest {
        val useCase = SkipPlayback(playerEngine)
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.PLAYING)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        useCase.invoke(100L)
        verify(playerEngine).seekTo(100L)
    }

    @Test
    fun skipPlaybackSeeksToEndWithGracePeriodIfCurrentlyPlayingWithinGracePeriod() = runBlockingTest {
        val useCase = SkipPlayback(playerEngine)
        val playbackState = TestData.playbackState.copy(
            state = PlaybackState.State.PLAYING,
            positionInMillis = TestData.playbackMedia.durationInMillis - MEDIA_SKIP_GRACE_PERIOD_MS
        )
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        useCase.invoke((MEDIA_SKIP_GRACE_PERIOD_MS + 1).toLong())
        verify(playerEngine).seekTo(any())
    }

    @Test
    fun skipPlaybackDoesNothingIfCurrentlyPlayingAndNotWithinGracePeriod() = runBlockingTest {
        val useCase = SkipPlayback(playerEngine)
        val playbackState = TestData.playbackState.copy(
            state = PlaybackState.State.PLAYING,
            positionInMillis = (TestData.playbackMedia.durationInMillis - MEDIA_SKIP_GRACE_PERIOD_MS) + 1
        )
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        useCase.invoke((MEDIA_SKIP_GRACE_PERIOD_MS + 1).toLong())
        verify(playerEngine, times(0)).seekTo(any())
    }

    @Test
    fun skipPlaybackSeeksToStartIfAmountTooLarge() = runBlockingTest {
        val useCase = SkipPlayback(playerEngine)
        val playbackState = TestData.playbackState.copy(state = PlaybackState.State.PLAYING)
        whenever(playerEngine.getCurrentPlaybackState()).thenReturn(playbackState)
        useCase.invoke(-100L)
        verify(playerEngine).seekTo(0L)
    }
}