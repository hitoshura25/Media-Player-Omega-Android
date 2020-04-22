package com.vmenon.mpo.model

import androidx.room.*
import com.vmenon.mpo.core.persistence.BaseEntity

@Entity(
    foreignKeys = [ForeignKey(
        entity = ShowModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("showId")
    )],
    indices = [Index("showId")],
    tableName = "episode"
)
data class EpisodeModel(
    @PrimaryKey(autoGenerate = true) override val id: Long = BaseEntity.UNSAVED_ID,
    val showId: Long,
    @Embedded
    val details: EpisodeDetailsModel
) : BaseEntity<EpisodeModel> {
    override fun copyWithNewId(newId: Long): EpisodeModel = copy(id = newId)
}