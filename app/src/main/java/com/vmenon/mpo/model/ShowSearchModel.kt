package com.vmenon.mpo.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["searchTerm"], unique = true)], tableName = "showSearch")
data class ShowSearchModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val searchTerm: String
)