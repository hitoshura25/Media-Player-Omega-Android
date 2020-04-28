package com.vmenon.mpo.persistence.room.entity

import androidx.room.*

@Entity(
    indices = [
        Index(value = ["showName"]),
        Index("showSearchResultsSearchId")
    ],
    tableName = "showSearchResults",
    foreignKeys = [ForeignKey(
        entity = ShowSearchEntity::class,
        parentColumns = arrayOf("showSearchId"),
        childColumns = arrayOf("showSearchResultsSearchId")
    )]
)
data class ShowSearchResultsEntity(
    @Embedded
    val showDetails: ShowDetailsEntity,
    @PrimaryKey(autoGenerate = true)
    val showSearchResultsId: Long,
    val showSearchResultsSearchId: Long
)
