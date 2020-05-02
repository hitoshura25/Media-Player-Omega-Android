package com.vmenon.mpo.persistence.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["searchTerm"], unique = true)], tableName = "showSearch")
data class ShowSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val showSearchId: Long,
    val searchTerm: String
)