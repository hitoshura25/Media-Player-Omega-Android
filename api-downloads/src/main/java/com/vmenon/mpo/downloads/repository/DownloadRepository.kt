package com.vmenon.mpo.downloads.repository

import com.vmenon.mpo.model.*

interface DownloadRepository {
    suspend fun getAllQueued(): List<QueuedDownloadModel>
    suspend fun queueDownload(episode: EpisodeModel): DownloadModel
    suspend fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ): DownloadModel

    suspend fun notifyDownloadCompleted(downloadManagerId: Long)
}