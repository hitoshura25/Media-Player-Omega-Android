package com.vmenon.mpo.core.repository

import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.model.EpisodeWithShowDetailsModel
import com.vmenon.mpo.model.EpisodeModel
import io.reactivex.Flowable
import io.reactivex.Single

class EpisodeRepository(private val episodeDao: EpisodeDao) {
    fun save(episode: EpisodeModel): Single<EpisodeModel> = Single.fromCallable {
        episodeDao.insertOrUpdate(episode)
    }

    fun getAll(): Flowable<List<EpisodeWithShowDetailsModel>> = episodeDao.getAllWithShowDetails()

    fun getById(id: Long): Flowable<EpisodeWithShowDetailsModel> =
        episodeDao.getWithShowDetailsById(id)
}