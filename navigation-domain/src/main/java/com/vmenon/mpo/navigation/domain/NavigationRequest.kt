package com.vmenon.mpo.navigation.domain

interface NavigationRequest<D: NavigationDestination, P: NavigationParams> {
    val destination: D
    val params: P
}