package com.vmenon.mpo.navigation.domain

import kotlinx.coroutines.flow.Flow

interface NavigationController {
    fun <P: NavigationParams, L: NavigationLocation<P>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>,
        navigationParams: P
    )

    fun <L: NavigationLocation<NoNavigationParams>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>
    ) {
        navigate(navigationOrigin, navigationDestination, NoNavigationParams)
    }

    fun setOrigin(navigationOrigin: NavigationOrigin<*>)

    fun <P : NavigationParams, L: NavigationLocation<P>> getParams(
        navigationOrigin: NavigationOrigin<L>
    ): P

    val currentLocation: Flow<NavigationLocation<*>>
}