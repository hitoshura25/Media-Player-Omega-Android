package com.vmenon.mpo.core.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.model.EpisodeModel

import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update
import com.vmenon.mpo.model.EpisodeAndShowModel
import io.reactivex.Flowable

@Dao
interface EpisodeDao {
    @Insert
    fun insert(episode: EpisodeModel): Long

    @Update(onConflict = REPLACE)
    fun update(episode: EpisodeModel)

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.episodeShowId = show.showId
        """
    )
    fun load(): Flowable<List<EpisodeAndShowModel>>

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.episodeShowId = show.showId
        WHERE episodeId = :id
        """
    )
    fun byIdWithShow(id: Long): Flowable<EpisodeAndShowModel>
}
