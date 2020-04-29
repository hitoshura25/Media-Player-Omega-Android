package com.vmenon.mpo.persistence

import com.vmenon.mpo.model.EpisodeModel
import io.reactivex.Flowable

interface EpisodePersistence : BasePersistence<EpisodeModel> {
    fun getAll(): Flowable<List<EpisodeModel>>
    fun getById(id: Long): Flowable<EpisodeModel>
}