package com.vmenon.mpo.model

data class QueuedDownloadModel(
    val download: DownloadModel,
    val episode: EpisodeDetailsModel,
    val show: ShowDetailsModel,
    val total: Int,
    val progress: Int
)