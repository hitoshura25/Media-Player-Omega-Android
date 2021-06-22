package com.vmenon.mpo.api.model

data class Episode(
    val name: String,
    val description: String?,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long?,
    val artworkUrl: String?
)