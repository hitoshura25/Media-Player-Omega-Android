package com.vmenon.mpo.persistence.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vmenon.mpo.persistence.room.base.dao.BaseDao

import com.vmenon.mpo.persistence.room.entity.ShowEntity

import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
abstract class ShowDao :
    BaseDao<ShowEntity> {
    @Query("SELECT * FROM show where showName = :name")
    abstract fun getByName(name: String): Maybe<ShowEntity>

    @Query("SELECT * FROM show WHERE isSubscribed")
    abstract fun getSubscribed(): Flowable<List<ShowEntity>>

    @Query("SELECT * FROM show WHERE isSubscribed AND lastUpdate < :comparisonTime")
    abstract fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): Maybe<List<ShowEntity>>
}
