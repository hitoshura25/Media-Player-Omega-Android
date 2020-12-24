package com.vmenon.mpo.downloads.data

data class DownloadQueueItem(
    val queueId: Long,
    val totalSize: Int,
    val downloaded: Int
)