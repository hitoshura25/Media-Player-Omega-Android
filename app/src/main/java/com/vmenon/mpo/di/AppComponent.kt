package com.vmenon.mpo.di

import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.core.ThirdPartyIntegrator

import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class],
    modules = [
        ThirdPartyIntegratorModule::class
    ]
)
@AppScope
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun thirdPartyIntegratorModule(module: ThirdPartyIntegratorModule): Builder
        fun build(): AppComponent
    }

    fun thirdPartyIntegrator(): ThirdPartyIntegrator

    fun activityComponent(): ActivityComponent.Factory
}
