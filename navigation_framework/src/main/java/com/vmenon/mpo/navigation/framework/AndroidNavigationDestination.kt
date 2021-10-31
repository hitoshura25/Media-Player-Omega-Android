package com.vmenon.mpo.navigation.framework

import android.os.Bundle
import androidx.navigation.NavDirections
import com.vmenon.mpo.navigation.domain.*

data class AndroidNavigationDestination<L : NavigationLocation<*>>(
    override val location: L,
    val destinationId: Int,
    val navDirectionMapper: (params: NavigationParams) -> NavDirections
) : NavigationDestination<L> {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <P : NavigationParams, L : NavigationLocation<P>> fromParams(
            location: L,
            destinationId: Int,
            navDirectionMapper: (params: P) -> NavDirections
        ) = AndroidNavigationDestination(
            location,
            destinationId,
            navDirectionMapper as (NavigationParams) -> NavDirections
        )

        fun <L : NavigationLocation<NoNavigationParams>> fromNoParams(
            location: L, destinationId: Int
        ) = AndroidNavigationDestination(location, destinationId {
            object : NavDirections {
                override val actionId: Int
                    get() = destinationId // DestinationId works for actionId
                override val arguments: Bundle
                    get() = Bundle()
            }
        }
    }
}