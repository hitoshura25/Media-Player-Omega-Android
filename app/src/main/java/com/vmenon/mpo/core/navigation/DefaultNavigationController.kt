package com.vmenon.mpo.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.downloads.view.activity.DownloadsActivity
import com.vmenon.mpo.view.activity.HomeActivity
import com.vmenon.mpo.library.view.activity.LibraryActivity
import com.vmenon.mpo.navigation.domain.NavigationController.Location
import com.vmenon.mpo.navigation.domain.NavigationView
import com.vmenon.mpo.player.view.activity.MediaPlayerActivity
import java.io.Serializable

class DefaultNavigationController : NavigationController {
    override fun onNavigationSelected(
        location: Location,
        navigationView: NavigationView,
        navigationParams: Map<String, Any>?
    ) {
        if (navigationView !is Context) {
            throw IllegalArgumentException("navigationView needs to be a Context!")
        }
        val intent = when (location) {
            Location.PLAYER -> Intent(navigationView, MediaPlayerActivity::class.java)
            Location.DOWNLOADS -> Intent(navigationView, DownloadsActivity::class.java)
            Location.HOME -> Intent(navigationView, HomeActivity::class.java)
            Location.LIBRARY ->Intent(navigationView, LibraryActivity::class.java)
        }

        startActivityForNavigation(intent, navigationView, navigationParams)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getParams(navigationView: NavigationView): Map<String, Any>? =
        (navigationView as Activity).intent.getSerializableExtra(EXTRA_NAVIGATION_BUNDLE) as? Map<String, Any>

    private fun startActivityForNavigation(
        intent: Intent,
        context: Context,
        navigationParams: Map<String, Any>?
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