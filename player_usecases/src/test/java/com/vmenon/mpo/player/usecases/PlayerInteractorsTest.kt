package com.vmenon.mpo.player.usecases

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class PlayerInteractorsTest {
    @Test
    fun interactors() {
        val connectPlayerClient: ConnectPlayerClient = mock()
        val disconnectPlayerClient: DisconnectPlayerClient = mock()
        val handlePlayerNavigationRequest: HandlePlayerNavigationRequest = mock()
        val listenForPlayBackStateChanges: ListenForPlaybackStateChanges = mock()
        val playMedia: PlayMedia = mock()
        val seekToPosition: SeekToPosition = mock()
        val skipPlayback: SkipPlayback = mock()
        val togglePlaybackState: TogglePlaybackState = mock()
        val interactors = PlayerInteractors(
            connectPlayerClient = connectPlayerClient,
            disconnectPlayerClient = disconnectPlayerClient,
            handlePlayerNavigationRequest = handlePlayerNavigationRequest,
            listenForPlayBackStateChanges = listenForPlayBackStateChanges,
            playMedia = playMedia,
            seekToPosition = seekToPosition,
            skipPlayback = skipPlayback,
            togglePlaybackState = togglePlaybackState
        )
        assertEquals(connectPlayerClient, interactors.connectPlayerClient)
        assertEquals(disconnectPlayerClient, interactors.disconnectPlayerClient)
        assertEquals(handlePlayerNavigationRequest, interactors.handlePlayerNavigationRequest)
        assertEquals(listenForPlayBackStateChanges, interactors.listenForPlayBackStateChanges)
        assertEquals(playMedia, interactors.playMedia)
        assertEquals(seekToPosition, interactors.seekToPosition)
        assertEquals(skipPlayback, interactors.skipPlayback)
        assertEquals(togglePlaybackState, interactors.togglePlaybackState)
    }
}