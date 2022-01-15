package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlaybackState
import com.vmenon.mpo.system.domain.Clock
import com.vmenon.mpo.system.domain.ThreadUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class ListForPlaybackStateChangesTest {
    private val playerEngine: MediaPlayerEngine = mock()

    @Test
    fun listenForChangesWhenEngineEmitsNewState() = runBlockingTest {
        val clock: Clock = mock()
        val threadUtil: ThreadUtil = object : ThreadUtil {
            override fun sleep(millis: Long) {
                Thread.sleep(millis)
            }

            override suspend fun delay(timeMillis: Long) {
                kotlinx.coroutines.delay(timeMillis)
            }
        }

        val playbackStates = listOf(
            TestData.playbackState.copy(
                state = PlaybackState.State.STOPPED,
                positionInMillis = 0L
            ),
            TestData.playbackState.copy(
                state = PlaybackState.State.PLAYING,
                positionInMillis = 100L
            ),
            TestData.playbackState.copy(
                state = PlaybackState.State.PAUSED,
                positionInMillis = 1000L
            )
        )

        val playbackStateChanges = flow {
            emit(playbackStates[0])
            emit(playbackStates[1])
            emit(playbackStates[2])
        }
        whenever(playerEngine.playbackStateChanges).thenReturn(playbackStateChanges)
        val useCase = ListenForPlaybackStateChanges(playerEngine, clock, threadUtil)
        val items = useCase.invoke().take(3).toList()
        assertEquals(playbackStates[0], items[0])
        assertEquals(playbackStates[1], items[1])
        assertEquals(playbackStates[2], items[2])
    }

    @Test
    fun listenForChangesEmitsNewStateWhenEstimationPosition() = runBlockingTest {
        val clock: Clock = mock()
        val threadUtil: ThreadUtil = object : ThreadUtil {
            override fun sleep(millis: Long) {
                Thread.sleep(millis)
            }

            override suspend fun delay(timeMillis: Long) {
                kotlinx.coroutines.delay(timeMillis)
            }
        }

        val playbackStates = listOf(
            TestData.playbackState.copy(
                state = PlaybackState.State.PLAYING,
                positionInMillis = 100L
            ),
            TestData.playbackState.copy(
                state = PlaybackState.State.PLAYING,
                positionInMillis = 200L
            ),
            TestData.playbackState.copy(
                state = PlaybackState.State.PLAYING,
                positionInMillis = 300L
            )
        )

        val playbackStateChanges = flow {
            emit(playbackStates[0])
        }
        whenever(clock.currentTimeMillis()).thenReturn(0L).thenReturn(100L).thenReturn(200L)
        whenever(playerEngine.playbackStateChanges).thenReturn(playbackStateChanges)
        val useCase = ListenForPlaybackStateChanges(playerEngine, clock, threadUtil)
        val items = useCase.invoke().take(3).toList()
        assertEquals(playbackStates[0], items[0])
        assertEquals(playbackStates[1], items[1])
        assertEquals(playbackStates[2], items[2])
    }

    @Test
    fun listenForChangesEmitsSameStateIfNotPlaying() = runBlockingTest {
        val clock: Clock = mock()
        val threadUtil: ThreadUtil = object : ThreadUtil {
            override fun sleep(millis: Long) {
                Thread.sleep(millis)
            }

            override suspend fun delay(timeMillis: Long) {
                kotlinx.coroutines.delay(timeMillis)
            }
        }

        val playbackState = TestData.playbackState.copy(
            state = PlaybackState.State.STOPPED,
            positionInMillis = 100L
        )

        val playbackStateChanges = flow {
            emit(playbackState)
        }
        whenever(playerEngine.playbackStateChanges).thenReturn(playbackStateChanges)
        val useCase = ListenForPlaybackStateChanges(playerEngine, clock, threadUtil)
        val items = useCase.invoke().take(3).toList()
        assertEquals(playbackState, items[0])
        assertEquals(playbackState, items[1])
        assertEquals(playbackState, items[2])
    }
}