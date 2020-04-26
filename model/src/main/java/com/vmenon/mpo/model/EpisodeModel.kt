package com.vmenon.mpo.model

data class EpisodeModel(
    val id: Long = 0L, // TODO: Can we avoid needing this...
    val name: String,
    val description: String,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long,
    val artworkUrl: String?,
    val filename: String? = null,
    val show: ShowModel
)