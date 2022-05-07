package com.vmenon.mpo.common.framework.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.auth.framework.di.dagger.BiometricsComponent
import com.vmenon.mpo.auth.framework.di.dagger.DaggerAuthComponent
import com.vmenon.mpo.navigation.framework.di.dagger.NavigationFrameworkComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider

class DaggerComponentProviders(
    val systemFrameworkComponent: SystemFrameworkComponent,
    biometricsComponent: BiometricsComponent,
    navigationFrameworkComponent: NavigationFrameworkComponent,
    persistenceComponent: PersistenceComponent,
    apiUrl: String,
) : SystemFrameworkComponentProvider, AuthComponentProvider, CommonFrameworkComponentProvider {

    private val authComponent = DaggerAuthComponent.builder()
        .systemFrameworkComponent(systemFrameworkComponent)
        .biometricsComponent(biometricsComponent)
        .build()
    private val commonFrameworkComponentProvider = DaggerCommonFrameworkComponentProvider(
        systemFrameworkComponent,
        authComponent,
        persistenceComponent,
        navigationFrameworkComponent,
        apiUrl
    )

    override fun authComponent(): AuthComponent = authComponent

    override fun commonFrameworkComponent(): CommonFrameworkComponent =
        commonFrameworkComponentProvider.commonFrameworkComponent()

    override fun systemFrameworkComponent(): SystemFrameworkComponent = systemFrameworkComponent
}