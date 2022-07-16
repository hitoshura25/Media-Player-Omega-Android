package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlayerClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class DisconnectPlayerClientTest {
    private val playerClient: PlayerClient = mock()
    private val playerEngine: MediaPlayerEngine = mock()

    @Test
    fun disconnectPlayerClient() = runBlockingTest {
        val useCase = DisconnectPlayerClient(playerEngine)
        useCase.invoke(playerClient)
        verify(playerEngine).disconnectClient(playerClient)
    }
}