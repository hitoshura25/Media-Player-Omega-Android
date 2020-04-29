package com.vmenon.mpo.model

data class DownloadModel(
    val id: Long = 0L,
    val episode: EpisodeModel,
    val downloadManagerId: Long
)