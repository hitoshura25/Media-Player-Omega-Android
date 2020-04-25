package com.vmenon.mpo.core

import android.util.Log
import com.vmenom.mpo.model.ShowModel
import com.vmenom.mpo.model.ShowUpdateModel
import com.vmenon.mpo.repository.DownloadRepository
import com.vmenon.mpo.repository.EpisodeRepository
import com.vmenon.mpo.repository.ShowRepository
import io.reactivex.Completable
import java.util.*

class ShowUpdateManager(
    private val showRepository: ShowRepository,
    private val episodeRepository: EpisodeRepository,
    private val downloadRepository: DownloadRepository
) {
    fun updateAllShows(): Completable =
        showRepository.getSubscribedAndLastUpdatedBefore((1000 * 60 * 5).toLong())
            .flatMapCompletable(this::fetchShowUpdatesAndQueueDownloads)

    fun updateShow(show: ShowModel): Completable = fetchShowUpdate(show)

    private fun fetchShowUpdatesAndQueueDownloads(shows: List<ShowModel>) = Completable.fromAction {
        shows.forEach { show ->
            Log.d(
                "UpdateWorker",
                "Got saved show: ${show.name} , ${show.feedUrl}, ${show.lastEpisodePublished}"
            )
            fetchShowUpdate(show).blockingAwait()
        }
    }

    private fun fetchShowUpdate(show: ShowModel): Completable =
        showRepository.getShowUpdate(show)
            .flatMapCompletable { showUpdate ->
                saveEpisodeAndQueueDownload(showUpdate)
            }

    private fun saveEpisodeAndQueueDownload(showUpdate: ShowUpdateModel): Completable =
        episodeRepository.save(showUpdate.newEpisode)
            .flatMap { savedEpisode ->
                downloadRepository.queueDownload(savedEpisode)
            }.flatMapCompletable { savedDownload ->
                showRepository.save(savedDownload.episode.show.copy(
                    lastUpdate = Date().time,
                    lastEpisodePublished = savedDownload.episode.published
                )).ignoreElement()
            }
}