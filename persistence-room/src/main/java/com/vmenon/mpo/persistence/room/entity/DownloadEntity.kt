package com.vmenon.mpo.persistence.room.entity

import androidx.room.*
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShowEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("showId")
        ),
        ForeignKey(
            entity = EpisodeEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("episodeId")
        )
    ],
    indices = [Index("showId"), Index("episodeId")],
    tableName = "downloads"
)
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    val showId: Long,
    val episodeId: Long,
    @Embedded
    val details: DownloadDetailsEntity
) : BaseEntity<DownloadEntity> {
    override fun copyWithNewId(newId: Long): DownloadEntity = copy(id = newId)
}
