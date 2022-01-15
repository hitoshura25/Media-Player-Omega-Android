package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class SeekToPositionTest {
    private val playerEngine: MediaPlayerEngine = mock()

    @Test
    fun seekToPosition() = runBlockingTest {
        val useCase = SeekToPosition(playerEngine)
        useCase.invoke(100L)
        verify(playerEngine).seekTo(100L)
    }
}