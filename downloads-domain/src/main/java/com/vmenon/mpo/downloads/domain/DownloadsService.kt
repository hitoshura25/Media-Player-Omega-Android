package com.vmenon.mpo.downloads.domain

interface DownloadsService {
    suspend fun queueDownload(downloadRequest: DownloadRequest): DownloadModel
    suspend fun getAllQueued(): List<QueuedDownloadModel>
    suspend fun getCompletedDownloadByQueueId(queueId: Long): CompletedDownloadModel
    suspend fun delete(id: Long)
}