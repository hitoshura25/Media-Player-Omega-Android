package com.vmenon.mpo.navigation.framework

import android.content.Context
import android.content.Intent
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationParams

data class ActivityDestination<L : NavigationLocation<*>>(
    val activityClass: Class<*>, override val location: L
) : NavigationDestination<L> {
    fun <P : NavigationParams> createIntent(context: Context, key: String, params: P): Intent =
        Intent(context, activityClass).apply {
            putExtra(key, params)
        }
}