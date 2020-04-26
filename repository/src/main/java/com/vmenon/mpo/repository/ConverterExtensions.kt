package com.vmenon.mpo.repository

import com.vmenon.mpo.model.*
import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity.Companion.UNSAVED_ID
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

internal fun EpisodeDetailsEntity.toModel(episodeId: Long, show: ShowModel) = EpisodeModel(
    id = episodeId,
    description = description,
    artworkUrl = episodeArtworkUrl,
    name = episodeName,
    type = type,
    published = published,
    length = length,
    downloadUrl = downloadUrl,
    filename = filename,
    show = show
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

internal fun EpisodeModel.toEntity() = EpisodeEntity(
    id = id,
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
    id = id,
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

internal fun EpisodeWithShowDetailsEntity.toModel() = EpisodeModel(
    id = episode.id,
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

internal fun DownloadWithShowAndEpisodeDetailsEntity.toModel(
    progress: Int,
    total: Int
) = DownloadModel(
    id = download.id,
    episode = episode.toModel(
        episodeId = download.episodeId,
        show = show.toModel(download.showId)
    ),
    downloadManagerId = download.details.downloadManagerId,
    total = total,
    progress = progress
)

internal fun DownloadEntity.toModel(episode: EpisodeModel) = DownloadModel(
    id = id,
    progress = 0,
    total = 0,
    downloadManagerId = details.downloadManagerId,
    episode = episode
)

internal fun ShowSearchResultModel.toEntity() = ShowEntity(
    details = ShowDetailsEntity(
        showName = name,
        author = author,
        feedUrl = feedUrl,
        genres = genres,
        showArtworkUrl = artWorkUrl,
        showDescription = description,
        lastEpisodePublished = 0L,
        lastUpdate = 0L,
        isSubscribed = false
    ),
    id = id
)

internal fun ShowSearchResultEpisodeModel.toEntity(showId: Long) = EpisodeEntity(
    showId = showId,
    details = EpisodeDetailsEntity(
        episodeName = name,
        episodeArtworkUrl = artworkUrl,
        description = description,
        downloadUrl = downloadUrl,
        length = length,
        published = published,
        type = type,
        filename = null
    ),
    id = UNSAVED_ID
)