package com.vmenon.mpo.test

import com.vmenon.mpo.downloads.domain.*
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.domain.ShowUpdateModel
import com.vmenon.mpo.player.domain.PlaybackMedia
import com.vmenon.mpo.player.domain.PlaybackMediaRequest
import com.vmenon.mpo.player.domain.PlaybackState
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel

object TestData {
    const val SHOW_RESULT_ID = 1L
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
    val showUpdate = ShowUpdateModel(episode)
    val showSearchResultModel = ShowSearchResultModel(
        name = "show",
        artworkUrl = "artwork.com",
        genres = emptyList(),
        author = "author",
        feedUrl = "feedUrl",
        description = "description",
        id = SHOW_RESULT_ID
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
    val download = DownloadModel(
        name = "download",
        downloadUrl = "www.download.com",
        downloadQueueId = 1L,
        downloadRequestType = DownloadRequestType.EPISODE,
        requesterId = 1L,
        downloadAttempt = 0,
        imageUrl = null
    )
    val queuedDownload = QueuedDownloadModel(
        download = download,
        total = 100,
        progress = 0,
        status = QueuedDownloadStatus.NOT_QUEUED
    )
    val completedDownload = CompletedDownloadModel(
        download = download,
        pathToFile = "/path/file.mp4"
    )
    val playbackMedia = PlaybackMedia(
        mediaId = "mediaId",
        durationInMillis = 120000L
    )
    val playbackMediaRequest = PlaybackMediaRequest(
        media = playbackMedia,
        mediaFile = "file"
    )
    val playbackState = PlaybackState(
        media = playbackMedia,
        positionInMillis = 0L,
        state = PlaybackState.State.NONE,
        playbackSpeed = 1F
    )
}