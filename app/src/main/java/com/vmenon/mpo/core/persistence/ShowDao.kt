package com.vmenon.mpo.core.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.model.SubscribedShowModel

import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface ShowDao {

    @Query("SELECT * FROM show where id = :id")
    fun getById(id: Long): SubscribedShowModel

    @Query("SELECT * FROM show where id = :id")
    fun getLiveById(id: Long): LiveData<SubscribedShowModel>

    @Insert(onConflict = REPLACE)
    fun save(show: SubscribedShowModel)

    @Query("SELECT * FROM show")
    fun load(): LiveData<List<SubscribedShowModel>>

    @Query("SELECT * FROM show WHERE lastUpdate < :comparisonTime")
    fun loadLastUpdatedBefore(comparisonTime: Long): List<SubscribedShowModel>
}
