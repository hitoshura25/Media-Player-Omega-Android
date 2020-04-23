package com.vmenon.mpo.persistence.room.dao

import androidx.room.Dao
import androidx.room.Query

import com.vmenon.mpo.persistence.room.entity.EpisodeEntity
import com.vmenon.mpo.persistence.room.entity.EpisodeWithShowDetailsEntity
import io.reactivex.Flowable

@Dao
abstract class EpisodeDao :
    BaseDao<EpisodeEntity> {
    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.id
        """
    )
    abstract fun getAllWithShowDetails(): Flowable<List<EpisodeWithShowDetailsEntity>>

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.id
        WHERE episode.id = :id
        """
    )
    abstract fun getWithShowDetailsById(id: Long): Flowable<EpisodeWithShowDetailsEntity>
}
