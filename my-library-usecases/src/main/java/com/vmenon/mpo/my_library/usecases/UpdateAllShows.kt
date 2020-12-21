package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.domain.ShowUpdateModel
import java.util.*

class UpdateAllShows(
    private val myLibraryService: MyLibraryService,
    private val downloadService: DownloadsService
) {
    suspend operator fun invoke() {
        myLibraryService.getShowsSubscribedAndLastUpdatedBefore((1000 * 60 * 5).toLong())
            ?.let { shows ->
                fetchShowUpdatesAndQueueDownloads(shows)
            }
    }

    private suspend fun fetchShowUpdatesAndQueueDownloads(shows: List<ShowModel>) {
        shows.forEach { show ->
            // TODO: Abstract logging?
            /*Log.d(
                "UpdateWorker",
                "Got saved show: ${show.name} , ${show.feedUrl}, ${show.lastEpisodePublished}"
            )*/
            fetchShowUpdate(show)
        }
    }

    private suspend fun fetchShowUpdate(show: ShowModel) {
        myLibraryService.getShowUpdate(show)?.let { showUpdate ->
            saveEpisodeAndQueueDownload(showUpdate)
        }
    }

    private suspend fun saveEpisodeAndQueueDownload(showUpdate: ShowUpdateModel) {
        val savedEpisode = myLibraryService.saveEpisode(showUpdate.newEpisode)
        val savedDownload = downloadService.queueDownload(savedEpisode)
        myLibraryService.saveShow(
            savedDownload.episode.show.copy(
                lastUpdate = Date().time,
                lastEpisodePublished = savedDownload.episode.published
            )
        )
    }
}
