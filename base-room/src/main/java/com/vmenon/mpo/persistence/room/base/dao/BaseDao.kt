package com.vmenon.mpo.persistence.room.base.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity

interface BaseDao<T : BaseEntity<T>> {
    @Insert
    suspend fun insert(entity: T): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: T)

    suspend fun insertOrUpdate(entity: T): T =
        if (entity.id() == BaseEntity.UNSAVED_ID) {
            entity.copyWithNewId(newId = insert(entity))
        } else {
            update(entity)
            entity
        }
}