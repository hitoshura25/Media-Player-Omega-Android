package com.vmenon.mpo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val name: String,
    val description: String,
    val published: Long,
    val type: String,
    val downloadUrl: String,
    val length: Long,
    val artworkUrl: String?,
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val showId: Long,
    var filename: String
)