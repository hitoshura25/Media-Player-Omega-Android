package com.vmenon.mpo.player.domain

import java.io.Serializable

data class PlaybackMediaRequest(val media: PlaybackMedia, val mediaFile: String?) : Serializable