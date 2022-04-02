package com.vmenon.mpo.auth.framework.di.dagger

import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(modules = [BiometricsModule::class], dependencies = [SystemFrameworkComponent::class])
@BiometricsScope
interface BiometricsComponent {
    @Component.Builder
    interface Builder {
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun build(): BiometricsComponent
    }

    fun biometricsManager(): BiometricsManager
}