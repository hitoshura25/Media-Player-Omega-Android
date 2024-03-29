package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class PlayMediaTest {
    private val playerEngine: MediaPlayerEngine = mock()
    @Test
    fun playMedia() = runBlockingTest {
        val useCase = PlayMedia(playerEngine)
        useCase.invoke(TestData.playbackMediaRequest)
        verify(playerEngine).play(TestData.playbackMediaRequest)
    }
}