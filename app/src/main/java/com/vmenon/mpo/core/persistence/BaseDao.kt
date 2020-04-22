package com.vmenon.mpo.core.persistence

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T : BaseEntity<T>> {
    @Insert
    fun insert(entity: T): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: T)

    fun insertOrUpdate(entity: T): T =
        if (entity.id == BaseEntity.UNSAVED_ID) {
            entity.copyWithNewId(newId = insert(entity))
        } else {
            update(entity)
            entity
        }
}