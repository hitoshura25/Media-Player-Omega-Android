package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.downloads.domain.QueuedDownloadStatus

data class DownloadQueueItem(
    val queueId: Long,
    val totalSize: Int,
    val downloaded: Int,
    val status: QueuedDownloadStatus
)