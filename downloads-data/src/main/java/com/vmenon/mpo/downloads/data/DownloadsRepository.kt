package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.downloads.domain.CompletedDownloadModel
import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.downloads.domain.DownloadRequest
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.domain.QueuedDownloadModel
import com.vmenon.mpo.downloads.domain.QueuedDownloadStatus

class DownloadsRepository(
    private val queueDataSource: DownloadsQueueDataSource,
    private val persistenceDataSource: DownloadsPersistenceDataSource,
    private val mediaPersistenceDataSource: MediaPersistenceDataSource
) : DownloadsService {
    override suspend fun queueDownload(downloadRequest: DownloadRequest): DownloadModel {
        val queueId = queueDataSource.queueDownloadAndGetQueueId(downloadRequest)
        val download = DownloadModel(
            name = downloadRequest.name,
            downloadUrl = downloadRequest.downloadUrl,
            downloadQueueId = queueId,
            requesterId = downloadRequest.requesterId,
            downloadRequestType = downloadRequest.downloadRequestType,
            imageUrl = downloadRequest.imageUrl,
            downloadAttempt = 0
        )
        return persistenceDataSource.insertOrUpdate(download)
    }

    override suspend fun getAllQueued(): List<QueuedDownloadModel> {
        val downloadMap = persistenceDataSource.getAll().map { download ->
            download.downloadQueueId to download
        }.toMap()

        val queueDownloadsMap =
            queueDataSource.getAllQueued(downloadMap.keys).map { queuedDownload ->
                queuedDownload.queueId to queuedDownload
            }.toMap()


        val queuedDownloadModels = ArrayList<QueuedDownloadModel>()
        downloadMap.values.forEach { download ->
            val queuedDownload = queueDownloadsMap[download.downloadQueueId]
            queuedDownloadModels.add(
                QueuedDownloadModel(
                    download = download,
                    progress = queuedDownload?.downloaded ?: 0,
                    total = if (queuedDownload?.totalSize == -1) 0 else queuedDownload?.totalSize
                        ?: 0,
                    status = queuedDownload?.status ?: QueuedDownloadStatus.NOT_QUEUED
                )
            )
        }

        return queuedDownloadModels
    }

    override suspend fun getCompletedDownloadByQueueId(queueId: Long): CompletedDownloadModel {
        val download = persistenceDataSource.getByQueueId(queueId)
        val mediaPath = mediaPersistenceDataSource.storeMediaAndGetPath(download)
        return CompletedDownloadModel(download, mediaPath)
    }

    override suspend fun delete(id: Long) {
        persistenceDataSource.delete(id)
    }

    override suspend fun retryDownload(download: DownloadModel): DownloadModel {
        val queueId = queueDataSource.queueDownloadAndGetQueueId(
            DownloadRequest(
                downloadUrl = download.downloadUrl,
                downloadRequestType = download.downloadRequestType,
                imageUrl = download.imageUrl,
                name = download.name,
                requesterId = download.requesterId
            )
        )
        return persistenceDataSource.insertOrUpdate(download.copy(
            downloadQueueId = queueId,
            downloadAttempt = download.downloadAttempt + 1
        ))
    }
}