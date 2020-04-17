package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.model.ShowEpisodeDownloadModel
import io.reactivex.Flowable

@Dao
interface DownloadDao {
    @Insert
    fun insert(download: DownloadModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(download: DownloadModel)

    @Query("SELECT * FROM downloads")
    fun load(): Flowable<List<DownloadModel>>

    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.downloadEpisodeId = episode.episodeId
        INNER JOIN show on downloads.downloadShowId = show.showId
        WHERE downloadManagerId = :id
        """
    )
    fun byDownloadManagerId(id: Long): Flowable<ShowEpisodeDownloadModel>

    @Query(
        """
        SELECT * from downloads 
        INNER JOIN episode on downloads.downloadEpisodeId = episode.episodeId
        INNER JOIN show on downloads.downloadShowId = show.showId
        """
    )
    fun loadDownloadsWithShowAndEpisode(): Flowable<List<ShowEpisodeDownloadModel>>

    @Query("DELETE FROM downloads WHERE downloadId = :id")
    fun delete(id: Long)
}