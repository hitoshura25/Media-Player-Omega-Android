package com.vmenon.mpo.model

data class EpisodeDetailsModel(
    val episodeName: String,
    val description: String,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long,
    val episodeArtworkUrl: String?,
    var filename: String
)