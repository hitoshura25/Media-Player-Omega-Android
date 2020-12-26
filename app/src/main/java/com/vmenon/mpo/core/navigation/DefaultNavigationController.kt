package com.vmenon.mpo.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.vmenon.mpo.navigation.domain.*
import com.vmenon.mpo.navigation.framework.ActivityDestination
import com.vmenon.mpo.navigation.framework.ActivityDestination.Companion.EXTRA_NAVIGATION_BUNDLE
import com.vmenon.mpo.view.DrawerNavigationRequest
import com.vmenon.mpo.view.R
import com.vmenon.mpo.view.activity.HomeActivity

class DefaultNavigationController : NavigationController {
    override fun onNavigationSelected(
        request: NavigationRequest<*, *>,
        navigationOrigin: NavigationOrigin<*>
    ) {
        val context = when (navigationOrigin) {
            is Context -> navigationOrigin
            is Fragment -> navigationOrigin.activity
            else -> null
        }
            ?: throw IllegalArgumentException("navigationOrigin needs to be a Context or a Fragment!")

        if (request is DrawerNavigationRequest) {
            when (request.destination.menuId) {
                R.id.nav_home -> startActivityForNavigation(
                    Intent(context, HomeActivity::class.java),
                    context
                )
            }
            return
        }

        val destination = request.destination
        if (destination is ActivityDestination) {
            val intent = destination.createIntent(context, request.params)
            startActivityForNavigation(intent, context)
        } else {
            throw IllegalArgumentException("request.destination needs to be an ActivityDestination!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <P : NavigationParams> getParams(navigationOrigin: NavigationOrigin<P>): P {
        if (navigationOrigin !is Activity) {
            throw IllegalArgumentException("navigationOrigin needs to be an Activity!")
        }
        return navigationOrigin.intent.getSerializableExtra(EXTRA_NAVIGATION_BUNDLE) as P
    }

    private fun startActivityForNavigation(intent: Intent, context: Context) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}