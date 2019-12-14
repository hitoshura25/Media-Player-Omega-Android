package com.vmenon.mpo.core.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.api.Episode

import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface EpisodeDao {
    @Insert(onConflict = REPLACE)
    fun save(episode: Episode)

    @Query("SELECT * FROM episode")
    fun load(): LiveData<List<Episode>>

    @Query("SELECT * from episode WHERE id = :id")
    fun byId(id: Long): Episode

    @Query("SELECT * from episode WHERE id = :id")
    fun liveById(id: Long): LiveData<Episode>
}
