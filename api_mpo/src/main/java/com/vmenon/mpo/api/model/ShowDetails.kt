package com.vmenon.mpo.api.model

import java.io.Serializable

data class ShowDetails(
    val name: String,
    val description: String?,
    val imageUrl: String,
    val episodes: List<Episode>
)  : Serializable
