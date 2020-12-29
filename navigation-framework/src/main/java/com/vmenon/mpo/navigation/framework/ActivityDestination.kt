package com.vmenon.mpo.navigation.framework

import android.content.Context
import android.content.Intent
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationParams

data class ActivityDestination<L : NavigationLocation<*>>(
    val activityClass: Class<*>
) : NavigationDestination<L> {
    fun createIntent(context: Context, params: NavigationParams): Intent =
        Intent(context, activityClass).apply {
            putExtra(
                EXTRA_NAVIGATION_BUNDLE, params
            )
        }

    companion object {
        const val EXTRA_NAVIGATION_BUNDLE = "extraNavigationBundle"
    }
}