package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerEngine
import com.vmenon.mpo.player.domain.PlaybackState.*

class TogglePlaybackState(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke() {
        val playbackState = playerEngine.getCurrentPlaybackState()
        when (playbackState?.state) {
            State.PLAYING, State.BUFFERING -> playerEngine.pause()
            State.STOPPED, State.NONE -> playerEngine.play(playbackState.media.mediaId)
            State.PAUSED -> playerEngine.resume()
            else -> {

            }
        }
    }
}