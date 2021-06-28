package com.vmenon.mpo.navigation.framework.di.dagger

import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.search.domain.SearchNavigationLocation
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@Component(
    dependencies = [SystemFrameworkComponent::class],
    modules = [NavigationFrameworkModule::class]
)
@NavigationFrameworkScope
interface NavigationFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        @BindsInstance
        fun hostFragmentId(@Named("navigationHostFragmentId") hostFragmentId: Int): Builder
        fun build(): NavigationFrameworkComponent
    }

    fun searchNavigationDestination(): NavigationDestination<SearchNavigationLocation>
    fun playerNavigationDestination(): NavigationDestination<PlayerNavigationLocation>
    fun navigationController(): NavigationController
}