package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerEngine
import com.vmenon.mpo.player.domain.State

// Don't keep skipping past after this to prevent accidentally completing media
const val MEDIA_SKIP_GRACE_PERIOD = 5

class SkipPlayback(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke(amount: Long) {
        val playbackState = playerEngine.getCurrentPlaybackState()
        if (playbackState != null) {
            when (playbackState.state) {
                State.PLAYING, State.PAUSED, State.BUFFERING, State.STOPPED -> {
                    var newPosition = playbackState.position + amount
                    if (newPosition < 0) {
                        newPosition = 0
                    } else if (newPosition > playbackState.duration) {
                        // Grace period for too much skipping?
                        if (playbackState.position > playbackState.duration - MEDIA_SKIP_GRACE_PERIOD) {
                            return
                        }
                        newPosition = playbackState.duration - MEDIA_SKIP_GRACE_PERIOD
                    }
                    playerEngine.seekTo((newPosition * 1000))
                }
                else -> {
                }
            }
        }
    }
}