package com.vmenon.mpo.persistence.room.entity

import androidx.room.*
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

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
    @PrimaryKey(autoGenerate = true) override val id: Long,
    val showId: Long,
    @Embedded
    val details: EpisodeDetailsEntity
) : BaseEntity<EpisodeEntity> {
    override fun copyWithNewId(newId: Long): EpisodeEntity = copy(id = newId)
}