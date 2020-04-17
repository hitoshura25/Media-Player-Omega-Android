package com.vmenon.mpo.core.repository

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import com.vmenon.mpo.core.persistence.DownloadDao
import com.vmenon.mpo.model.QueuedDownloadModel
import com.vmenon.mpo.model.DownloadModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.Completable

class DownloadRepository(
    val context: Context,
    private val downloadDao: DownloadDao
) {
    private val downloadManager: DownloadManager = context.getSystemService(
        Context.DOWNLOAD_SERVICE
    ) as DownloadManager

    fun getAllDownloads(): Flowable<List<QueuedDownloadModel>> =
        downloadDao.loadDownloadsWithShowAndEpisode().map { savedDownloads ->
            val downloadListItems = ArrayList<QueuedDownloadModel>()
            val savedDownloadMap = savedDownloads.map { it.download.downloadManagerId to it}.toMap()
            val downloadManagerIds = savedDownloadMap.keys

            val cursor = downloadManager.query(
                Query().setFilterById(*downloadManagerIds.toLongArray())
            )
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val totalSize = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_SIZE_BYTES))
                val downloaded = cursor.getInt(
                    cursor.getColumnIndex(COLUMN_BYTES_DOWNLOADED_SO_FAR)
                )
                savedDownloadMap[id]?.let { savedDownload ->
                    downloadListItems.add(
                        QueuedDownloadModel(
                            download = savedDownload.download,
                            show = savedDownload.show,
                            episode = savedDownload.episode,
                            progress = downloaded,
                            total = if (totalSize == -1) 0 else totalSize
                        )
                    )
                }
            }
            cursor.close()
            downloadListItems
        }

    fun save(downloadModel: DownloadModel): Single<DownloadModel> = Single.create { emitter ->
        emitter.onSuccess(
            if (downloadModel.downloadId == 0L) {
                downloadModel.copy(downloadId = downloadDao.insert(downloadModel))
            } else {
                downloadDao.update(downloadModel)
                downloadModel
            }
        )
    }

    fun deleteDownload(downloadId: Long): Completable = Completable.create { emitter ->
        downloadDao.delete(downloadId)
        emitter.onComplete()
    }

    fun byDownloadManagerId(downloadManagerId: Long) =
        downloadDao.byDownloadManagerId(downloadManagerId)
}