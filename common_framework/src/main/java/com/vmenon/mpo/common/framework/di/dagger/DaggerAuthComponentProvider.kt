package com.vmenon.mpo.common.framework.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.auth.framework.di.dagger.BiometricsComponent
import com.vmenon.mpo.auth.framework.di.dagger.DaggerAuthComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent

class DaggerAuthComponentProvider(
    systemFrameworkComponent: SystemFrameworkComponent,
    biometricsComponent: BiometricsComponent
) : AuthComponentProvider {
    private val authComponent = DaggerAuthComponent.builder()
        .systemFrameworkComponent(systemFrameworkComponent)
        .biometricsComponent(biometricsComponent)
        .build()

    override fun authComponent(): AuthComponent = authComponent
}