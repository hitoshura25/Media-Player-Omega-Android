package com.vmenon.mpo.player.domain

import com.vmenon.mpo.navigation.domain.NavigationParams
import kotlinx.serialization.Serializable

@Serializable
data class PlayerNavigationParams(
    val playbackMediaRequest: PlaybackMediaRequest?
) : NavigationParams