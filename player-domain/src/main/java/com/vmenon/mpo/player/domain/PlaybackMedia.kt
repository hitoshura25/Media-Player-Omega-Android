package com.vmenon.mpo.player.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlaybackMedia(
    val mediaId: String,
    val durationInMillis: Long,
    val title: String? = null,
    val author: String? = null,
    val artworkUrl: String? = null,
    val album: String? = null,
    val genres: List<String>? = null
)