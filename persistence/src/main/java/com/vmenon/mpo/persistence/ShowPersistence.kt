package com.vmenon.mpo.persistence

import com.vmenon.mpo.model.ShowModel
import io.reactivex.Flowable
import io.reactivex.Maybe

interface ShowPersistence : BasePersistence<ShowModel> {
    fun getByName(name: String): Maybe<ShowModel>
    fun getSubscribed(): Flowable<List<ShowModel>>
    fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): Maybe<List<ShowModel>>
}