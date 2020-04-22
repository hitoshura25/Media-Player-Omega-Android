package com.vmenon.mpo.core.persistence

interface BaseEntity<T: BaseEntity<T>> {
    val id: Long
    fun copyWithNewId(newId: Long): T

    companion object {
        const val UNSAVED_ID = 0L
    }
}