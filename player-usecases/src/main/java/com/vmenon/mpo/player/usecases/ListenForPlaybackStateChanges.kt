package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class ListenForPlaybackStateChanges(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke() = flow {
        while (true) {
            playerEngine.getCurrentPlaybackState()?.let { playbackState ->
                emit(playbackState)
            }
            delay(1000L)
        }
    }
}