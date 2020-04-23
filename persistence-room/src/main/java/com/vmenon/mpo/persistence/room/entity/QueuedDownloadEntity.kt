package com.vmenon.mpo.persistence.room.entity

data class QueuedDownloadEntity(
    val download: DownloadEntity,
    val episode: EpisodeDetailsEntity,
    val show: ShowDetailsEntity,
    val total: Int,
    val progress: Int
)