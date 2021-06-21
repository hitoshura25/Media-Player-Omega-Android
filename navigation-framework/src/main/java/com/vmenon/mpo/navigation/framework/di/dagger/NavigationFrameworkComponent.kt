package com.vmenon.mpo.navigation.framework.di.dagger

import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

// TODO: This is currently unused
@Component(modules = [NavigationFrameworkModule::class])
@NavigationFrameworkScope
interface NavigationFrameworkComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun topLevelItems(
            @Named("navigationTopLevelItems") topLevelItems: Map<Int, NavigationDestination<out NavigationLocation<NoNavigationParams>>>
        ): Builder

        @BindsInstance
        fun hostFragmentId(@Named("navigationHostFragmentId") hostFragmentId: Int): Builder

        @BindsInstance fun navGraphId(@Named("navigationGraphId") graphId: Int): Builder

        fun navigationFrameworkModule(module: NavigationFrameworkModule): Builder
        fun build(): NavigationFrameworkComponent
    }
}