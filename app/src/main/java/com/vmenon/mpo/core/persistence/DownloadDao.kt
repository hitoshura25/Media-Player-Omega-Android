package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.model.DownloadWithShowAndEpisodeDetailsModel
import io.reactivex.Flowable

@Dao
interface DownloadDao {
    @Insert
    fun insert(download: DownloadModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(download: DownloadModel)

    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.id
        INNER JOIN show on downloads.showId = show.id
        WHERE downloadManagerId = :id
        """
    )
    fun getWithShowAndEpisodeDetailsByDownloadManagerId(id: Long): Flowable<DownloadWithShowAndEpisodeDetailsModel>

    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.episodeId = episode.id
        INNER JOIN show on downloads.showId = show.id
        """
    )
    fun getAllWithShowAndEpisodeDetails(): Flowable<List<DownloadWithShowAndEpisodeDetailsModel>>

    @Query("DELETE FROM downloads WHERE id = :id")
    fun delete(id: Long)
}