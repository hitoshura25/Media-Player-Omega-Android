package com.vmenon.mpo.downloads.persistence

import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.persistence.BasePersistence

interface DownloadPersistence : BasePersistence<DownloadModel> {
    suspend fun getByDownloadManagerId(id: Long): DownloadModel
    suspend fun getAll(): List<DownloadModel>
    suspend fun delete(id: Long)
}