package com.vmenon.mpo.api.model

data class Show(
    val name: String,
    val artworkUrl: String,
    val genres: List<String>,
    val author: String,
    val feedUrl: String?
)
