package com.vmenon.mpo.core

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import com.vmenon.mpo.core.repository.DownloadRepository
import com.vmenon.mpo.core.repository.EpisodeRepository

import com.vmenon.mpo.core.repository.ShowRepository
import com.vmenon.mpo.model.*
import com.vmenon.mpo.util.writeToFile
import io.reactivex.Completable
import io.reactivex.Single

import java.io.File

class DownloadManager(
    private val context: Context,
    private val downloadRepository: DownloadRepository,
    private val episodeRepository: EpisodeRepository,
    private val showRepository: ShowRepository
) {
    private val downloadManager: DownloadManager = context.getSystemService(
        Context.DOWNLOAD_SERVICE
    ) as DownloadManager

    fun queueDownload(
        showDetails: ShowDetailsModel,
        episode: EpisodeModel
    ) = createShowAndEpisodeForDownload(showDetails, episode).flatMap { showAndEpisode ->
        queueDownload(showAndEpisode.first, showAndEpisode.second)
    }

    fun queueDownload(show: ShowModel, episode: EpisodeModel) = Single.fromCallable {
        val downloadManagerId = downloadManager.enqueue(
            DownloadManager.Request(Uri.parse(episode.downloadUrl))
                .setTitle(episode.episodeName)
        )

        val download = DownloadModel(
            downloadShowId = show.showId,
            downloadEpisodeId = episode.episodeId,
            downloadManagerId = downloadManagerId
        )
        val savedDownload = downloadRepository.save(download).blockingGet()
        Log.d("MPO", "Queued download: $download, ${episode.downloadUrl}")
        savedDownload
    }

    fun notifyDownloadCompleted(downloadManagerId: Long) = Completable.fromAction {
        val download = downloadRepository.byDownloadManagerId(
            downloadManagerId
        ).firstElement().blockingGet()

        if (download != null) {
            val filename = URLUtil.guessFileName(download.episode.downloadUrl, null, null)
            val showDir = File(context.filesDir, download.show.showDetails.showName)
            showDir.mkdir()
            val episodeFile = File(showDir, filename)
            downloadManager.openDownloadedFile(downloadManagerId).writeToFile(episodeFile)
            download.episode.filename = episodeFile.path
            episodeRepository.save(download.episode).ignoreElement().blockingAwait()
            downloadRepository.deleteDownload(download.download.downloadId).blockingAwait()
        }
    }

    private fun createShowAndEpisodeForDownload(
        showDetails: ShowDetailsModel,
        episode: EpisodeModel
    ) = Single.fromCallable<Pair<ShowModel, EpisodeModel>> {
        val savedShow = showRepository.save(
            ShowModel(
                showDetails = showDetails,
                lastEpisodePublished = 0L,
                lastUpdate = 0L
            )
        ).blockingGet()

        val savedEpisode = episodeRepository.save(
            EpisodeModel(
                episodeName = episode.episodeName,
                episodeArtworkUrl = episode.episodeArtworkUrl,
                description = episode.description,
                downloadUrl = episode.downloadUrl,
                filename = "",
                length = episode.length,
                published = episode.published,
                episodeShowId = savedShow.showId,
                type = episode.type
            )
        ).blockingGet()
        Pair(savedShow, savedEpisode)
    }
}
