package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.downloads.domain.*

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
            imageUrl = downloadRequest.imageUrl
        )
        return persistenceDataSource.insertOrUpdate(download)
    }

    override suspend fun getAllQueued(): List<QueuedDownloadModel> {
        val savedDownloads = persistenceDataSource.getAll()
        val downloadListItems = ArrayList<QueuedDownloadModel>()
        val savedDownloadMap = savedDownloads.map {
            it.downloadQueueId to it
        }.toMap()
        val downloadManagerIds = savedDownloadMap.keys
        val downloadQueueItems = queueDataSource.getAllQueued(downloadManagerIds)

        downloadQueueItems.forEach { queueItem ->
            savedDownloadMap[queueItem.queueId]?.let { savedDownloadWithShowAndEpisode ->
                downloadListItems.add(
                    QueuedDownloadModel(
                        download = savedDownloadWithShowAndEpisode,
                        progress = queueItem.downloaded,
                        total = if (queueItem.totalSize == -1) 0 else queueItem.totalSize
                    )
                )
            }
        }

        return downloadListItems
    }

    override suspend fun getCompletedDownloadByQueueId(queueId: Long): CompletedDownloadModel {
        val download = persistenceDataSource.getByQueueId(queueId)
        val mediaPath = mediaPersistenceDataSource.storeMediaAndGetPath(download)
        return CompletedDownloadModel(download, mediaPath)
    }

    override suspend fun delete(id: Long) {
        persistenceDataSource.delete(id)
    }
}