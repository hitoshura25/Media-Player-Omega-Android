package com.vmenon.mpo.navigation.domain

interface NavigationDestination<L: NavigationLocation<*>> {
    val location: L
}