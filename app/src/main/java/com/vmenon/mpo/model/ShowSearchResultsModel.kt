package com.vmenon.mpo.model

import androidx.room.*

@Entity(
    indices = [
        Index(value = ["name"]),
        Index("showSearchId")
    ],
    tableName = "showSearchResults",
    foreignKeys = [ForeignKey(
        entity = ShowSearchModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("showSearchId")
    )]
)
data class ShowSearchResultsModel(
    @Embedded
    val showDetails: ShowDetailsModel,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val showSearchId: Long
)
