package com.vmenon.mpo.auth.framework.di.dagger

import com.vmenon.mpo.auth.data.AuthState
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.framework.Authenticator
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(modules = [AuthModule::class], dependencies = [SystemFrameworkComponent::class])
@AuthScope
interface AuthComponent {
    @Component.Builder
    interface Builder {
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun build(): AuthComponent
    }

    fun authService(): AuthService
    fun authState(): AuthState
    fun authenticator(): Authenticator
}