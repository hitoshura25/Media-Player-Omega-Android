package com.vmenon.mpo.navigation.domain

interface NavigationOrigin<P : NavigationParams> {
    val location: NavigationLocation<P>

    fun <P : NavigationParams, L : NavigationLocation<P>> navigateTo(
        destination: NavigationDestination<L>,
        params: P
    )

    fun getNavigationParamJson(): String?
}
