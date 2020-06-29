package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import io.reactivex.Flowable
import io.reactivex.Maybe

class EpisodePersistenceRoom(private val episodeDao: EpisodeDao) :
    EpisodePersistence {
    override fun getByName(name: String): Maybe<EpisodeModel> =
        episodeDao.getByNameWithShowDetails(name).map {
            it.toModel()
        }

    override fun getAll(): Flowable<List<EpisodeModel>> =
        episodeDao.getAllWithShowDetails().map { episodes -> episodes.map { it.toModel() } }

    override fun getById(id: Long): Flowable<EpisodeModel> =
        episodeDao.getWithShowDetailsById(id).map { it.toModel() }

    override fun insertOrUpdate(model: EpisodeModel): EpisodeModel =
        episodeDao.insertOrUpdate(model.toEntity()).toModel(model.show)
}