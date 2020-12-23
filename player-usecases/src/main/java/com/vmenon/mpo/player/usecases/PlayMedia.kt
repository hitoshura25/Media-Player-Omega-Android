package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerEngine

class PlayMedia(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke(mediaId: String) {
        playerEngine.play(mediaId)
    }
}