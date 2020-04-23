package com.vmenom.mpo.model

data class ShowSearchResultEpisodeModel(
    val name: String,
    val description: String,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long,
    val artworkUrl: String?
)