package com.vmenon.mpo.core.usecases

import com.vmenon.mpo.common.converters.toDownloadRequest
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.domain.ShowUpdateModel
import com.vmenon.mpo.system.domain.Logger
import java.util.*

class UpdateAllShows(
    private val myLibraryService: MyLibraryService,
    private val downloadService: DownloadsService,
    private val logger: Logger
) {
    suspend operator fun invoke() {
        val interval = 1000 * 60 * 5
        val compareTime = Date().time - interval
        myLibraryService.getShowsSubscribedAndLastUpdatedBefore(compareTime)
            ?.let { shows ->
                fetchShowUpdatesAndQueueDownloads(shows)
            }
    }

    private suspend fun fetchShowUpdatesAndQueueDownloads(shows: List<ShowModel>) {
        shows.forEach { show ->
            logger.println(
                "UpdateWorker, Got saved show:" +
                        " ${show.name} , ${show.feedUrl}, ${show.lastEpisodePublished}"
            )
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
        downloadService.queueDownload(savedEpisode.toDownloadRequest())
        myLibraryService.saveShow(
            savedEpisode.show.copy(
                lastUpdate = Date().time,
                lastEpisodePublished = savedEpisode.published
            )
        )
    }
}
