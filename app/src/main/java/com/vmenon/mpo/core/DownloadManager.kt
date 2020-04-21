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
            DownloadManager.Request(Uri.parse(episode.details.downloadUrl))
                .setTitle(episode.details.episodeName)
        )

        val download = DownloadModel(
            showId = show.id,
            episodeId = episode.id,
            details = DownloadDetailsModel(downloadManagerId = downloadManagerId)
        )
        val savedDownload = downloadRepository.save(download).blockingGet()
        Log.d("MPO", "Queued download: $download, ${episode.details.downloadUrl}")
        savedDownload
    }

    fun notifyDownloadCompleted(downloadManagerId: Long) = Completable.fromAction {
        val downloadWithShowAndEpisode = downloadRepository.getByDownloadManagerId(
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
            episodeRepository.save(
                EpisodeModel(
                    details = downloadWithShowAndEpisode.episode,
                    showId = downloadWithShowAndEpisode.download.showId,
                    id = downloadWithShowAndEpisode.download.episodeId
                )
            ).ignoreElement().blockingAwait()
            downloadRepository.delete(downloadWithShowAndEpisode.download.id).blockingAwait()
        }
    }

    private fun createShowAndEpisodeForDownload(
        showDetails: ShowDetailsModel,
        episode: EpisodeModel
    ) = Single.fromCallable<Pair<ShowModel, EpisodeModel>> {
        val savedShow = showRepository.save(
            ShowModel(
                details = showDetails.copy(
                    lastEpisodePublished = 0L,
                    lastUpdate = 0L
                )
            )
        ).blockingGet()

        val savedEpisode = episodeRepository.save(
            EpisodeModel(
                details = episode.details,
                showId = savedShow.id
            )
        ).blockingGet()
        Pair(savedShow, savedEpisode)
    }
}
