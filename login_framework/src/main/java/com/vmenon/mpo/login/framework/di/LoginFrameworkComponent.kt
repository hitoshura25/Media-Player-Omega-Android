package com.vmenon.mpo.login.framework.di

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.login.data.UserCache
import com.vmenon.mpo.login.data.UserRegistry
import com.vmenon.mpo.login.domain.LoginService
import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class],
    modules = [LoginFrameworkModule::class]
)
@LoginFrameworkScope
interface LoginFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun build(): LoginFrameworkComponent
    }

    fun loginService(): LoginService
    fun userRegistry(): UserRegistry
    fun userCache(): UserCache
}