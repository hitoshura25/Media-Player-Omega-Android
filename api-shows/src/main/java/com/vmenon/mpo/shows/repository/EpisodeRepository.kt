package com.vmenon.mpo.shows.repository

import com.vmenon.mpo.model.EpisodeModel

interface EpisodeRepository {
    suspend fun save(episode: EpisodeModel): EpisodeModel
    suspend fun getAll(): List<EpisodeModel>
    suspend fun getById(id: Long): EpisodeModel?
}