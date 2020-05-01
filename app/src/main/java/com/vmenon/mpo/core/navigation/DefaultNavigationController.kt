package com.vmenon.mpo.core.navigation

import android.content.Context
import android.content.Intent
import com.vmenon.mpo.R
import com.vmenon.mpo.navigation.NavigationController
import com.vmenon.mpo.downloads.view.activity.DownloadsActivity
import com.vmenon.mpo.view.activity.HomeActivity
import com.vmenon.mpo.view.activity.LibraryActivity

class DefaultNavigationController : NavigationController {
    override fun onNavigationSelected(navigationId: Int, context: Context) {
        when (navigationId) {
            R.id.nav_downloads -> {
                val intent = Intent(
                    context,
                    DownloadsActivity::class.java
                )
                context.startActivity(intent)
            }
            R.id.nav_library -> {
                val intent = Intent(context, LibraryActivity::class.java)
                context.startActivity(intent)
            }
            R.id.nav_home -> {
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}