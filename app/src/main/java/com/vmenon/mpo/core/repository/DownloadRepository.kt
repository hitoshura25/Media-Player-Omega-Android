package com.vmenon.mpo.core.repository

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import com.vmenon.mpo.core.persistence.DownloadDao
import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.core.persistence.ShowDao
import com.vmenon.mpo.model.*
import com.vmenon.mpo.util.writeToFile
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.Completable
import java.io.File

class DownloadRepository(
    val context: Context,
    private val downloadDao: DownloadDao,
    private val episodeDao: EpisodeDao,
    private val showDao: ShowDao
) {
    private val downloadManager: DownloadManager = context.getSystemService(
        Context.DOWNLOAD_SERVICE
    ) as DownloadManager

    fun getAllQueued(): Flowable<List<QueuedDownloadModel>> =
        downloadDao.getAllWithShowAndEpisodeDetails().map { savedDownloads ->
            val downloadListItems = ArrayList<QueuedDownloadModel>()
            val savedDownloadMap = savedDownloads.map {
                it.download.details.downloadManagerId to it
            }.toMap()
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
                savedDownloadMap[id]?.let { savedDownloadWithShowAndEpisode ->
                    downloadListItems.add(
                        QueuedDownloadModel(
                            download = savedDownloadWithShowAndEpisode.download,
                            show = savedDownloadWithShowAndEpisode.show,
                            episode = savedDownloadWithShowAndEpisode.episode,
                            progress = downloaded,
                            total = if (totalSize == -1) 0 else totalSize
                        )
                    )
                }
            }
            cursor.close()
            downloadListItems
        }

    fun queueDownload(
        showDetails: ShowDetailsModel,
        episode: EpisodeModel
    ) = createShowAndEpisodeForDownload(showDetails, episode).flatMap { showAndEpisode ->
        queueDownload(showAndEpisode.first, showAndEpisode.second)
    }

    fun queueDownload(show: ShowModel, episode: EpisodeModel) = Single.fromCallable {
        val downloadManagerId = downloadManager.enqueue(
            Request(Uri.parse(episode.details.downloadUrl))
                .setTitle(episode.details.episodeName)
        )

        val download = DownloadModel(
            showId = show.id,
            episodeId = episode.id,
            details = DownloadDetailsModel(downloadManagerId = downloadManagerId)
        )
        val savedDownload = downloadDao.insertOrUpdate(download)
        Log.d("MPO", "Queued download: $download, ${episode.details.downloadUrl}")
        savedDownload
    }

    fun notifyDownloadCompleted(downloadManagerId: Long) = Completable.fromAction {
        val downloadWithShowAndEpisode =
            downloadDao.getWithShowAndEpisodeDetailsByDownloadManagerId(
                downloadManagerId
            ).firstElement().blockingGet()

        if (downloadWithShowAndEpisode != null) {
            val filename = URLUtil.guessFileName(
                downloadWithShowAndEpisode.episode.downloadUrl,
                null,
                null
            )
            val showDir = File(context.filesDir, downloadWithShowAndEpisode.show.showName)
            showDir.mkdir()
            val episodeFile = File(showDir, filename)
            downloadManager.openDownloadedFile(downloadManagerId).writeToFile(episodeFile)
            downloadWithShowAndEpisode.episode.filename = episodeFile.path
            episodeDao.insertOrUpdate(
                EpisodeModel(
                    details = downloadWithShowAndEpisode.episode,
                    showId = downloadWithShowAndEpisode.download.showId,
                    id = downloadWithShowAndEpisode.download.episodeId
                )
            )
            downloadDao.delete(downloadWithShowAndEpisode.download.id)
        }
    }

    private fun createShowAndEpisodeForDownload(
        showDetails: ShowDetailsModel,
        episode: EpisodeModel
    ) = Single.fromCallable {
        val savedShow = showDao.insertOrUpdate(
            ShowModel(
                details = showDetails.copy(
                    lastEpisodePublished = 0L,
                    lastUpdate = 0L
                )
            )
        )

        val savedEpisode = episodeDao.insertOrUpdate(
            EpisodeModel(
                details = episode.details,
                showId = savedShow.id
            )
        )
        Pair(savedShow, savedEpisode)
    }
}