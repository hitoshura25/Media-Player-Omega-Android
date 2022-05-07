package com.vmenon.mpo.auth.framework.di.dagger

import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.framework.Authenticator
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(
    modules = [AuthModule::class, AuthStateModule::class],
    dependencies = [SystemFrameworkComponent::class, BiometricsComponent::class]
)
@AuthScope
interface AuthComponent {
    @Component.Builder
    interface Builder {
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun biometricsComponent(component: BiometricsComponent): Builder
        fun build(): AuthComponent
    }

    fun authService(): AuthService
    fun authState(): AuthState
    fun authenticator(): Authenticator
    fun biometricsManager(): BiometricsManager
}