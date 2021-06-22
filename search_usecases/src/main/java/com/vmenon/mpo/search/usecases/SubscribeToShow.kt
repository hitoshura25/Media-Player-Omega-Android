package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.common.converters.toDownloadRequest
import com.vmenon.mpo.downloads.domain.DownloadRequest
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import java.util.*

class SubscribeToShow(
    private val myLibraryService: MyLibraryService,
    private val downloadsService: DownloadsService
) {
    suspend operator fun invoke(showDetails: ShowSearchResultDetailsModel): ShowModel {
        val addedShow = addShowToLibrary(showDetails)
        val showUpdate = myLibraryService.getShowUpdate(addedShow)
        if (showUpdate != null) {
            val savedEpisode = myLibraryService.saveEpisode(showUpdate.newEpisode)
            downloadsService.queueDownload(savedEpisode.toDownloadRequest())
            myLibraryService.saveShow(
                savedEpisode.show.copy(
                    lastUpdate = Date().time,
                    lastEpisodePublished = savedEpisode.published
                )
            )
        }
        return addedShow
    }

    private suspend fun addShowToLibrary(showDetails: ShowSearchResultDetailsModel): ShowModel =
        myLibraryService.saveShow(
            ShowModel(
                name = showDetails.show.name,
                artworkUrl = showDetails.show.artworkUrl,
                description = showDetails.show.description,
                genres = showDetails.show.genres,
                feedUrl = showDetails.show.feedUrl,
                author = showDetails.show.author,
                lastEpisodePublished = 0L,
                lastUpdate = 0L,
                isSubscribed = true
            )
        )
}