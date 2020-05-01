package com.vmenon.mpo.shows.repository

import com.vmenon.mpo.model.EpisodeModel
import io.reactivex.Flowable
import io.reactivex.Single

interface EpisodeRepository {
    fun save(episode: EpisodeModel): Single<EpisodeModel>
    fun getAll(): Flowable<List<EpisodeModel>>
    fun getById(id: Long): Flowable<EpisodeModel>
}