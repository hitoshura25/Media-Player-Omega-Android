package com.vmenon.mpo.model

data class QueuedDownloadModel(
    val download: DownloadModel,
    val episode: EpisodeModel,
    val show: ShowModel,
    val total: Int,
    val progress: Int
)