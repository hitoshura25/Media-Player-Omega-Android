package com.vmenon.mpo.navigation.domain

interface NavigationController {
    fun onNavigationSelected(
        request: NavigationRequest<*, *>,
        navigationSource: NavigationSource<*>
    )

    fun <P: NavigationParams> getParams(navigationSource: NavigationSource<P>): P
}