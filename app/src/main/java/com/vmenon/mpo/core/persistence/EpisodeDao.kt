package com.vmenon.mpo.core.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.model.EpisodeModel

import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update
import io.reactivex.Flowable

@Dao
interface EpisodeDao {
    @Insert
    fun insert(episode: EpisodeModel): Long

    @Update(onConflict = REPLACE)
    fun update(episode: EpisodeModel)

    @Query("SELECT * FROM episode")
    fun load(): Flowable<List<EpisodeModel>>

    @Query("SELECT * from episode WHERE episodeId = :id")
    fun byId(id: Long): Flowable<EpisodeModel>
}
