package com.vmenon.mpo.persistence.room.entity

import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = ShowEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("showId")
    )],
    indices = [Index("showId")],
    tableName = "episode"
)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long = BaseEntity.UNSAVED_ID,
    val showId: Long,
    @Embedded
    val details: EpisodeDetailsEntity
) : BaseEntity<EpisodeEntity> {
    override fun copyWithNewId(newId: Long): EpisodeEntity = copy(id = newId)
}