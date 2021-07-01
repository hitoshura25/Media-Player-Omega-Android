package com.vmenon.mpo.player.usecases

import com.vmenon.mpo.navigation.domain.player.PlayerNavigationParams
import com.vmenon.mpo.player.domain.NavigationParamsConverter

class HandlePlayerNavigationRequest(
    private val converter: NavigationParamsConverter
) {
    suspend operator fun invoke(playerNavigationParams: PlayerNavigationParams) =
        converter.createPlaybackMediaRequest(playerNavigationParams)
}