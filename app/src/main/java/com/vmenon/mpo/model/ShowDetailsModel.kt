package com.vmenon.mpo.model

data class ShowDetailsModel(
    val name: String,
    val artworkUrl: String,
    val genres: List<String>,
    val author: String,
    val feedUrl: String
)
