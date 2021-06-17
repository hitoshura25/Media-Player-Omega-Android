package com.vmenon.mpo.di

import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.api.di.dagger.ApiModule
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.core.ThirdPartyIntegrator
import com.vmenon.mpo.navigation.domain.NavigationController

import com.vmenon.mpo.persistence.di.dagger.PersistenceModule
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        ThirdPartyIntegratorModule::class,
        PersistenceModule::class,
        ApiModule::class,
        NavigationModule::class
    ]
)
@AppScope
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun appModule(module: AppModule): Builder
        fun thirdPartyIntegratorModule(module: ThirdPartyIntegratorModule): Builder
        fun build(): AppComponent
    }

    fun thirdPartyIntegrator(): ThirdPartyIntegrator

    fun activityComponent(): ActivityComponent.Factory

    fun navigationController(): NavigationController

    fun system(): System
}
