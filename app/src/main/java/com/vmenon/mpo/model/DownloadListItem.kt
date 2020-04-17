package com.vmenon.mpo.model

data class DownloadListItem(
    val download: DownloadModel,
    val episode: EpisodeModel,
    val show: ShowModel,
    val total: Int,
    val progress: Int
)