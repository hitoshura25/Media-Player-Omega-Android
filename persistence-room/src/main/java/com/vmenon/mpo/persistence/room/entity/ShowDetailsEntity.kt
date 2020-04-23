package com.vmenon.mpo.persistence.room.entity

data class ShowDetailsEntity(
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
