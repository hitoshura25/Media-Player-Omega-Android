package com.vmenon.mpo.model

import androidx.room.*

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
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val showId: Long,
    @Embedded
    val details: EpisodeDetailsModel
)