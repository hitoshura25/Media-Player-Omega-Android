package com.vmenon.mpo.player.domain

import com.vmenon.mpo.navigation.domain.player.PlayerNavigationParams

interface NavigationParamsConverter {
   suspend fun createPlaybackMediaRequest(params: PlayerNavigationParams): PlaybackMediaRequest?
}