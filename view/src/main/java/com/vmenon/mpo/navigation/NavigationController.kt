package com.vmenon.mpo.navigation

import android.content.Context

interface NavigationController {
    fun onNavigationSelected(navigationId: Int, context: Context)
}