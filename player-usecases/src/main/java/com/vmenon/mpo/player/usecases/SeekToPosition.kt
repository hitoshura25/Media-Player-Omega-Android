package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerEngine

class SeekToPosition(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke(position: Long) {
        playerEngine.seekTo(position)
    }
}