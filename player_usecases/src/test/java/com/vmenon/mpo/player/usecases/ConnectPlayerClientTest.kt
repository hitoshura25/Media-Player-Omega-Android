package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlayerClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ConnectPlayerClientTest {
    private val playerEngine: MediaPlayerEngine = mock()
    private val playerClient: PlayerClient = mock()

    @Test
    fun connectPlayerClient() = runBlockingTest {
        val usecase = ConnectPlayerClient(playerEngine)
        whenever(playerEngine.connectClient(playerClient)).thenReturn(true)
        assertTrue(usecase.invoke(playerClient))
    }
}