package com.vmenon.mpo.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.vmenon.mpo.navigation.domain.*
import com.vmenon.mpo.navigation.framework.ActivityDestination
import com.vmenon.mpo.navigation.framework.ActivityDestination.Companion.EXTRA_NAVIGATION_BUNDLE
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.view.DrawerNavigationDestination
import com.vmenon.mpo.view.R
import com.vmenon.mpo.view.activity.HomeActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DefaultNavigationController : NavigationController {
    private val origin: MutableSharedFlow<NavigationLocation<*>> = MutableSharedFlow()
    private val mainScope = MainScope()

    override fun <P : NavigationParams, L : NavigationLocation<P>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>,
        navigationParams: P
    ) {
        if (navigationDestination is DrawerNavigationDestination) {
            handleDrawerNavigationRequest(navigationDestination, navigationOrigin)
            return
        }

        when (navigationDestination) {
            is ActivityDestination -> handleActivityDestination(
                navigationOrigin,
                navigationDestination,
                navigationParams
            )
            is FragmentDestination -> handleFragmentDestination(
                navigationOrigin,
                navigationDestination,
                navigationParams
            )
            else -> {
                throw IllegalArgumentException("request.destination is invalid or unsupported!")
            }
        }
    }

    override fun setOrigin(navigationOrigin: NavigationOrigin<*>) {
        mainScope.launch {
            origin.emit(navigationOrigin.location)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <P : NavigationParams> getParams(
        navigationOrigin: NavigationOrigin<P>
    ): P {
        if (navigationOrigin is Activity) {
            return navigationOrigin.intent.getSerializableExtra(EXTRA_NAVIGATION_BUNDLE) as P
        }
        if (navigationOrigin is Fragment) {
            return navigationOrigin.requireArguments().getSerializable(EXTRA_NAVIGATION_BUNDLE) as P
        }

        throw IllegalArgumentException("navigationOrigin is invalid!")
    }

    override val currentLocation: Flow<NavigationLocation<*>>
        get() = origin.asSharedFlow()

    private fun handleDrawerNavigationRequest(
        navigationDestination: DrawerNavigationDestination,
        navigationOrigin: NavigationOrigin<*>
    ) {
        val context = when (navigationOrigin) {
            is Context -> navigationOrigin
            is Fragment -> navigationOrigin.requireActivity()
            else -> null
        } ?: throw IllegalArgumentException("navigationOrigin needs to be a Context or a Fragment!")

        when (navigationDestination.location.menuId) {
            R.id.nav_home -> startActivityForNavigation(
                Intent(context, HomeActivity::class.java),
                context
            )
        }
    }

    private fun handleActivityDestination(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: ActivityDestination<*>,
        params: NavigationParams
    ) {
        val context = when (navigationOrigin) {
            is Context -> navigationOrigin
            is Fragment -> navigationOrigin.activity
            else -> null
        } ?: throw IllegalArgumentException("navigationOrigin needs to be a Context or a Fragment!")
        val intent = navigationDestination.createIntent(context, params)
        startActivityForNavigation(intent, context)
    }

    private fun handleFragmentDestination(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: FragmentDestination<*>,
        params: NavigationParams
    ) {
        val fragmentManager = when (navigationOrigin) {
            is FragmentActivity -> navigationOrigin.supportFragmentManager
            is Fragment -> navigationOrigin.parentFragmentManager
            else -> null
        }
            ?: throw IllegalArgumentException("navigationOrigin needs to be an Activity or a Fragment!")
        val fragment =
            fragmentManager.findFragmentByTag(navigationDestination.tag)
                ?: navigationDestination.fragmentCreator()
        fragment.arguments = Bundle().apply { putSerializable(EXTRA_NAVIGATION_BUNDLE, params) }
        fragmentManager.beginTransaction()
            .replace(navigationDestination.containerId, fragment, navigationDestination.tag)
            .addToBackStack(null)
            .commit()
    }

    private fun startActivityForNavigation(intent: Intent, context: Context) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}