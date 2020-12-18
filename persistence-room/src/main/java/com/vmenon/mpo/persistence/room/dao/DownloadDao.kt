package com.vmenon.mpo.persistence.room.dao

import androidx.room.*
import com.vmenon.mpo.persistence.room.base.dao.BaseDao
import com.vmenon.mpo.persistence.room.entity.DownloadEntity
import com.vmenon.mpo.persistence.room.entity.DownloadWithShowAndEpisodeDetailsEntity

@Dao
abstract class DownloadDao :
    BaseDao<DownloadEntity> {
    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.episodeId
        INNER JOIN show on downloads.showId = show.showId
        WHERE downloadManagerId = :id
        """
    )
    abstract suspend fun getWithShowAndEpisodeDetailsByDownloadManagerId(id: Long): DownloadWithShowAndEpisodeDetailsEntity

    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.episodeId
        INNER JOIN show on downloads.showId = show.showId
        """
    )
    abstract suspend fun getAllWithShowAndEpisodeDetails(): List<DownloadWithShowAndEpisodeDetailsEntity>

    @Query("DELETE FROM downloads WHERE downloadId = :id")
    abstract suspend fun delete(id: Long)
}