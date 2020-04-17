package com.vmenon.mpo.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["showName"], unique = true)], tableName = "show")
data class ShowModel(
    @Embedded
    val showDetails: ShowDetailsModel,

    @PrimaryKey(autoGenerate = true)
    val showId: Long = 0L,
    var lastUpdate: Long = -1L,
    var lastEpisodePublished: Long = -1L,
    var isSubscribed: Boolean = false
)
