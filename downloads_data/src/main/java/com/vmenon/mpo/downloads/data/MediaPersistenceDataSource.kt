package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.downloads.domain.DownloadModel

interface MediaPersistenceDataSource {
    suspend fun storeMediaAndGetPath(download: DownloadModel): String
}