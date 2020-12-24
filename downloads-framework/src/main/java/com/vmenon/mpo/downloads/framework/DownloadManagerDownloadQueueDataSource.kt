package com.vmenon.mpo.downloads.framework

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.vmenon.mpo.downloads.data.DownloadQueueItem
import com.vmenon.mpo.downloads.data.DownloadsQueueDataSource
import com.vmenon.mpo.my_library.domain.EpisodeModel

class DownloadManagerDownloadQueueDataSource(
    context: Context
) : DownloadsQueueDataSource {
    private val downloadManager: DownloadManager = context.getSystemService(
        Context.DOWNLOAD_SERVICE
    ) as DownloadManager

    override suspend fun getAllQueued(queueIds: Collection<Long>): List<DownloadQueueItem> {
        val downloadQueueItems = ArrayList<DownloadQueueItem>()
        val cursor = downloadManager.query(
            DownloadManager.Query().setFilterById(*queueIds.toLongArray())
        )
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
            val totalSize =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val downloaded = cursor.getInt(
                cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            )
            downloadQueueItems.add(
                DownloadQueueItem(
                    queueId = id,
                    totalSize = totalSize,
                    downloaded = downloaded
                )
            )
        }
        cursor.close()
        return downloadQueueItems
    }

    override suspend fun queueDownloadAndGetQueueId(episode: EpisodeModel): Long =
        downloadManager.enqueue(
            DownloadManager.Request(Uri.parse(episode.downloadUrl))
                .setTitle(episode.name)
        )
}