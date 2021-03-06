package com.vmenon.mpo.persistence.room.base.entity

interface BaseEntity<T: BaseEntity<T>> {
    fun id(): Long
    fun copyWithNewId(newId: Long): T

    companion object {
        const val UNSAVED_ID = 0L
    }
}