package com.vmenon.mpo.core.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.model.ShowModel

import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update
import io.reactivex.Flowable

@Dao
interface ShowDao {

    @Query("SELECT * FROM show where id = :id")
    fun getById(id: Long): ShowModel

    @Query("SELECT * FROM show where id = :id")
    fun getLiveById(id: Long): LiveData<ShowModel>

    @Query("SELECT * FROM show where name = :name")
    fun getByName(name: String): ShowModel?

    @Insert
    fun insert(show: ShowModel): Long

    @Update(onConflict = REPLACE)
    fun update(show: ShowModel)

    @Query("SELECT * FROM show WHERE isSubscribed")
    fun loadAllSubscribed(): Flowable<List<ShowModel>>

    @Query("SELECT * FROM show WHERE isSubscribed AND lastUpdate < :comparisonTime")
    fun loadSubscribedLastUpdatedBefore(comparisonTime: Long): List<ShowModel>
}
