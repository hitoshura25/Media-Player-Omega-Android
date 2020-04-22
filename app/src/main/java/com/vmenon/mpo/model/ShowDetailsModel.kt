package com.vmenon.mpo.model

data class ShowDetailsModel(
    val showName: String,
    val showArtworkUrl: String,
    val genres: List<String>,
    val author: String,
    val feedUrl: String,
    val showDescription: String,
    var lastUpdate: Long = -1L,
    var lastEpisodePublished: Long = -1L,
    var isSubscribed: Boolean = false
)
