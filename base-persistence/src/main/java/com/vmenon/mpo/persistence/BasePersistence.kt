package com.vmenon.mpo.persistence

interface BasePersistence<Model> {
    fun insertOrUpdate(model: Model): Model
}