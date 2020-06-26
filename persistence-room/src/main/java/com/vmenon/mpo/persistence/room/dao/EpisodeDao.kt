package com.vmenon.mpo.persistence.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vmenon.mpo.persistence.room.base.dao.BaseDao

import com.vmenon.mpo.persistence.room.entity.EpisodeEntity
import com.vmenon.mpo.persistence.room.entity.EpisodeWithShowDetailsEntity
import io.reactivex.Flowable

@Dao
abstract class EpisodeDao :
    BaseDao<EpisodeEntity> {
    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.showId
        """
    )
    abstract fun getAllWithShowDetails(): Flowable<List<EpisodeWithShowDetailsEntity>>

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.showId
        WHERE episode.episodeId = :id
        """
    )
    abstract fun getWithShowDetailsById(id: Long): Flowable<EpisodeWithShowDetailsEntity>
}
