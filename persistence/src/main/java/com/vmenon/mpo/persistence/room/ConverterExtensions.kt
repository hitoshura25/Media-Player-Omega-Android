package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity
import com.vmenon.mpo.persistence.room.entity.*

fun ShowSearchResultsEntity.toModel(): ShowSearchResultModel =
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

internal fun DownloadWithShowAndEpisodeDetailsEntity.toModel() = DownloadModel(
    id = download.id,
    episode = episode.toModel(
        episodeId = download.episodeId,
        show = show.toModel(download.showId)
    ),
    downloadManagerId = download.details.downloadManagerId
)

internal fun DownloadModel.toEntity() = DownloadEntity(
    showId = episode.show.id,
    episodeId = episode.id,
    details = DownloadDetailsEntity(
        downloadManagerId = downloadManagerId
    ),
    id = id
)

internal fun DownloadEntity.toModel(episode: EpisodeModel) = DownloadModel(
    id = id,
    downloadManagerId = details.downloadManagerId,
    episode = episode
)