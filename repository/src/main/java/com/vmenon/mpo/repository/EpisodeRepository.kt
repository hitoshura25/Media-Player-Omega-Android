package com.vmenon.mpo.repository

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.persistence.EpisodePersistence
import io.reactivex.Flowable
import io.reactivex.Single

class EpisodeRepository(private val episodePersistence: EpisodePersistence) {
    fun save(episode: EpisodeModel): Single<EpisodeModel> = Single.fromCallable {
        episodePersistence.insertOrUpdate(episode)
    }

    fun getAll(): Flowable<List<EpisodeModel>> = episodePersistence.getAll()
    fun getById(id: Long): Flowable<EpisodeModel> = episodePersistence.getById(id)
}