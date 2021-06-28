package com.vmenon.mpo.core.usecases

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.domain.QueuedDownloadStatus

class RetryDownloads(
    private val downloadsService: DownloadsService,
    private val maxAttempts: Int,
    private val system: System
) {
    suspend operator fun invoke() {
        val queuedDownloads = downloadsService.getAllQueued()
        queuedDownloads.forEach { queuedDownload ->
            if (queuedDownload.status in listOf(
                    QueuedDownloadStatus.FAILED,
                    QueuedDownloadStatus.NOT_QUEUED
                ) && queuedDownload.download.downloadAttempt < maxAttempts
            ) {
                system.println("Retrying download: $queuedDownload")
                downloadsService.retryDownload(queuedDownload.download)
            }
        }
    }
}