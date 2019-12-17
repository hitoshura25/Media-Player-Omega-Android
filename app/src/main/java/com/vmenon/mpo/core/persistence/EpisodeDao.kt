package com.vmenon.mpo.core.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.model.EpisodeModel

import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update

@Dao
interface EpisodeDao {
    @Insert
    fun insert(episode: EpisodeModel): Long

    @Update(onConflict = REPLACE)
    fun update(episode: EpisodeModel)

    @Query("SELECT * FROM episode")
    fun load(): LiveData<List<EpisodeModel>>

    @Query("SELECT * from episode WHERE id = :id")
    fun byId(id: Long): EpisodeModel

    @Query("SELECT * from episode WHERE id = :id")
    fun liveById(id: Long): LiveData<EpisodeModel>
}
