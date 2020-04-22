package com.vmenon.mpo.model

import androidx.room.*

@Entity(
    indices = [
        Index(value = ["showName"]),
        Index("showSearchResultsSearchId")
    ],
    tableName = "showSearchResults",
    foreignKeys = [ForeignKey(
        entity = ShowSearchModel::class,
        parentColumns = arrayOf("showSearchId"),
        childColumns = arrayOf("showSearchResultsSearchId")
    )]
)
data class ShowSearchResultsModel(
    @Embedded
    val showDetails: ShowDetailsModel,
    @PrimaryKey(autoGenerate = true)
    val showSearchResultsId: Long = 0L,
    val showSearchResultsSearchId: Long
)
