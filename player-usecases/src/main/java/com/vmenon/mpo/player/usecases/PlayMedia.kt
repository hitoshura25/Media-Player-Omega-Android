package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine

class PlayMedia(private val playerEngine: MediaPlayerEngine) {
    suspend operator fun invoke(mediaId: String) {
        playerEngine.play(mediaId)
    }
}