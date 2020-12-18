package com.vmenon.mpo.search.repository.impl

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.shows.repository.EpisodeRepository
import io.reactivex.Flowable

class EpisodeRepositoryImpl(
    private val episodePersistence: EpisodePersistence
) : EpisodeRepository {
    override suspend fun save(episode: EpisodeModel): EpisodeModel =
        episodePersistence.insertOrUpdate(episode)

    override fun getAll(): Flowable<List<EpisodeModel>> = episodePersistence.getAll()
    override fun getById(id: Long): Flowable<EpisodeModel> = episodePersistence.getById(id)
}