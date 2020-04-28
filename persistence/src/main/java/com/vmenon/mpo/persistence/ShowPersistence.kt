package com.vmenon.mpo.persistence

import com.vmenon.mpo.model.ShowModel
import io.reactivex.Flowable
import io.reactivex.Maybe

interface ShowPersistence {
    fun getByName(name: String): Maybe<ShowModel>
    fun getSubscribed(): Flowable<List<ShowModel>>
    fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): Maybe<List<ShowModel>>
    fun insert(show: ShowModel): ShowModel
    fun update(show: ShowModel)
}