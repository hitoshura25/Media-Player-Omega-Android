package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.persistence.room.dao.EpisodeDao

class EpisodePersistenceRoom(private val episodeDao: EpisodeDao) :
    EpisodePersistence {
    override suspend fun getByName(name: String): EpisodeModel? =
        episodeDao.getByNameWithShowDetails(name)?.toModel()

    override suspend fun getAll(): List<EpisodeModel> =
        episodeDao.getAllWithShowDetails().map { it.toModel() }

    override suspend fun getById(id: Long): EpisodeModel? =
        episodeDao.getWithShowDetailsById(id)?.toModel()

    override fun insertOrUpdate(model: EpisodeModel): EpisodeModel =
        episodeDao.insertOrUpdate(model.toEntity()).toModel(model.show)
}