package com.vmenon.mpo.shows.persistence

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.persistence.BasePersistence
import io.reactivex.Flowable
import io.reactivex.Maybe

interface EpisodePersistence : BasePersistence<EpisodeModel> {
    fun getByName(name: String): Maybe<EpisodeModel>
    fun getAll(): Flowable<List<EpisodeModel>>
    fun getById(id: Long): Flowable<EpisodeModel>
}