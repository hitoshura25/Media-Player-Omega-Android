package com.vmenon.mpo.persistence.room.entity

import androidx.room.*
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShowEntity::class,
            parentColumns = arrayOf("showId"),
            childColumns = arrayOf("showId")
        ),
        ForeignKey(
            entity = EpisodeEntity::class,
            parentColumns = arrayOf("episodeId"),
            childColumns = arrayOf("episodeId")
        )
    ],
    indices = [Index("showId"), Index("episodeId")],
    tableName = "downloads"
)
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val downloadId: Long,
    val showId: Long,
    val episodeId: Long,
    @Embedded
    val details: DownloadDetailsEntity
) : BaseEntity<DownloadEntity> {
    override fun id(): Long = downloadId
    override fun copyWithNewId(newId: Long): DownloadEntity = copy(downloadId = newId)
}
