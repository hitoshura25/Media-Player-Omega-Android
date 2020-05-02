package com.vmenon.mpo.model

data class QueuedDownloadModel(
    val download: DownloadModel,
    val total: Int,
    val progress: Int
)