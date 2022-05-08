package com.vmenon.mpo

import android.app.Application
import com.vmenon.mpo.auth.framework.di.dagger.BiometricsComponent
import com.vmenon.mpo.auth.test.di.dagger.DaggerTestBiometricsComponent
import com.vmenon.mpo.di.DaggerComponentProviders
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.persistence.room.test.di.dagger.DaggerTestPersistenceComponent

class CucumberDaggerComponentProviders(application: Application, apiUrl: String) :
    DaggerComponentProviders(application, apiUrl) {
    override fun createPersistenceComponent(): PersistenceComponent =
        DaggerTestPersistenceComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()

    override fun createBiometricsComponent(): BiometricsComponent {
        return DaggerTestBiometricsComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()
    }
}