package com.vmenon.mpo.downloads.domain

import com.vmenon.mpo.my_library.domain.EpisodeModel

interface DownloadsService {
    suspend fun queueDownload(episode: EpisodeModel): DownloadModel
    suspend fun getAllQueued(): List<QueuedDownloadModel>
    suspend fun notifyDownloadCompleted(downloadQueueId: Long)
}