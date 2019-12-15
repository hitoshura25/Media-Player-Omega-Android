package com.vmenon.mpo.model

data class ShowModel(
    val name: String,
    val artworkUrl: String,
    val genres: List<String>,
    val author: String,
    val feedUrl: String
)
