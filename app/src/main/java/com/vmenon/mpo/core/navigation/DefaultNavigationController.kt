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
        navigationOrigin: NavigationOrigin<*>
    ) {
        if (navigationOrigin !is Context) {
            throw IllegalArgumentException("navigationOrigin needs to be a Context!")
        }

        if (request is DrawerNavigationRequest) {
            when (request.destination.menuId) {
                R.id.nav_downloads -> startActivityForNavigation(
                    Intent(navigationOrigin, DownloadsActivity::class.java),
                    navigationOrigin
                )
                R.id.nav_library -> startActivityForNavigation(
                    Intent(navigationOrigin, LibraryActivity::class.java),
                    navigationOrigin
                )
                R.id.nav_home -> startActivityForNavigation(
                    Intent(navigationOrigin, HomeActivity::class.java),
                    navigationOrigin
                )
            }
            return
        }

        if (request.destination !is ActivityDestination) {
            throw IllegalArgumentException("request.destination needs to be an ActivityDestination!")
        }

        val intent = Intent(
            navigationOrigin,
            (request.destination as ActivityDestination).activityClass
        )
        startActivityForNavigation(intent, navigationOrigin, request.params)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <P : NavigationParams> getParams(navigationOrigin: NavigationOrigin<P>): P {
        if (navigationOrigin !is Activity) {
            throw IllegalArgumentException("navigationOrigin needs to be an Activity!")
        }
        return navigationOrigin.intent.getSerializableExtra(EXTRA_NAVIGATION_BUNDLE) as P
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