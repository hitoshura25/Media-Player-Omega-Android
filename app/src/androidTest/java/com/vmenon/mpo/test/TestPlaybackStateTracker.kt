package com.vmenon.mpo.test

import com.vmenon.mpo.player.domain.PlaybackState
import com.vmenon.mpo.player.domain.PlaybackStateTracker

class TestPlaybackStateTracker : PlaybackStateTracker {
    val playbackStates = ArrayList<PlaybackState>()
    override fun receivedPlaybackState(playbackState: PlaybackState) {
        playbackStates.add(playbackState)
        println("Received playback state: $playbackState")
    }
}