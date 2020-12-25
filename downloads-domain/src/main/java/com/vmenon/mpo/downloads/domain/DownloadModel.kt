package com.vmenon.mpo.downloads.domain

data class DownloadModel(
    val id: Long = 0L,
    val name: String,
    val downloadUrl: String,
    val downloadQueueId: Long,
    val downloadRequestType: DownloadRequestType,
    val requesterId: Long,
    val imageUrl: String?
)