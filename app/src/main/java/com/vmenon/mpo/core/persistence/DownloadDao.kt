package com.vmenon.mpo.core.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vmenon.mpo.model.DownloadModel
import io.reactivex.Flowable

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(download: DownloadModel): Long

    @Query("SELECT * FROM downloads")
    fun load(): Flowable<List<DownloadModel>>

    @Query("SELECT * from downloads WHERE id = :id")
    fun byId(id: Long): Flowable<DownloadModel>
}