package com.vmenon.mpo.persistence.room.entity

import androidx.room.*
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val downloadId: Long,
    val requesterId: Long,
    val downloadRequestType: String,
    val downloadQueueId: Long,
    val downloadUrl: String,
    val name: String,
    val imageUrl: String?
) : BaseEntity<DownloadEntity> {
    override fun id(): Long = downloadId
    override fun copyWithNewId(newId: Long): DownloadEntity = copy(downloadId = newId)
}
