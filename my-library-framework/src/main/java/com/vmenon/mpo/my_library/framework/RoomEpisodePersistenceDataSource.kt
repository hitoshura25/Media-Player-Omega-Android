package com.vmenon.mpo.my_library.framework

import com.vmenon.mpo.my_library.data.EpisodePersistenceDataSource
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.entity.EpisodeDetailsEntity
import com.vmenon.mpo.persistence.room.entity.EpisodeEntity
import com.vmenon.mpo.persistence.room.entity.EpisodeWithShowDetailsEntity
import com.vmenon.mpo.persistence.room.entity.ShowDetailsEntity

class RoomEpisodePersistenceDataSource(private val episodeDao: EpisodeDao) :
    EpisodePersistenceDataSource {
    override suspend fun insertOrUpdate(episode: EpisodeModel): EpisodeModel =
        episodeDao.insertOrUpdate(episode.toEntity()).toModel(episode.show)

    override suspend fun getByName(name: String): EpisodeModel? =
        episodeDao.getByNameWithShowDetails(name)?.toModel()

    override suspend fun getAll(): List<EpisodeModel> =
        episodeDao.getAllWithShowDetails().map { episode ->
            episode.toModel()
        }

    override suspend fun getEpisode(episodeId: Long): EpisodeModel =
        episodeDao.getWithShowDetailsById(episodeId).toModel()

    private fun EpisodeModel.toEntity() = EpisodeEntity(
        episodeId = id,
        showId = show.id,
        details = EpisodeDetailsEntity(
            episodeName = name,
            episodeArtworkUrl = artworkUrl,
            type = type,
            published = published,
            length = lengthInSeconds,
            downloadUrl = downloadUrl,
            description = description,
            filename = filename
        )
    )

    private fun EpisodeEntity.toModel(show: ShowModel) = EpisodeModel(
        id = episodeId,
        downloadUrl = details.downloadUrl,
        type = details.type,
        published = details.published,
        lengthInSeconds = details.length,
        description = details.description,
        artworkUrl = details.episodeArtworkUrl,
        name = details.episodeName,
        filename = details.filename,
        show = show
    )

    private fun EpisodeWithShowDetailsEntity.toModel() = EpisodeModel(
        id = episode.episodeId,
        name = episode.details.episodeName,
        filename = episode.details.filename,
        description = episode.details.description,
        downloadUrl = episode.details.downloadUrl,
        lengthInSeconds = episode.details.length,
        published = episode.details.published,
        type = episode.details.type,
        artworkUrl = episode.details.episodeArtworkUrl,
        show = showDetails.toModel(episode.showId)
    )

    private fun ShowDetailsEntity.toModel(showId: Long) = ShowModel(
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
}