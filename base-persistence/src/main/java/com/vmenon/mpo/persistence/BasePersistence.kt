package com.vmenon.mpo.persistence

interface BasePersistence<Model> {
    suspend fun insertOrUpdate(model: Model): Model
}