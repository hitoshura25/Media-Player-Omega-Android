package com.vmenon.mpo.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)], tableName = "show")
data class SubscribedShowModel(
    @Embedded
    val show: ShowModel,

    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var lastUpdate: Long = -1L,
    var lastEpisodePublished: Long = -1L
)
