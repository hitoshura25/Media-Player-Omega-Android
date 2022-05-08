package com.vmenon.mpo.di

import android.app.Application
import android.util.Log
import com.vmenon.mpo.BuildConfig
import com.vmenon.mpo.R
import com.vmenon.mpo.auth.framework.di.dagger.BiometricsComponent
import com.vmenon.mpo.auth.framework.di.dagger.DaggerAuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.DaggerBiometricsComponent
import com.vmenon.mpo.common.framework.di.dagger.DaggerCommonFrameworkComponent
import com.vmenon.mpo.navigation.framework.di.dagger.DaggerNavigationFrameworkComponent
import com.vmenon.mpo.persistence.di.dagger.DaggerPersistenceComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.system.framework.AndroidBuildConfigProvider
import com.vmenon.mpo.system.framework.di.dagger.DaggerSystemFrameworkComponent

open class DaggerComponentProviders(application: Application, apiUrl: String) {
    val systemFrameworkComponent = DaggerSystemFrameworkComponent.builder()
        .application(application)
        .buildConfigProvider(
            AndroidBuildConfigProvider(
                BuildConfig.VERSION_NAME,
                BuildConfig.buildNumber
            )
        ).build()

    private val navigationFrameworkComponent = DaggerNavigationFrameworkComponent.builder()
        .systemFrameworkComponent(systemFrameworkComponent)
        .hostFragmentId(R.id.nav_host_fragment)
        .build()

    val biometricsComponent by lazy { createBiometricsComponent() }

    val authComponent by lazy {
        DaggerAuthComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .biometricsComponent(biometricsComponent)
            .build()
    }

    val persistenceComponent by lazy { createPersistenceComponent() }

    val commonFrameworkComponent by lazy {
        DaggerCommonFrameworkComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .authComponent(authComponent)
            .persistenceComponent(persistenceComponent)
            .navigationFrameworkComponent(navigationFrameworkComponent)
            .apiUrl(apiUrl)
            .build()
    }

    protected open fun createBiometricsComponent(): BiometricsComponent {
        Log.d("MPO", "Creating normal biometric component")
        return DaggerBiometricsComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()
    }

    protected open fun createPersistenceComponent(): PersistenceComponent =
        DaggerPersistenceComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()
}