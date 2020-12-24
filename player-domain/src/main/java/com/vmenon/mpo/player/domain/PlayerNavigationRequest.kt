package com.vmenon.mpo.player.domain

import com.vmenon.mpo.navigation.domain.NavigationRequest

data class PlayerNavigationRequest(
    override val destination: PlayerNavigationDestination,
    override val params: PlayerNavigationParams
) : NavigationRequest<PlayerNavigationDestination, PlayerNavigationParams>