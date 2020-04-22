package com.vmenon.mpo.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vmenon.mpo.core.persistence.BaseEntity

@Entity(indices = [Index(value = ["showName"], unique = true)], tableName = "show")
data class ShowModel(
    @Embedded
    val details: ShowDetailsModel,

    @PrimaryKey(autoGenerate = true)
    override val id: Long = BaseEntity.UNSAVED_ID
) : BaseEntity<ShowModel> {
    override fun copyWithNewId(newId: Long): ShowModel = copy(id = newId)
}
