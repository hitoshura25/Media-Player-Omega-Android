package com.vmenon.mpo.di

import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.core.ThirdPartyIntegrator
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.search.domain.SearchNavigationLocation

import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class],
    modules = [
        AppModule::class,
        ThirdPartyIntegratorModule::class,
        NavigationModule::class
    ]
)
@AppScope
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun appModule(module: AppModule): Builder
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun thirdPartyIntegratorModule(module: ThirdPartyIntegratorModule): Builder
        fun build(): AppComponent
    }

    fun thirdPartyIntegrator(): ThirdPartyIntegrator

    fun activityComponent(): ActivityComponent.Factory

    fun navigationController(): NavigationController

    fun playerDestination(): NavigationDestination<PlayerNavigationLocation>

    fun searchDestination(): NavigationDestination<SearchNavigationLocation>
}
