package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine
import com.vmenon.mpo.player.domain.PlaybackMediaRequest

class PlayMedia(private val playerEngine: MediaPlayerEngine) {
    suspend operator fun invoke(request: PlaybackMediaRequest) {
        playerEngine.play(request)
    }
}