package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel

class QueueDownloadForShow(
    private val myLibraryService: MyLibraryService,
    private val downloadsService: DownloadsService
) {
    suspend operator fun invoke(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ): DownloadModel {
        val savedShow = myLibraryService.getShowByName(show.name) ?: myLibraryService.saveShow(
            ShowModel(
                name = show.name,
                author = show.author,
                feedUrl = show.feedUrl,
                genres = show.genres,
                artworkUrl = show.artworkUrl,
                lastEpisodePublished = 0L,
                lastUpdate = 0L,
                isSubscribed = false,
                description = show.description
            )

        )
        val savedEpisode = myLibraryService.getEpisodeByName(episode.name)
            ?: myLibraryService.saveEpisode(
                EpisodeModel(
                    show = savedShow,
                    description = episode.description,
                    artworkUrl = episode.artworkUrl,
                    name = episode.name,
                    downloadUrl = episode.downloadUrl,
                    lengthInSeconds = episode.length,
                    published = episode.published,
                    type = episode.type,
                    filename = null
                )
            )
        return downloadsService.queueDownload(savedEpisode)
    }
}