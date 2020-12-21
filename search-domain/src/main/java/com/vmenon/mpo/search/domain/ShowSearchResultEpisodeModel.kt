package com.vmenon.mpo.search.domain

data class ShowSearchResultEpisodeModel(
    val name: String,
    val description: String?,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long,
    val artworkUrl: String?
)