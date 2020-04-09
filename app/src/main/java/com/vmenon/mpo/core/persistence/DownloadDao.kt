package com.vmenon.mpo.core.persistence

import androidx.room.*
import com.vmenon.mpo.model.DownloadModel
import io.reactivex.Flowable

@Dao
interface DownloadDao {
    @Insert
    fun insert(download: DownloadModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(download: DownloadModel)

    @Query("SELECT * FROM downloads")
    fun load(): Flowable<List<DownloadModel>>

    @Query("SELECT * from downloads WHERE id = :id")
    fun byId(id: Long): Flowable<DownloadModel>

    @Query("DELETE FROM downloads WHERE id = :id")
    fun delete(id: Long)
}