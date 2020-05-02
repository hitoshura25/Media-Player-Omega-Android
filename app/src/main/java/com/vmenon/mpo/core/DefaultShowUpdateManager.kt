package com.vmenon.mpo.core

import android.util.Log
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel
import com.vmenon.mpo.shows.ShowUpdateManager
import com.vmenon.mpo.shows.repository.EpisodeRepository
import com.vmenon.mpo.shows.repository.ShowRepository
import io.reactivex.Completable
import java.util.*

class DefaultShowUpdateManager(
    private val showRepository: ShowRepository,
    private val episodeRepository: EpisodeRepository,
    private val downloadRepository: DownloadRepository
) : ShowUpdateManager {
    override fun updateAllShows(): Completable =
        showRepository.getSubscribedAndLastUpdatedBefore((1000 * 60 * 5).toLong())
            .flatMapCompletable(this::fetchShowUpdatesAndQueueDownloads)

    override fun updateShow(show: ShowModel): Completable = fetchShowUpdate(show)

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