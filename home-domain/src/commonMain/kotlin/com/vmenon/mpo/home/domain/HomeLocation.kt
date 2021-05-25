package com.vmenon.mpo.home.domain

import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationParams
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import kotlinx.serialization.Serializable

@Serializable
data class HomeNavigationParams(
    val playbackMediaRequest: PlaybackMediaRequest? = null
) : NavigationParams

object HomeLocation : NavigationLocation<HomeNavigationParams>