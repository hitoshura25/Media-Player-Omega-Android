package com.vmenon.mpo.my_library.domain

data class ShowModel(
    val id: Long = 0L, // TODO: Can we avoid needing this...
    val name: String,
    val artworkUrl: String?,
    val genres: List<String>,
    val author: String,
    val feedUrl: String,
    val description: String,
    val lastUpdate: Long,
    val lastEpisodePublished: Long,
    val isSubscribed: Boolean = false
)
