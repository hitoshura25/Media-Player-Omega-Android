package com.vmenon.mpo.persistence.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

@Entity(indices = [Index(value = ["showName"], unique = true)], tableName = "show")
data class ShowEntity(
    @Embedded
    val details: ShowDetailsEntity,

    @PrimaryKey(autoGenerate = true)
    val showId: Long
) : BaseEntity<ShowEntity> {
    override fun id(): Long = showId
    override fun copyWithNewId(newId: Long): ShowEntity = copy(showId = newId)
}
