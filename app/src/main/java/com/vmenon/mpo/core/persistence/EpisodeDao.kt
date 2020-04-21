package com.vmenon.mpo.core.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.vmenon.mpo.model.EpisodeModel

import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update
import com.vmenon.mpo.model.EpisodeWithShowDetailsModel
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
        INNER JOIN show on episode.showId = show.id
        """
    )
    fun getAllWithShowDetails(): Flowable<List<EpisodeWithShowDetailsModel>>

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.id
        WHERE episode.id = :id
        """
    )
    fun getWithShowDetailsById(id: Long): Flowable<EpisodeWithShowDetailsModel>
}
