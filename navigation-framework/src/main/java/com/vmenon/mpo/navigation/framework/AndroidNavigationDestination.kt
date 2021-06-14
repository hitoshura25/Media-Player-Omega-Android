package com.vmenon.mpo.navigation.framework

import android.os.Bundle
import androidx.navigation.NavDirections
import com.vmenon.mpo.navigation.domain.*

data class AndroidNavigationDestination<L : NavigationLocation<*>>(
    override val location: L,
    val navDirectionMapper: (params: NavigationParams) -> NavDirections
) : NavigationDestination<L> {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <P: NavigationParams, L: NavigationLocation<P>> fromParams(
            location: L,
            navDirectionMapper: (params: P) -> NavDirections
        ) = AndroidNavigationDestination(location,
            navDirectionMapper as (NavigationParams) -> NavDirections
        )

        fun <L : NavigationLocation<NoNavigationParams>> fromNoParams(
            location: L,
            actionId: Int
        ) = AndroidNavigationDestination(location) {
            object : NavDirections {
                override fun getActionId(): Int = actionId
                override fun getArguments(): Bundle = Bundle()
            }
        }
    }
}