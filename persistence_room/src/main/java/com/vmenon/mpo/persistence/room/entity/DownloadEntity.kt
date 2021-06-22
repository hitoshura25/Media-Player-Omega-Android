package com.vmenon.mpo.persistence.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val downloadId: Long,
    val requesterId: Long,
    val downloadRequestType: String,
    val downloadQueueId: Long,
    val downloadUrl: String,
    val name: String,
    val imageUrl: String?,
    @ColumnInfo(defaultValue = "0") val downloadAttempt: Int
) : BaseEntity<DownloadEntity> {
    override fun id(): Long = downloadId
    override fun copyWithNewId(newId: Long): DownloadEntity = copy(downloadId = newId)
}
