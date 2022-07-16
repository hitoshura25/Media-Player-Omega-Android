package com.vmenon.mpo.navigation.framework.di.dagger

import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(
    dependencies = [SystemFrameworkComponent::class],
    modules = [NavigationFrameworkModule::class]
)
@NavigationFrameworkScope
interface NavigationFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun navigationFrameworkModule(module: NavigationFrameworkModule): Builder
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun build(): NavigationFrameworkComponent
    }

    fun searchNavigationDestination(): NavigationDestination<SearchNavigationLocation>
    fun playerNavigationDestination(): NavigationDestination<PlayerNavigationLocation>
    fun navigationController(): NavigationController
}