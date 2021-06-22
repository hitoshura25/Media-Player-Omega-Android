package com.vmenon.mpo.downloads.domain

data class DownloadRequest(
    val downloadUrl: String,
    val name: String,
    val imageUrl: String?,
    val downloadRequestType: DownloadRequestType,
    val requesterId: Long
)