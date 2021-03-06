package com.vmenon.mpo.my_library.domain

data class EpisodeModel(
    val id: Long = 0L, // TODO: Can we avoid needing this...
    val name: String,
    val description: String?,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val lengthInSeconds: Long?,
    val artworkUrl: String?,
    val filename: String? = null,
    val show: ShowModel
)