package com.vmenon.mpo.search.repository.impl

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.shows.persistence.EpisodePersistence
import com.vmenon.mpo.shows.repository.EpisodeRepository

class EpisodeRepositoryImpl(
    private val episodePersistence: EpisodePersistence
) : EpisodeRepository {
    override suspend fun save(episode: EpisodeModel): EpisodeModel =
        episodePersistence.insertOrUpdate(episode)

    override suspend fun getAll(): List<EpisodeModel> = episodePersistence.getAll()
    override suspend fun getById(id: Long): EpisodeModel? = episodePersistence.getById(id)
}