package com.vmenon.mpo.search.repository

import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.model.ShowSearchResultModel

internal fun Show.toSearchResultsModel() = ShowSearchResultModel(
    id = 0L,
    author = author,
    artworkUrl = artworkUrl,
    feedUrl = feedUrl ?: "",
    genres = genres,
    name = name,
    description = ""
)

internal fun Episode.toModel() = ShowSearchResultEpisodeModel(
    name = name,
    artworkUrl = artworkUrl,
    description = description,
    downloadUrl = downloadUrl,
    length = length,
    published = published,
    type = type
)