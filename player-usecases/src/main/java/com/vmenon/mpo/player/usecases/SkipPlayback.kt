package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerEngine
import com.vmenon.mpo.player.domain.State

class SkipPlayback(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke(amountMillis: Long) {
        val playbackState = playerEngine.getCurrentPlaybackState()
        if (playbackState != null) {
            when (playbackState.state) {
                State.PLAYING, State.PAUSED, State.BUFFERING, State.STOPPED -> {
                    var newPosition = playbackState.positionInMillis + amountMillis
                    if (newPosition < 0) {
                        newPosition = 0
                    } else if (newPosition > playbackState.durationInMillis) {
                        // Grace period for too much skipping?
                        if (playbackState.positionInMillis > playbackState.durationInMillis - MEDIA_SKIP_GRACE_PERIOD_MS) {
                            return
                        }
                        newPosition = playbackState.durationInMillis - MEDIA_SKIP_GRACE_PERIOD_MS
                    }
                    playerEngine.seekTo(newPosition)
                }
                else -> {
                }
            }
        }
    }

    companion object {
        // Don't keep skipping past after this to prevent accidentally completing media
        const val MEDIA_SKIP_GRACE_PERIOD_MS = 5000
    }
}