package com.vmenon.mpo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShowModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("showId")
        ),
        ForeignKey(
            entity = EpisodeModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("episodeId")
        )
    ],
    indices = [Index("showId"), Index("episodeId")],
    tableName = "downloads"
)
data class DownloadModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val showId: Long,
    val episodeId: Long,
    var total: Int = 0,
    var progress: Int = 0
) {
    @Synchronized
    fun addProgress(progress: Int) {
        this.progress += progress
    }
}
