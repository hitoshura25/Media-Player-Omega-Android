package com.vmenon.mpo.persistence.room.entity

data class EpisodeDetailsEntity(
    val episodeName: String,
    val description: String?,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long,
    val episodeArtworkUrl: String?,
    val filename: String?
)