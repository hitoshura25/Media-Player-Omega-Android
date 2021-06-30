package com.vmenon.mpo.core.usecases

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.domain.QueuedDownloadStatus
import com.vmenon.mpo.system.domain.Logger

class RetryDownloads(
    private val downloadsService: DownloadsService,
    private val maxAttempts: Int,
    private val logger: Logger
) {
    suspend operator fun invoke() {
        val queuedDownloads = downloadsService.getAllQueued()
        queuedDownloads.forEach { queuedDownload ->
            if (queuedDownload.status in listOf(
                    QueuedDownloadStatus.FAILED,
                    QueuedDownloadStatus.NOT_QUEUED
                ) && queuedDownload.download.downloadAttempt < maxAttempts
            ) {
                logger.println("Retrying download: $queuedDownload")
                downloadsService.retryDownload(queuedDownload.download)
            }
        }
    }
}