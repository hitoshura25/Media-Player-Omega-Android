package com.vmenon.mpo.core

import android.text.TextUtils
import android.util.Log
import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.core.repository.EpisodeRepository
import com.vmenon.mpo.core.repository.ShowRepository
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.service.MediaPlayerOmegaService
import io.reactivex.Completable
import java.util.*

class ShowUpdateManager(
    private val service: MediaPlayerOmegaService,
    private val showRepository: ShowRepository,
    private val episodeRepository: EpisodeRepository,
    private val downloadManager: DownloadManager
) {
    fun updateAllShows(): Completable =
        showRepository.notUpdatedInLast((1000 * 60 * 5).toLong())
            .flatMapCompletable(this::fetchShowUpdatesAndQueueDownloads)

    fun updateShow(show: ShowModel): Completable = fetchShowUpdate(show, show.lastEpisodePublished)

    private fun fetchShowUpdatesAndQueueDownloads(shows: List<ShowModel>) = Completable.fromAction {
        shows.forEach { show ->
            Log.d(
                "UpdateWorker",
                "Got saved show: ${show.showDetails.showName} , ${show.showDetails.feedUrl}, ${show.lastEpisodePublished}"
            )
            fetchShowUpdate(show, show.lastEpisodePublished).blockingAwait()
        }
    }

    private fun fetchShowUpdate(show: ShowModel, lastEpisodePublished: Long): Completable =
        service.getPodcastUpdate(
            show.showDetails.feedUrl,
            lastEpisodePublished
        ).flatMapCompletable { episode ->
            saveEpisodeAndQueueDownload(show, episode)
        }

    private fun saveEpisodeAndQueueDownload(show: ShowModel, episode: Episode): Completable =
        Completable.fromAction {
            if (TextUtils.isEmpty(episode.artworkUrl)) {
                episode.artworkUrl = show.showDetails.showArtworkUrl
            }
        }.andThen(episodeRepository.save(
            EpisodeModel(
                episodeName = episode.name,
                episodeArtworkUrl = episode.artworkUrl,
                description = episode.description,
                downloadUrl = episode.downloadUrl,
                filename = "",
                length = episode.length,
                published = episode.published,
                episodeShowId = show.showId,
                type = episode.type
            )
        ).flatMap { savedEpisode ->
            downloadManager.queueDownload(show, savedEpisode)
        }).flatMapCompletable {
            show.lastUpdate = Date().time
            show.lastEpisodePublished = episode.published
            showRepository.save(show).ignoreElement()
        }
}