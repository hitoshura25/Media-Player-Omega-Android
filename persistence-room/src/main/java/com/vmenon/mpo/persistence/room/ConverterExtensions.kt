package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.persistence.room.entity.*

internal fun ShowDetailsEntity.toModel(showId: Long) = ShowModel(
    id = showId,
    name = showName,
    artworkUrl = showArtworkUrl,
    lastUpdate = lastUpdate,
    lastEpisodePublished = lastEpisodePublished,
    isSubscribed = isSubscribed,
    author = author,
    feedUrl = feedUrl,
    genres = genres,
    description = showDescription
)

internal fun EpisodeWithShowDetailsEntity.toModel() = EpisodeModel(
    id = episode.episodeId,
    name = episode.details.episodeName,
    filename = episode.details.filename,
    description = episode.details.description,
    downloadUrl = episode.details.downloadUrl,
    length = episode.details.length,
    published = episode.details.published,
    type = episode.details.type,
    artworkUrl = episode.details.episodeArtworkUrl,
    show = showDetails.toModel(episode.showId)
)

internal fun EpisodeModel.toEntity() = EpisodeEntity(
    episodeId = id,
    showId = show.id,
    details = EpisodeDetailsEntity(
        episodeName = name,
        episodeArtworkUrl = artworkUrl,
        type = type,
        published = published,
        length = length,
        downloadUrl = downloadUrl,
        description = description,
        filename = filename
    )
)

internal fun EpisodeEntity.toModel(show: ShowModel) = EpisodeModel(
    id = episodeId,
    downloadUrl = details.downloadUrl,
    type = details.type,
    published = details.published,
    length = details.length,
    description = details.description,
    artworkUrl = details.episodeArtworkUrl,
    name = details.episodeName,
    filename = details.filename,
    show = show
)
