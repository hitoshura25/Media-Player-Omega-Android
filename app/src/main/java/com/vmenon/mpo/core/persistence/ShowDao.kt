package com.vmenon.mpo.core.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.model.ShowModel

import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface ShowDao {
    @Query("SELECT * FROM show where showName = :name")
    fun getByName(name: String): Maybe<ShowModel>

    @Insert
    fun insert(show: ShowModel): Long

    @Update(onConflict = REPLACE)
    fun update(show: ShowModel)

    @Query("SELECT * FROM show WHERE isSubscribed")
    fun loadAllSubscribed(): Flowable<List<ShowModel>>

    @Query("SELECT * FROM show WHERE isSubscribed AND lastUpdate < :comparisonTime")
    fun loadSubscribedLastUpdatedBefore(comparisonTime: Long): Maybe<List<ShowModel>>
}
