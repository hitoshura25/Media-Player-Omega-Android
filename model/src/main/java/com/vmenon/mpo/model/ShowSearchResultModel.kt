package com.vmenon.mpo.model

data class ShowSearchResultModel(
    val id: Long = 0L, // TODO: Can we avoid needing this...
    val name: String,
    val artworkUrl: String?,
    val genres: List<String>,
    val author: String,
    val feedUrl: String,
    val description: String
)