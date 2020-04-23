package com.vmenom.mpo.model

data class DownloadModel(
    val id: Long = 0L,
    val episode: EpisodeModel,
    val downloadManagerId: Long,
    val total: Int,
    val progress: Int
)