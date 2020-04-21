package com.vmenon.mpo.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["showName"], unique = true)], tableName = "show")
data class ShowModel(
    @Embedded
    val details: ShowDetailsModel,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L
)
