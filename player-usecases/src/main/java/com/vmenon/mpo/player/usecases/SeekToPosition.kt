package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.MediaPlayerEngine

class SeekToPosition(private val playerEngine: MediaPlayerEngine) {
    suspend operator fun invoke(position: Long) {
        playerEngine.seekTo(position)
    }
}