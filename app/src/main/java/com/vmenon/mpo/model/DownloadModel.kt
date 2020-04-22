package com.vmenon.mpo.model

import androidx.room.*
import com.vmenon.mpo.core.persistence.BaseEntity

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
    @PrimaryKey(autoGenerate = true) override val id: Long = BaseEntity.UNSAVED_ID,
    val showId: Long,
    val episodeId: Long,
    @Embedded
    val details: DownloadDetailsModel
) : BaseEntity<DownloadModel> {
    override fun copyWithNewId(newId: Long): DownloadModel = copy(id = newId)
}
