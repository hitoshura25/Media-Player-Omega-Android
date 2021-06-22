package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerClient
import com.vmenon.mpo.player.domain.MediaPlayerEngine

class DisconnectPlayerClient(private val playerEngine: MediaPlayerEngine) {
    suspend operator fun invoke(playerClient: PlayerClient) {
        playerEngine.disconnectClient(playerClient)
    }
}