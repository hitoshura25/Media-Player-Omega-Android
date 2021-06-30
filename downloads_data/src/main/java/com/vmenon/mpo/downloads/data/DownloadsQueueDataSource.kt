package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.downloads.domain.DownloadRequest

interface DownloadsQueueDataSource {
    suspend fun getAllQueued(queueIds:Collection<Long>): List<DownloadQueueItem>
    suspend fun queueDownloadAndGetQueueId(downloadRequest: DownloadRequest): Long
}