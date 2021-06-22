package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.PlaybackState.*

class TogglePlaybackState(private val playerEngine: MediaPlayerEngine) {
    suspend operator fun invoke(request: PlaybackMediaRequest) {
        val playbackState = playerEngine.getCurrentPlaybackState()
        when (playbackState?.state) {
            State.PLAYING, State.BUFFERING -> playerEngine.pause()
            State.STOPPED, State.NONE -> playerEngine.play(request)
            State.PAUSED -> playerEngine.resume()
            else -> {

            }
        }
    }
}