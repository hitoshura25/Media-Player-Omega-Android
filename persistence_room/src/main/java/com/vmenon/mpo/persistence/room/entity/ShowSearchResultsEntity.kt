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
    val showName: String,
    val showArtworkUrl: String?,
    val genres: List<String>,
    val author: String,
    val feedUrl: String,
    val showDescription: String,
    val lastUpdate: Long,
    val lastEpisodePublished: Long,
    val isSubscribed: Boolean,
    @PrimaryKey(autoGenerate = true)
    val showSearchResultsId: Long,
    val showSearchResultsSearchId: Long
)
