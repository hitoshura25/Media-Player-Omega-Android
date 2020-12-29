package com.vmenon.mpo.navigation.domain

interface NavigationOrigin<L : NavigationLocation<*>> {
    val location: L
}