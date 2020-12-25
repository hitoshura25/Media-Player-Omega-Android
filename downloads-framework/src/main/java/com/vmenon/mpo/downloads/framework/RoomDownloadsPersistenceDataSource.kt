package com.vmenon.mpo.downloads.framework

import com.vmenon.mpo.downloads.data.DownloadsPersistenceDataSource
import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.downloads.domain.DownloadRequestType
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.entity.*

class RoomDownloadsPersistenceDataSource(private val downloadDao: DownloadDao) :
    DownloadsPersistenceDataSource {
    override suspend fun insertOrUpdate(download: DownloadModel): DownloadModel =
        downloadDao.insertOrUpdate(download.toEntity()).toModel()

    override suspend fun getAll(): List<DownloadModel> =
        downloadDao.getAll().map { download -> download.toModel() }

    override suspend fun getByQueueId(queueId: Long): DownloadModel =
        downloadDao.getByDownloadManagerId(queueId).toModel()

    override suspend fun delete(id: Long) {
        downloadDao.delete(id)
    }

    private fun DownloadModel.toEntity() = DownloadEntity(
        downloadId = id,
        downloadQueueId = downloadQueueId,
        downloadUrl = downloadUrl,
        downloadRequestType = downloadRequestType.name,
        requesterId = requesterId,
        imageUrl = imageUrl,
        name = name
    )

    private fun DownloadEntity.toModel() = DownloadModel(
        id = downloadId,
        downloadQueueId = downloadQueueId,
        downloadUrl = downloadUrl,
        downloadRequestType = try {
            DownloadRequestType.valueOf(downloadRequestType)
        } catch (e: Exception) {
            println("Error determining DownloadRequestType: $e")
            DownloadRequestType.UNKNOWN
        },
        requesterId = requesterId,
        imageUrl = imageUrl,
        name = name
    )
}