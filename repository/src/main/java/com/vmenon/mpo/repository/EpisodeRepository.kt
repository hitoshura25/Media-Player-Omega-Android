package com.vmenon.mpo.repository

import com.vmenom.mpo.model.EpisodeModel
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import io.reactivex.Flowable
import io.reactivex.Single

class EpisodeRepository(private val episodeDao: EpisodeDao) {
    fun save(episode: EpisodeModel): Single<EpisodeModel> = Single.fromCallable {
        episode.copy(id = episodeDao.insertOrUpdate(episode.toEntity()).id)
    }

    fun getAll(): Flowable<List<EpisodeModel>> =
        episodeDao.getAllWithShowDetails().map { episodesWithShowDetails ->
            episodesWithShowDetails.map { it.toModel()  }
        }

    fun getById(id: Long): Flowable<EpisodeModel> =
        episodeDao.getWithShowDetailsById(id).map { it.toModel() }
}