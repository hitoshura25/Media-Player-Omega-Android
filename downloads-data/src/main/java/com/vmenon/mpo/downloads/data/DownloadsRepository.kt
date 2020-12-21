package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.domain.QueuedDownloadModel
import com.vmenon.mpo.my_library.domain.EpisodeModel

class DownloadsRepository(
    private val queueDataSource: DownloadsQueueDataSource,
    private val persistenceDataSource: DownloadsPersistenceDataSource
): DownloadsService {
    override suspend fun queueDownload(episode: EpisodeModel): DownloadModel {
        val queueId = queueDataSource.queueDownloadAndGetQueueId(episode)
        val download = DownloadModel(
            episode = episode,
            downloadManagerId = queueId
        )
        return persistenceDataSource.insertOrUpdate(download)
    }

    override suspend fun getAllQueued(): List<QueuedDownloadModel> {
        val savedDownloads = persistenceDataSource.getAll()
        val downloadListItems = ArrayList<QueuedDownloadModel>()
        val savedDownloadMap = savedDownloads.map {
            it.downloadManagerId to it
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
}