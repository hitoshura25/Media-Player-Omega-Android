package com.vmenon.mpo.core.persistence

import androidx.room.Dao
import androidx.room.Query

import com.vmenon.mpo.model.ShowModel

import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
abstract class ShowDao : BaseDao<ShowModel> {
    @Query("SELECT * FROM show where showName = :name")
    abstract fun getByName(name: String): Maybe<ShowModel>

    @Query("SELECT * FROM show WHERE isSubscribed")
    abstract fun getSubscribed(): Flowable<List<ShowModel>>

    @Query("SELECT * FROM show WHERE isSubscribed AND lastUpdate < :comparisonTime")
    abstract fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): Maybe<List<ShowModel>>
}
