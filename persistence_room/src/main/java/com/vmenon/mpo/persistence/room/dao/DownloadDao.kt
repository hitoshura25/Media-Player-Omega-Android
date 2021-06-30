package com.vmenon.mpo.persistence.room.dao

import androidx.room.*
import com.vmenon.mpo.persistence.room.base.dao.BaseDao
import com.vmenon.mpo.persistence.room.entity.DownloadEntity

@Dao
abstract class DownloadDao :
    BaseDao<DownloadEntity> {
    @Query(
        """
        SELECT * from downloads
        WHERE downloadQueueId = :id
        """
    )
    abstract suspend fun getByDownloadManagerId(id: Long): DownloadEntity

    @Query(
        """
        SELECT * from downloads
        """
    )
    abstract suspend fun getAll(): List<DownloadEntity>

    @Query("DELETE FROM downloads WHERE downloadId = :id")
    abstract suspend fun delete(id: Long)
}