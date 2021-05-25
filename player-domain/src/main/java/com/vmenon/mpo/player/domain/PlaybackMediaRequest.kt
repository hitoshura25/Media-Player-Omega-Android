package com.vmenon.mpo.player.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlaybackMediaRequest(val media: PlaybackMedia, val mediaFile: String?)