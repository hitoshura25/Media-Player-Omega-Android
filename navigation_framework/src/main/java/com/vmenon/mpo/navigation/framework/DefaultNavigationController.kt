package com.vmenon.mpo.navigation.framework

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NavigationParams
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class DefaultNavigationController(
    private val topLevelItems: Map<Int, NavigationDestination<out NavigationLocation<NoNavigationParams>>>,
    private val hostFragmentId: Int,
    private val navGraphId: Int
) : NavigationController {
    private val origin: MutableSharedFlow<NavigationLocation<*>> = MutableSharedFlow()
    private val mainScope = MainScope()

    override fun <P : NavigationParams, L : NavigationLocation<P>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>,
        navigationParams: P
    ) {
        require(navigationDestination is AndroidNavigationDestination) {
            "request.destination is invalid or unsupported!"
        }
        handleAndroidNavigationDestination(
            navigationOrigin,
            navigationDestination,
            navigationParams,
        )
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
        return requireNotNull(getOptionalParams(navigationOrigin)) {
            "required parameters were not set!"
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <P : NavigationParams> getOptionalParams(navigationOrigin: NavigationOrigin<P>): P? {
        if (navigationOrigin is Activity) {
            return navigationOrigin.intent.getSerializableExtra(
                NAVIGATION_PARAMS_NAME
            ) as P?
        }
        if (navigationOrigin is Fragment) {
            return navigationOrigin.arguments?.getSerializable(
                NAVIGATION_PARAMS_NAME
            ) as P?
        }

        throw IllegalArgumentException("navigationOrigin is invalid!")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any, P : NavigationParams> createNavigationRequest(
        context: Any,
        params: P,
        navigationDestination: NavigationDestination<out NavigationLocation<P>>
    ): T {
        require(context is Context) {
            "context is not a Context!"
        }
        require(navigationDestination is AndroidNavigationDestination) {
            "navigationDestination is not a AndroidNavigationDestination!"
        }

        val directions = navigationDestination.navDirectionMapper(params)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(navGraphId)
            .setDestination(navigationDestination.destinationId)
            .setArguments(directions.arguments)
            .createPendingIntent()
        return pendingIntent as T
    }

    override fun setupWith(navigationOrigin: NavigationOrigin<*>, vararg component: Any?) {
        var toolbar: Toolbar? = null
        var bottomNavigationView: BottomNavigationView? = null
        var drawerLayout: DrawerLayout? = null
        var collapsingToolbarLayout: CollapsingToolbarLayout? = null
        var navigationView: NavigationView? = null

        component.forEach { element ->
            when (element) {
                is Toolbar -> toolbar = element
                is CollapsingToolbarLayout -> collapsingToolbarLayout = element
                is NavigationView -> navigationView = element
                is BottomNavigationView -> bottomNavigationView = element
                is DrawerLayout -> drawerLayout = element
                else -> throw IllegalArgumentException("component was not a supported type!")
            }
        }

        val navController = getNavController(navigationOrigin)
        val appBarConfiguration = AppBarConfiguration(topLevelItems.keys, drawerLayout)
        setupWithToolbar(
            navController,
            toolbar,
            collapsingToolbarLayout,
            drawerLayout,
            appBarConfiguration
        )
        setupBottomNavigationView(bottomNavigationView, navController, navigationOrigin)
        navigationView?.setupWithNavController(navController)
    }

    private fun setupBottomNavigationView(
        bottomNavigationView: BottomNavigationView?,
        navController: NavController,
        navigationOrigin: NavigationOrigin<*>
    ) {
        if (bottomNavigationView != null) {
            val originWeakReference = WeakReference(navigationOrigin)
            bottomNavigationView.setOnItemSelectedListener { menuItem: MenuItem ->
                topLevelItems[menuItem.itemId]?.let { destination ->
                    val origin = originWeakReference.get()
                    if (origin != null) {
                        navigate(navigationOrigin, destination)
                    }
                    true
                } ?: false
            }
            bottomNavigationView.setOnItemReselectedListener { }

            val bottomNavigationRef = WeakReference(bottomNavigationView)
            val listener = object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val bottomNav = bottomNavigationRef.get()
                    if (bottomNav == null) {
                        navController.removeOnDestinationChangedListener(this)
                    } else {
                        val menuItemId = destination.parent?.id ?: destination.id
                        bottomNav.menu.findItem(menuItemId)?.isChecked = true
                    }
                }
            }
            navController.addOnDestinationChangedListener(listener)
        }
    }

    private fun setupWithToolbar(
        navController: NavController,
        toolbar: Toolbar?,
        collapsingToolbarLayout: CollapsingToolbarLayout?,
        drawerLayout: DrawerLayout?,
        appBarConfiguration: AppBarConfiguration
    ) {
        if (collapsingToolbarLayout != null && toolbar != null) {
            if (drawerLayout != null) {
                collapsingToolbarLayout.setupWithNavController(
                    toolbar,
                    navController,
                    drawerLayout
                )
            } else {
                collapsingToolbarLayout.setupWithNavController(
                    toolbar,
                    navController,
                    appBarConfiguration
                )
            }
        } else {
            if (drawerLayout != null) {
                toolbar?.setupWithNavController(navController, drawerLayout)
            } else {
                toolbar?.setupWithNavController(navController, appBarConfiguration)
            }
        }
    }

    override val currentLocation: Flow<NavigationLocation<*>>
        get() = origin.asSharedFlow()

    private fun handleAndroidNavigationDestination(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: AndroidNavigationDestination<*>,
        params: NavigationParams
    ) {
        val navigationController = when (navigationOrigin) {
            is Fragment -> navigationOrigin.findNavController()
            is FragmentActivity -> {
                val navHostFragment = navigationOrigin.supportFragmentManager.findFragmentById(
                    hostFragmentId
                ) as NavHostFragment
                navHostFragment.navController
            }
            else -> null
        } ?: throw IllegalArgumentException(
            "navigationOrigin needs to be an Activity or a Fragment!"
        )
        navigationController.navigate(
            navigationDestination.navDirectionMapper(params),
            NavOptions.Builder()
                .setExitAnim(0)
                .setEnterAnim(0)
                .setPopExitAnim(0)
                .setPopEnterAnim(0)
                .setLaunchSingleTop(true)
                .build()
        )
    }

    private fun getNavController(navigationOrigin: NavigationOrigin<*>) =
        when (navigationOrigin) {
            is Fragment -> navigationOrigin.findNavController()
            is FragmentActivity -> {
                val navHostFragment = navigationOrigin.supportFragmentManager.findFragmentById(
                    hostFragmentId
                ) as NavHostFragment
                navHostFragment.navController
            }
            else -> null
        } ?: throw IllegalArgumentException(
            "navigationOrigin needs to be an Activity or a Fragment!"
        )

    companion object {
        const val NAVIGATION_PARAMS_NAME = "params" // Should match nav_graph.xml
    }
}