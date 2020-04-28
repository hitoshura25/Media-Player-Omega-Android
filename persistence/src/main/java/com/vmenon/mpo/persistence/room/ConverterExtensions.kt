package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.ShowSearchResultModel
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