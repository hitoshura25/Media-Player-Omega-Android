package com.vmenon.mpo.navigation

import android.app.Activity
import android.content.Context
import android.os.Bundle

interface NavigationController {
    enum class Location {
        HOME,
        LIBRARY,
        DOWNLOADS,
        PLAYER
    }

    fun onNavigationSelected(
        location: Location,
        context: Context,
        navigationBundle: Bundle?
    )

    fun getParams(activity: Activity): Bundle

    companion object {
        const val EXTRA_NAVIGATION_BUNDLE = "extraNavigationBundle"
    }
}