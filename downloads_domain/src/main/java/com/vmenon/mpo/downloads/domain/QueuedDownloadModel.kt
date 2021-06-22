package com.vmenon.mpo.downloads.domain

data class QueuedDownloadModel(
    val download: DownloadModel,
    val total: Int,
    val progress: Int,
    val status: QueuedDownloadStatus
)