package com.vmenon.mpo.shows.persistence

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.persistence.BasePersistence
import io.reactivex.Flowable

interface EpisodePersistence : BasePersistence<EpisodeModel> {
    fun getAll(): Flowable<List<EpisodeModel>>
    fun getById(id: Long): Flowable<EpisodeModel>
}