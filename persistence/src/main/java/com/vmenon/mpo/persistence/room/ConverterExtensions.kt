package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.persistence.room.entity.ShowDetailsEntity
import com.vmenon.mpo.persistence.room.entity.ShowEntity
import com.vmenon.mpo.persistence.room.entity.ShowSearchResultsEntity

fun ShowSearchResultsEntity.toModel(): ShowSearchResultModel  =
    ShowSearchResultModel(
        id = showSearchResultsId,
        name = showDetails.showName,
        genres = showDetails.genres,
        feedUrl = showDetails.feedUrl,
        author = showDetails.author,
        artworkUrl = showDetails.showArtworkUrl,
        description = showDetails.showDescription
    )

internal fun ShowEntity.toModel() = ShowModel(
    id = id,
    name = details.showName,
    artworkUrl = details.showArtworkUrl,
    genres = details.genres,
    feedUrl = details.feedUrl,
    description = details.showDescription,
    author = details.author,
    isSubscribed = details.isSubscribed,
    lastEpisodePublished = details.lastEpisodePublished,
    lastUpdate = details.lastUpdate
)

internal fun ShowModel.toEntity() = ShowEntity(
    details = ShowDetailsEntity(
        showName = name,
        showDescription = description,
        showArtworkUrl = artworkUrl,
        lastUpdate = lastUpdate,
        lastEpisodePublished = lastEpisodePublished,
        isSubscribed = isSubscribed,
        author = author,
        feedUrl = feedUrl,
        genres = genres
    ),
    id = id
)