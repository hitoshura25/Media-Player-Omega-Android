package com.vmenon.mpo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ShowModel::class,
        parentColumns = arrayOf("showId"),
        childColumns = arrayOf("episodeShowId")
    )],
    indices = [Index("episodeShowId")],
    tableName = "episode"
)
data class EpisodeModel(
    val episodeName: String,
    val description: String,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long,
    val episodeArtworkUrl: String?,
    @PrimaryKey(autoGenerate = true) val episodeId: Long = 0L,
    val episodeShowId: Long,
    var filename: String
)