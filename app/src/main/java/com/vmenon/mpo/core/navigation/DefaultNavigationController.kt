package com.vmenon.mpo.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.vmenon.mpo.downloads.view.activity.DownloadsActivity
import com.vmenon.mpo.library.view.activity.LibraryActivity
import com.vmenon.mpo.navigation.domain.*
import com.vmenon.mpo.navigation.framework.ActivityDestination
import com.vmenon.mpo.view.DrawerNavigationRequest
import com.vmenon.mpo.view.R
import com.vmenon.mpo.view.activity.HomeActivity
import java.io.Serializable

class DefaultNavigationController : NavigationController {
    override fun onNavigationSelected(
        request: NavigationRequest<*, *>,
        navigationSource: NavigationSource<*>
    ) {
        if (navigationSource !is Context) {
            throw IllegalArgumentException("navigationView needs to be a Context!")
        }

        if (request is DrawerNavigationRequest) {
            when (request.destination.menuId) {
                R.id.nav_downloads -> startActivityForNavigation(
                    Intent(navigationSource, DownloadsActivity::class.java),
                    navigationSource
                )
                R.id.nav_library -> startActivityForNavigation(
                    Intent(navigationSource, LibraryActivity::class.java),
                    navigationSource
                )
                R.id.nav_home -> startActivityForNavigation(
                    Intent(navigationSource, HomeActivity::class.java),
                    navigationSource
                )
            }
            return
        }

        if (request.destination !is ActivityDestination) {
            throw IllegalArgumentException("request.destination needs to be an ActivityDestination!")
        }

        val intent = Intent(
            navigationSource,
            (request.destination as ActivityDestination).activityClass
        )
        startActivityForNavigation(intent, navigationSource, request.params)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <P : NavigationParams> getParams(navigationSource: NavigationSource<P>): P {
        if (navigationSource !is Activity) {
            throw IllegalArgumentException("navigationSource needs to be an Activity!")
        }
        return navigationSource.intent.getSerializableExtra(EXTRA_NAVIGATION_BUNDLE) as P
    }

    private fun startActivityForNavigation(
        intent: Intent,
        context: Context,
        navigationParams: NavigationParams? = null
    ) {
        navigationParams?.let { params ->
            intent.putExtra(EXTRA_NAVIGATION_BUNDLE, params as Serializable)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    companion object {
        const val EXTRA_NAVIGATION_BUNDLE = "extraNavigationBundle"
    }
}