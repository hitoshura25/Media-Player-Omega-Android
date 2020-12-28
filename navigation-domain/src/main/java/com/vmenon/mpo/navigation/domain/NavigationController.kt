package com.vmenon.mpo.navigation.domain

import kotlinx.coroutines.flow.Flow

interface NavigationController {
    fun navigate(
        request: NavigationRequest<*, *>,
        navigationOrigin: NavigationOrigin<*>
    )

    fun setOrigin(navigationOrigin: NavigationOrigin<*>)

    fun <P: NavigationParams> getParams(navigationOrigin: NavigationOrigin<P>): P
    val currentLocation: Flow<NavigationOrigin<*>>
}