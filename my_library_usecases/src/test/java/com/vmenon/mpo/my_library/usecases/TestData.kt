package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.ShowModel

object TestData {
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
}