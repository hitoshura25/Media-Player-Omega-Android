package com.vmenon.mpo.navigation.domain

interface NavigationController {
    fun onNavigationSelected(
        request: NavigationRequest<*, *>,
        navigationOrigin: NavigationOrigin<*>
    )

    fun <P: NavigationParams> getParams(navigationOrigin: NavigationOrigin<P>): P
}