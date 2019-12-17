package com.vmenon.mpo.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)], tableName = "showSearchResults")
data class ShowSearchResultsModel(
    @Embedded
    val showDetails: ShowDetailsModel,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L
)
