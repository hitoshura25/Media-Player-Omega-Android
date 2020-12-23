package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerEngine
import com.vmenon.mpo.player.domain.State

class TogglePlaybackState(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke() {
        val playbackState = playerEngine.getCurrentPlaybackState()
        when (playbackState?.state) {
            State.PLAYING, State.BUFFERING -> playerEngine.stop()
            State.STOPPED, State.NONE ->  playerEngine.resume()
            State.PAUSED -> playerEngine.resume()
            else -> {

            }
        }
    }
}