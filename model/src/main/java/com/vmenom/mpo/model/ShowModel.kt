package com.vmenom.mpo.model

data class ShowModel(
    val id: Long = 0L, // TODO: Can we avoid needing this...
    val name: String,
    val artworkUrl: String,
    val genres: List<String>,
    val author: String,
    val feedUrl: String,
    val description: String,
    var lastUpdate: Long = -1L,
    var lastEpisodePublished: Long = -1L,
    val isSubscribed: Boolean = false
)
