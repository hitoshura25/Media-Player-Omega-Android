package com.vmenon.mpo

import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.persistence.room.test.di.dagger.DaggerTestPersistenceComponent

class TestMPOApplication : MPOApplication() {
    override fun onCreate() {
        super.onCreate()
        println("I'm the test app!")
    }

    override fun createPersistenceComponent(): PersistenceComponent =
        DaggerTestPersistenceComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()

    override fun apiUrl(): String = "http://localhost:8080/"
}