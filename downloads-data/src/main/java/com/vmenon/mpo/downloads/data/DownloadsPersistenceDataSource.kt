package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.downloads.domain.DownloadModel

interface DownloadsPersistenceDataSource {
    suspend fun insertOrUpdate(download: DownloadModel): DownloadModel
    suspend fun getAll():List<DownloadModel>
}