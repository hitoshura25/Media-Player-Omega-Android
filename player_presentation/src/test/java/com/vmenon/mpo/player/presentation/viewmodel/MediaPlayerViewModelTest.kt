package com.vmenon.mpo.player.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vmenon.mpo.getOrAwaitValue
import com.vmenon.mpo.noValueExpected
import com.vmenon.mpo.player.domain.PlayerClient
import com.vmenon.mpo.player.usecases.ConnectPlayerClient
import com.vmenon.mpo.player.usecases.PlayerInteractors
import com.vmenon.mpo.player.usecases.SkipPlayback
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MediaPlayerViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    val skipPlayback: SkipPlayback = mock()
    val connectPlayerClient: ConnectPlayerClient = mock()
    val interactors: PlayerInteractors = mock {
        on { skipPlayback }.thenReturn(skipPlayback)
        on { connectPlayerClient }.thenReturn(connectPlayerClient)
    }
    val viewModel = MediaPlayerViewModel().apply {
        playerInteractors = interactors
    }

    @Test
    fun skipPlaybackShouldCallSkipPlaybackUseCase() = runTest {
        viewModel.skipPlayback(5L)
        verify(skipPlayback).invoke(5000L)
    }

    @Test
    fun ifConnectToPlayClientReturnsTrueThenEmitValue() = runTest {
        val playerClient: PlayerClient = mock()
        whenever(connectPlayerClient.invoke(playerClient)).thenReturn(true)
        val liveData = viewModel.connectClient(playerClient)
        assertEquals(Unit, liveData.getOrAwaitValue())
    }

    @Test
    fun ifConnectToPlayClientReturnsFalseThenEmitNoValue() = runTest {
        val playerClient: PlayerClient = mock()
        whenever(connectPlayerClient.invoke(playerClient)).thenReturn(false)
        val liveData = viewModel.connectClient(playerClient)
        liveData.noValueExpected()
    }
}