package com.vmenon.mpo.persistence.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.vmenon.mpo.persistence.room.base.dao.BaseDao

import com.vmenon.mpo.persistence.room.entity.EpisodeEntity
import com.vmenon.mpo.persistence.room.entity.EpisodeWithShowDetailsEntity

@Dao
abstract class EpisodeDao :
    BaseDao<EpisodeEntity> {
    @Query(
        """
        SELECT * FROM episode INNER JOIN show on episode.showId = show.showId
        WHERE episodeName = :name
        """
    )
    abstract suspend fun getByNameWithShowDetails(name: String): EpisodeWithShowDetailsEntity?

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.showId
        """
    )
    abstract suspend fun getAllWithShowDetails(): List<EpisodeWithShowDetailsEntity>

    @Query(
        """
        SELECT * from episode 
        INNER JOIN show on episode.showId = show.showId
        WHERE episode.episodeId = :id
        """
    )
    abstract suspend fun getWithShowDetailsById(id: Long): EpisodeWithShowDetailsEntity
}
