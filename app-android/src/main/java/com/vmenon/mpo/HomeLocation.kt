package com.vmenon.mpo

import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationParams
import com.vmenon.mpo.player.domain.PlaybackMediaRequest

data class HomeNavigationParams(
    val playbackMediaRequest: PlaybackMediaRequest?
) : NavigationParams

object HomeLocation : NavigationLocation<HomeNavigationParams>