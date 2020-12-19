package com.vmenon.mpo.shows.persistence

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.persistence.BasePersistence

interface EpisodePersistence : BasePersistence<EpisodeModel> {
    suspend fun getByName(name: String): EpisodeModel?
    suspend fun getAll(): List<EpisodeModel>
    suspend fun getById(id: Long): EpisodeModel?
}