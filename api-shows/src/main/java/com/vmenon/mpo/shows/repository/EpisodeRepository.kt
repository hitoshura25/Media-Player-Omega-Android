package com.vmenon.mpo.shows.repository

import com.vmenon.mpo.model.EpisodeModel
import io.reactivex.Flowable

interface EpisodeRepository {
    suspend fun save(episode: EpisodeModel): EpisodeModel
    fun getAll(): Flowable<List<EpisodeModel>>
    fun getById(id: Long): Flowable<EpisodeModel>
}