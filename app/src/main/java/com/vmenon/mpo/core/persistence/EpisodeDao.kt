package com.vmenon.mpo.core.persistence

import androidx.room.Dao
import androidx.room.Query

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.EpisodeWithShowDetailsModel
import io.reactivex.Flowable

@Dao
abstract class EpisodeDao : BaseDao<EpisodeModel> {
    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.id
        """
    )
    abstract fun getAllWithShowDetails(): Flowable<List<EpisodeWithShowDetailsModel>>

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.id
        WHERE episode.id = :id
        """
    )
    abstract fun getWithShowDetailsById(id: Long): Flowable<EpisodeWithShowDetailsModel>
}
