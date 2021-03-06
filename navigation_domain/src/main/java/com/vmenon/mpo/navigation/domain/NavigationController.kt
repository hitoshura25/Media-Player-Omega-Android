package com.vmenon.mpo.navigation.domain

import kotlinx.coroutines.flow.Flow

interface NavigationController {
    fun <P : NavigationParams, L : NavigationLocation<P>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>,
        navigationParams: P
    )

    fun <L : NavigationLocation<NoNavigationParams>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>
    ) {
        navigate(navigationOrigin, navigationDestination, NoNavigationParams)
    }

    fun setOrigin(navigationOrigin: NavigationOrigin<*>)

    fun <P : NavigationParams> getParams(
        navigationOrigin: NavigationOrigin<P>
    ): P

    fun <P : NavigationParams> getOptionalParams(
        navigationOrigin: NavigationOrigin<P>
    ): P?

    fun <T : Any, P : NavigationParams> createNavigationRequest(
        context: Any,
        params: P,
        navigationDestination: NavigationDestination<out NavigationLocation<P>>
    ): T

    fun setupWith(navigationOrigin: NavigationOrigin<*>, vararg component: Any?)

    val currentLocation: Flow<NavigationLocation<*>>
}