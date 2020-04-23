package com.vmenon.mpo.persistence.room.entity

import androidx.room.*

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
    @PrimaryKey(autoGenerate = true) override val id: Long = BaseEntity.UNSAVED_ID,
    val showId: Long,
    val episodeId: Long,
    @Embedded
    val details: DownloadDetailsEntity
) : BaseEntity<DownloadEntity> {
    override fun copyWithNewId(newId: Long): DownloadEntity = copy(id = newId)
}
