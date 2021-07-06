package com.vmenon.mpo.api.model

import java.io.Serializable

data class Show(
    val name: String,
    val artworkUrl: String,
    val genres: List<String>,
    val author: String,
    val feedUrl: String?
)  : Serializable
