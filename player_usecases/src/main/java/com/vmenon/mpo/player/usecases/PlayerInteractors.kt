package com.vmenon.mpo.player.usecases

data class PlayerInteractors(
    val connectPlayerClient: ConnectPlayerClient,
    val disconnectPlayerClient: DisconnectPlayerClient,
    val listenForPlayBackStateChanges: ListenForPlaybackStateChanges,
    val playMedia: PlayMedia,
    val togglePlaybackState: TogglePlaybackState,
    val skipPlayback: SkipPlayback,
    val seekToPosition: SeekToPosition,
    val handlePlayerNavigationRequest: HandlePlayerNavigationRequest
)