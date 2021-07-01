package com.vmenon.mpo.navigation.domain.player

import java.io.Serializable

data class Media(
    val mediaId: String,
    val mediaSource: MediaSource,
    val title: String? = null,
    val author: String? = null,
    val artworkUrl: String? = null,
    val album: String? = null,
    val genres: List<String>? = null
) : Serializable