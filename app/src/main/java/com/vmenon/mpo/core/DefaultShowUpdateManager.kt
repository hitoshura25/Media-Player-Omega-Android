package com.vmenon.mpo.core

import android.util.Log
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel
import com.vmenon.mpo.shows.ShowUpdateManager
import com.vmenon.mpo.shows.repository.EpisodeRepository
import com.vmenon.mpo.shows.repository.ShowRepository
import java.util.*

class DefaultShowUpdateManager(
    private val showRepository: ShowRepository,
    private val episodeRepository: EpisodeRepository,
    private val downloadRepository: DownloadRepository
) : ShowUpdateManager {
    override suspend fun updateAllShows() {
        showRepository.getSubscribedAndLastUpdatedBefore((1000 * 60 * 5).toLong())?.let { shows ->
            fetchShowUpdatesAndQueueDownloads(shows)
        }
    }

    override suspend fun updateShow(show: ShowModel) {
        fetchShowUpdate(show)
    }

    private suspend fun fetchShowUpdatesAndQueueDownloads(shows: List<ShowModel>) {
        shows.forEach { show ->
            Log.d(
                "UpdateWorker",
                "Got saved show: ${show.name} , ${show.feedUrl}, ${show.lastEpisodePublished}"
            )
            fetchShowUpdate(show)
        }
    }

    private suspend fun fetchShowUpdate(show: ShowModel) {
        showRepository.getShowUpdate(show)?.let { showUpdate ->
            saveEpisodeAndQueueDownload(showUpdate)
        }
    }

    private suspend fun saveEpisodeAndQueueDownload(showUpdate: ShowUpdateModel) {
        val savedDownload = episodeRepository.save(showUpdate.newEpisode)
            .flatMap { savedEpisode ->
                downloadRepository.queueDownload(savedEpisode)
            }.blockingGet()

        showRepository.save(
            savedDownload.episode.show.copy(
                lastUpdate = Date().time,
                lastEpisodePublished = savedDownload.episode.published
            )
        )
    }
}