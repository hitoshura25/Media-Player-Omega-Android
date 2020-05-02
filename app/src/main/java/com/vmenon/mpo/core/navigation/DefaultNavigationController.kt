package com.vmenon.mpo.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.vmenon.mpo.navigation.NavigationController
import com.vmenon.mpo.downloads.view.activity.DownloadsActivity
import com.vmenon.mpo.view.activity.HomeActivity
import com.vmenon.mpo.library.view.activity.LibraryActivity
import com.vmenon.mpo.navigation.NavigationController.Companion.EXTRA_NAVIGATION_BUNDLE
import com.vmenon.mpo.navigation.NavigationController.Location
import com.vmenon.mpo.view.activity.MediaPlayerActivity

class DefaultNavigationController : NavigationController {
    override fun onNavigationSelected(
        location: Location,
        context: Context,
        navigationBundle: Bundle?
    ) {
        val intent = when (location) {
            Location.PLAYER -> Intent(context, MediaPlayerActivity::class.java)
            Location.DOWNLOADS -> Intent(context, DownloadsActivity::class.java)
            Location.HOME -> Intent(context, HomeActivity::class.java)
            Location.LIBRARY ->Intent(context, LibraryActivity::class.java)
        }

        startActivityForNavigation(intent, context, navigationBundle)
    }

    override fun getParams(activity: Activity): Bundle =
        activity.intent.getBundleExtra(EXTRA_NAVIGATION_BUNDLE) ?: Bundle.EMPTY

    private fun startActivityForNavigation(
        intent: Intent,
        context: Context,
        navigationBundle: Bundle?
    ) {
        navigationBundle?.let {
            intent.putExtra(EXTRA_NAVIGATION_BUNDLE, it)
        }
        context.startActivity(intent)
    }
}