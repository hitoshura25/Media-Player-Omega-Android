package com.vmenon.mpo.api

data class ShowDetails(
    val name: String,
    val description: String,
    val imageUrl: String,
    var episodes: List<Episode>
)
