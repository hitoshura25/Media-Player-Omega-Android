package com.vmenon.mpo.persistence.room.dao

import androidx.room.*
import com.vmenon.mpo.persistence.room.entity.DownloadEntity
import com.vmenon.mpo.persistence.room.entity.DownloadWithShowAndEpisodeDetailsEntity
import io.reactivex.Flowable

@Dao
abstract class DownloadDao :
    BaseDao<DownloadEntity> {
    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.id
        INNER JOIN show on downloads.showId = show.id
        WHERE downloadManagerId = :id
        """
    )
    abstract fun getWithShowAndEpisodeDetailsByDownloadManagerId(id: Long): Flowable<DownloadWithShowAndEpisodeDetailsEntity>

    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.id
        INNER JOIN show on downloads.showId = show.id
        """
    )
    abstract fun getAllWithShowAndEpisodeDetails(): Flowable<List<DownloadWithShowAndEpisodeDetailsEntity>>

    @Query("DELETE FROM downloads WHERE id = :id")
    abstract fun delete(id: Long)
}