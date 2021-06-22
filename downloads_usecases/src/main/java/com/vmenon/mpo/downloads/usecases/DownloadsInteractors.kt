package com.vmenon.mpo.downloads.usecases

data class DownloadsInteractors(
    val queuedDownloads: GetQueuedDownloads,
    val notifyDownloadCompleted: NotifyDownloadCompleted,
    val retryDownloads: RetryDownloads
)