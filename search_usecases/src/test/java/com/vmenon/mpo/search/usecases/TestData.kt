package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.downloads.domain.DownloadRequestType
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel

object TestData {
    val showSearchResultModel = ShowSearchResultModel(
        name = "show",
        artworkUrl = "artwork.com",
        genres = emptyList(),
        author = "author",
        feedUrl = "feedUrl",
        description = "description",
        id = 1L
    )
    val showSearchResultEpisodeModel = ShowSearchResultEpisodeModel(
        name = "episode",
        artworkUrl = "artwork.com",
        description = "description",
        downloadUrl = "downloadUrl",
        length = 100L,
        published = 1L,
        type = "episode"
    )
    val showSearchResultDetailsModel = ShowSearchResultDetailsModel(
        show = showSearchResultModel,
        episodes = listOf(showSearchResultEpisodeModel),
        subscribed = true
    )
    val show = ShowModel(
        name = "show",
        artworkUrl = "artwork.com",
        genres = emptyList(),
        author = "author",
        feedUrl = "feedUrl",
        description = "description",
        lastUpdate = 0L,
        lastEpisodePublished = 0L
    )
    val episode = EpisodeModel(
        name = "episode",
        description = "description",
        published = 0L,
        type = "show",
        downloadUrl = "www.download.com",
        lengthInSeconds = 100,
        artworkUrl = "artworkurl",
        filename = "filename",
        show = show
    )
    val download = DownloadModel(
        name = "download",
        downloadUrl = "www.download.com",
        downloadQueueId = 1L,
        downloadRequestType = DownloadRequestType.EPISODE,
        requesterId = 1L,
        downloadAttempt = 0,
        imageUrl = null
    )
}