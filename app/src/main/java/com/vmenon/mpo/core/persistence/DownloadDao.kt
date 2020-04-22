package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.model.DownloadWithShowAndEpisodeDetailsModel
import io.reactivex.Flowable

@Dao
abstract class DownloadDao : BaseDao<DownloadModel> {
    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.id
        INNER JOIN show on downloads.showId = show.id
        WHERE downloadManagerId = :id
        """
    )
    abstract fun getWithShowAndEpisodeDetailsByDownloadManagerId(id: Long): Flowable<DownloadWithShowAndEpisodeDetailsModel>

    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.id
        INNER JOIN show on downloads.showId = show.id
        """
    )
    abstract fun getAllWithShowAndEpisodeDetails(): Flowable<List<DownloadWithShowAndEpisodeDetailsModel>>

    @Query("DELETE FROM downloads WHERE id = :id")
    abstract fun delete(id: Long)
}