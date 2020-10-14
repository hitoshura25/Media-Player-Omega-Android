package com.vmenon.mpo.download.repository.impl

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import com.vmenon.mpo.extensions.writeToFile
import com.vmenon.mpo.model.*
import com.vmenon.mpo.downloads.persistence.DownloadPersistence
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity.Companion.UNSAVED_ID
import com.vmenon.mpo.repository.toEpisodeModel
import com.vmenon.mpo.repository.toShowModel
import com.vmenon.mpo.shows.persistence.ShowPersistence
import io.reactivex.Flowable
import io.reactivex.Completable
import java.io.File

class DownloadRepositoryImpl(
    val context: Context,
    private val downloadPersistence: DownloadPersistence,
    private val episodePersistence: EpisodePersistence,
    private val showPersistence: ShowPersistence
) : DownloadRepository {
    private val downloadManager: DownloadManager = context.getSystemService(
        Context.DOWNLOAD_SERVICE
    ) as DownloadManager

    override fun getAllQueued(): Flowable<List<QueuedDownloadModel>> =
        downloadPersistence.getAll().map { savedDownloads ->
            val downloadListItems = ArrayList<QueuedDownloadModel>()
            val savedDownloadMap = savedDownloads.map {
                it.downloadManagerId to it
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
                            download = savedDownloadWithShowAndEpisode,
                            progress = downloaded,
                            total = if (totalSize == -1) 0 else totalSize
                        )
                    )
                }
            }
            cursor.close()
            downloadListItems
        }

    override suspend fun queueDownload(episode: EpisodeModel): DownloadModel {
        val downloadManagerId = downloadManager.enqueue(
            Request(Uri.parse(episode.downloadUrl))
                .setTitle(episode.name)
        )

        val download = DownloadModel(
            episode = episode,
            downloadManagerId = downloadManagerId,
            id = UNSAVED_ID
        )

        Log.d("MPO", "Queued download: $download, ${download.episode.downloadUrl}")
        return downloadPersistence.insertOrUpdate(download)
    }

    override suspend fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ): DownloadModel = queueDownload(createShowAndEpisodeForDownload(show, episode).second)

    override fun notifyDownloadCompleted(downloadManagerId: Long) = Completable.fromAction {
        val downloadWithShowAndEpisode =
            downloadPersistence.getByDownloadManagerId(
                downloadManagerId
            ).firstElement().blockingGet()

        if (downloadWithShowAndEpisode != null) {
            val filename = URLUtil.guessFileName(
                downloadWithShowAndEpisode.episode.downloadUrl,
                null,
                null
            )
            val showDir = File(context.filesDir, downloadWithShowAndEpisode.episode.show.name)
            showDir.mkdir()
            val episodeFile = File(showDir, filename)
            downloadManager.openDownloadedFile(downloadManagerId).writeToFile(episodeFile)
            episodePersistence.insertOrUpdate(
                downloadWithShowAndEpisode.episode.copy(filename = episodeFile.path)
            )
            downloadPersistence.delete(downloadWithShowAndEpisode.id)
        }
    }

    private suspend fun createShowAndEpisodeForDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ): Pair<ShowModel, EpisodeModel> {
        val savedShow =
            showPersistence.getByName(show.name) ?: showPersistence.insertOrUpdate(
                show.toShowModel()
            )
        val savedEpisode = episodePersistence.getByName(episode.name).blockingGet()
            ?: episodePersistence.insertOrUpdate(episode.toEpisodeModel(savedShow))
        return Pair(savedShow, savedEpisode)
    }
}