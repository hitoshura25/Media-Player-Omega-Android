package com.vmenon.mpo.navigation.framework.di.dagger

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.navigation.framework.DefaultNavigationController
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object NavigationFrameworkModule {
    @Provides
    @NavigationFrameworkScope
    fun providesNavigationController(
        @Named("navigationTopLevelItems") topLevelItems: Map<Int, NavigationDestination<out NavigationLocation<NoNavigationParams>>>,
        @Named("navigationHostFragmentId") hostFragmentId: Int,
        @Named("navigationGraphId") navGraphId: Int,
        system: System
    ): NavigationController = DefaultNavigationController(
        topLevelItems,
        system,
        hostFragmentId,
        navGraphId
    )
}