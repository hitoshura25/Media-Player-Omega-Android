package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlaybackState
import com.vmenon.mpo.player.domain.MediaPlayerEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.transformLatest

class ListenForPlaybackStateChanges(private val playerEngine: MediaPlayerEngine) {
    operator fun invoke() = playerEngine.playbackStateChanges.transformLatest{ playbackState ->
        emit(playbackState)
        val comparisonTime = System.currentTimeMillis()
        delay(100) // Initial delay
        while (true) {
            var estimatedPosition = playbackState.positionInMillis
            if (playbackState.state == PlaybackState.State.PLAYING) {
                // Calculate the elapsed time between the last position update and now and unless
                // paused, we can assume (delta * speed) + current position is approximately the
                // latest position. This ensure that we do not repeatedly query the MediaPlayerEngine
                val timeDelta = System.currentTimeMillis() - comparisonTime
                estimatedPosition += (timeDelta.toInt() * playbackState.playbackSpeed).toLong()
            }
            emit(playbackState.copy(positionInMillis = estimatedPosition))
            delay(1000L)
        }
    }
}