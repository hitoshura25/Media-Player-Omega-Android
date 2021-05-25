package com.vmenon.mpo.my_library.domain

import com.vmenon.mpo.navigation.domain.NavigationParams
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeDetailsParams(val episodeId: Long) : NavigationParams