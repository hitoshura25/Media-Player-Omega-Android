package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.player.domain.PlayerClient
import com.vmenon.mpo.player.domain.PlayerEngine

class ConnectPlayerClient(private val playerEngine: PlayerEngine) {
    suspend operator fun invoke(playerClient: PlayerClient): Boolean =
        playerEngine.connectClient(playerClient)
}