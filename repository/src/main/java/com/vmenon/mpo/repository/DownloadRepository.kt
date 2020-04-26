package com.vmenon.mpo.repository

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.entity.*
import com.vmenon.mpo.extensions.writeToFile
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity.Companion.UNSAVED_ID
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

    fun getAllQueued(): Flowable<List<DownloadModel>> =
        downloadDao.getAllWithShowAndEpisodeDetails().map { savedDownloads ->
            val downloadListItems = ArrayList<DownloadModel>()
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
                        savedDownloadWithShowAndEpisode.toModel(
                            progress = downloaded,
                            total = if (totalSize == -1) 0 else totalSize
                        )
                    )
                }
            }
            cursor.close()
            downloadListItems
        }

    fun queueDownload(episode: EpisodeModel): Single<DownloadModel> = Single.fromCallable {
        queueDownload(
            episodeId = episode.id,
            episodeName = episode.name,
            downloadUrl = episode.downloadUrl,
            showId = episode.show.id
        ).toModel(episode)
    }

    fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ): Single<DownloadModel> = createShowAndEpisodeForDownload(show, episode).map { showAndEpisode ->
        queueDownload(
            episodeId = showAndEpisode.second.id,
            episodeName = showAndEpisode.second.details.episodeName,
            downloadUrl = showAndEpisode.second.details.downloadUrl,
            showId = showAndEpisode.second.showId
        ).toModel(showAndEpisode.second.toModel(showAndEpisode.first.toModel()))
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
            episodeDao.insertOrUpdate(
                EpisodeEntity(
                    details = downloadWithShowAndEpisode.episode.copy(filename = episodeFile.path),
                    showId = downloadWithShowAndEpisode.download.showId,
                    id = downloadWithShowAndEpisode.download.episodeId
                )
            )
            downloadDao.delete(downloadWithShowAndEpisode.download.id)
        }
    }

    private fun createShowAndEpisodeForDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ) = Single.fromCallable {
        val savedShow = showDao.insertOrUpdate(show.toEntity())
        val savedEpisode = episodeDao.insertOrUpdate(episode.toEntity(savedShow.id))
        Pair(savedShow, savedEpisode)
    }

    private fun queueDownload(
        episodeId: Long,
        episodeName: String,
        downloadUrl: String,
        showId: Long
    ): DownloadEntity {
        val downloadManagerId = downloadManager.enqueue(
            Request(Uri.parse(downloadUrl))
                .setTitle(episodeName)
        )

        val download = DownloadEntity(
            showId = showId,
            episodeId = episodeId,
            details = DownloadDetailsEntity(
                downloadManagerId = downloadManagerId
            ),
            id = UNSAVED_ID
        )
        val savedDownload = downloadDao.insertOrUpdate(download)
        Log.d("MPO", "Queued download: $download, $downloadUrl")
        return savedDownload
    }
}