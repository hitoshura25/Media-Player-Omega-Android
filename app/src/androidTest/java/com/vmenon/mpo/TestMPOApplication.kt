package com.vmenon.mpo

import com.vmenon.mpo.auth.framework.di.dagger.BiometricsComponent
import com.vmenon.mpo.auth.test.MockAndroidBiometricsManager
import com.vmenon.mpo.auth.test.di.dagger.DaggerTestBiometricsComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.persistence.room.test.di.dagger.DaggerTestPersistenceComponent

class TestMPOApplication : MPOApplication() {
    lateinit var mockBiometricsManager: MockAndroidBiometricsManager

    override fun onCreate() {
        super.onCreate()
        println("I'm the test app!")
    }

    override fun createPersistenceComponent(): PersistenceComponent =
        DaggerTestPersistenceComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()

    override fun createBiometricsComponent(): BiometricsComponent {
        val component = DaggerTestBiometricsComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()
        mockBiometricsManager = component.biometricsManager() as MockAndroidBiometricsManager
        return component
    }

    override fun apiUrl(): String = "https://localhost:8080/"
}