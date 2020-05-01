package com.vmenon.mpo.persistence.room.base.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

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