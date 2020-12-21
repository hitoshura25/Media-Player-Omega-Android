package com.vmenon.mpo.my_library.framework

import com.vmenon.mpo.my_library.data.ShowPersistenceDataSource
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.persistence.room.entity.ShowDetailsEntity
import com.vmenon.mpo.persistence.room.entity.ShowEntity

class RoomShowPersistenceDataSource(private val showDao: ShowDao) : ShowPersistenceDataSource {
    override suspend fun insertOrUpdate(showModel: ShowModel): ShowModel =
        showDao.insertOrUpdate(showModel.toEntity()).toModel()

    override suspend fun getByName(name: String): ShowModel? = showDao.getByName(name)?.toModel()

    override suspend fun getSubscribed(): List<ShowModel> =
        showDao.getSubscribed().map { entity -> entity.toModel() }

    override suspend fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): List<ShowModel> =
        showDao.getSubscribedAndLastUpdatedBefore(comparisonTime).map { entity -> entity.toModel() }

    private fun ShowEntity.toModel() = ShowModel(
        id = showId,
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

    private fun ShowModel.toEntity() = ShowEntity(
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
        showId = id
    )
}