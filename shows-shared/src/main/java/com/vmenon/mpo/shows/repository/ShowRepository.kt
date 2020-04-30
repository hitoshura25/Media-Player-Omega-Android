package com.vmenon.mpo.shows.repository

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface ShowRepository {
    fun getSubscribed(): Flowable<List<ShowModel>>
    fun save(show: ShowModel): Single<ShowModel>
    fun getSubscribedAndLastUpdatedBefore(interval: Long): Maybe<List<ShowModel>>
    fun getShowUpdate(show: ShowModel): Maybe<ShowUpdateModel>
}