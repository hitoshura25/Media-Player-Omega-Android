package com.vmenon.mpo.repository

import com.vmenon.mpo.model.*
import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity.Companion.UNSAVED_ID

internal fun Show.toSearchResultsModel() = ShowSearchResultModel(
    id = UNSAVED_ID,
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

internal fun Episode.toModel(show: ShowModel) = EpisodeModel(
    name = name,
    description = description,
    artworkUrl = artworkUrl,
    downloadUrl = downloadUrl,
    length = length,
    published = published,
    type = type,
    show = show,
    id = 0L
)

internal fun ShowSearchResultModel.toShowModel() = ShowModel(
    name = name,
    author = author,
    feedUrl = feedUrl,
    genres = genres,
    artworkUrl = artworkUrl,
    lastEpisodePublished = 0L,
    lastUpdate = 0L,
    isSubscribed = false,
    description = description,
    id = id
)

internal fun ShowSearchResultEpisodeModel.toEpisodeModel(show: ShowModel) = EpisodeModel(
    show = show,
    description = description,
    artworkUrl = artworkUrl,
    name = name,
    downloadUrl = downloadUrl,
    length = length,
    published = published,
    type = type,
    filename = null,
    id = UNSAVED_ID
)