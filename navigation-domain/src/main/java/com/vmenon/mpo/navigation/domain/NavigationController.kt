package com.vmenon.mpo.navigation.domain

import kotlinx.coroutines.flow.Flow

interface NavigationController {
    fun onNavigationSelected(
        request: NavigationRequest<*, *>,
        navigationOrigin: NavigationOrigin<*>
    )

    fun <P: NavigationParams> getParams(navigationOrigin: NavigationOrigin<P>): P
    val currentLocation: Flow<NavigationOrigin<*>>
}