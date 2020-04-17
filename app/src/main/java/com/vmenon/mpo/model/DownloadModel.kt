package com.vmenon.mpo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShowModel::class,
            parentColumns = arrayOf("showId"),
            childColumns = arrayOf("downloadShowId")
        ),
        ForeignKey(
            entity = EpisodeModel::class,
            parentColumns = arrayOf("episodeId"),
            childColumns = arrayOf("downloadEpisodeId")
        )
    ],
    indices = [Index("downloadShowId"), Index("downloadEpisodeId")],
    tableName = "downloads"
)
data class DownloadModel(
    @PrimaryKey(autoGenerate = true) val downloadId: Long = 0L,
    val downloadShowId: Long,
    val downloadEpisodeId: Long,
    val downloadManagerId: Long
)
