package com.vmenon.mpo.navigation.domain

interface NavigationOrigin<P : NavigationParams> {
    val location: NavigationLocation<P>
    companion object {
        fun <P : NavigationParams> from(location: NavigationLocation<P>) =
            object : NavigationOrigin<P> {
                override val location: NavigationLocation<P>
                    get() = location
            }
    }
}
