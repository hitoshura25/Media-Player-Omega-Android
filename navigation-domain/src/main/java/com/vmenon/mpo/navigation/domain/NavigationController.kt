package com.vmenon.mpo.navigation.domain

interface NavigationController {
    enum class Location {
        HOME,
        LIBRARY,
        DOWNLOADS,
        PLAYER
    }

    fun onNavigationSelected(
        location: Location,
        navigationView: NavigationView,
        navigationParams: Map<String, Any>? = null
    )

    fun getParams(navigationView: NavigationView): Map<String, Any>?
}