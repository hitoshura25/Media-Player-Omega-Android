package com.vmenon.mpo.player.domain

interface PlaybackStateTracker {
    fun receivedPlaybackState(playbackState: PlaybackState)
}