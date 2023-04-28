package com.vmenon.mpo.di

import android.app.Application
import com.vmenon.mpo.BuildConfig
import com.vmenon.mpo.R
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.DaggerAuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.DaggerBiometricsComponent
import com.vmenon.mpo.common.framework.di.dagger.DaggerCommonFrameworkComponent
import com.vmenon.mpo.navigation.domain.downloads.DownloadsLocation
import com.vmenon.mpo.navigation.domain.login.LoginNavigationLocation
import com.vmenon.mpo.navigation.domain.my_library.MyLibraryNavigationLocation
import com.vmenon.mpo.navigation.domain.my_library.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import com.vmenon.mpo.navigation.framework.AndroidNavigationDestination
import com.vmenon.mpo.navigation.framework.DefaultNavigationController
import com.vmenon.mpo.navigation.framework.di.dagger.DaggerNavigationFrameworkComponent
import com.vmenon.mpo.navigation.framework.di.dagger.NavigationFrameworkModule
import com.vmenon.mpo.navigation.graph.NavGraphDirections
import com.vmenon.mpo.persistence.di.dagger.DaggerPersistenceComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.system.framework.AndroidBuildConfigProvider
import com.vmenon.mpo.system.framework.di.dagger.DaggerSystemFrameworkComponent

open class DaggerComponentProviders(application: Application, apiUrl: String) {
    private val navGraphId = R.navigation.nav_graph
    private val navHostFragmentId = R.id.nav_host_fragment
    private val loginDestinationId = R.id.login_nav_graph
    private val searchDestinationId = R.id.search_nav_graph
    private val libraryDestinationId = R.id.my_library_nav_graph
    private val playerDestinationId = R.id.player_nav_graph
    private val downloadsDestinationId = R.id.downloads_nav_graph
    private val showsDestinationId = R.id.subscribed_shows_navigation_graph

    val systemFrameworkComponent = DaggerSystemFrameworkComponent.builder()
        .application(application)
        .buildConfigProvider(
            AndroidBuildConfigProvider(
                BuildConfig.VERSION_NAME,
                BuildConfig.buildNumber
            )
        ).build()

    private val navigationFrameworkComponent = DaggerNavigationFrameworkComponent.builder()
        .systemFrameworkComponent(systemFrameworkComponent)
        .navigationFrameworkModule(createNavigationFrameworkModule())
        .build()

    val authComponent by lazy { createAuthComponent() }

    val persistenceComponent by lazy { createPersistenceComponent() }

    val commonFrameworkComponent by lazy {
        DaggerCommonFrameworkComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .authComponent(authComponent)
            .persistenceComponent(persistenceComponent)
            .navigationFrameworkComponent(navigationFrameworkComponent)
            .apiUrl(apiUrl)
            .build()
    }

    protected open fun createAuthComponent(): AuthComponent {
        return DaggerAuthComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .biometricsComponent(
                DaggerBiometricsComponent.builder()
                    .systemFrameworkComponent(systemFrameworkComponent)
                    .build()
            )
            .build()
    }

    protected open fun createPersistenceComponent(): PersistenceComponent =
        DaggerPersistenceComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()

    private fun createNavigationFrameworkModule(): NavigationFrameworkModule {
        val account = AndroidNavigationDestination.fromNoParams(
            LoginNavigationLocation,
            loginDestinationId,
        )
        val downloads = AndroidNavigationDestination.fromNoParams(
            DownloadsLocation,
            downloadsDestinationId,
        )
        val library = AndroidNavigationDestination.fromNoParams(
            MyLibraryNavigationLocation,
            libraryDestinationId,
        )
        val search = AndroidNavigationDestination.fromParams(
            SearchNavigationLocation,
            searchDestinationId,
        ) { params ->
            NavGraphDirections.actionGlobalSearchNavGraph(params)
        }
        val player = AndroidNavigationDestination.fromParams(
            PlayerNavigationLocation,
            playerDestinationId,
        ) { params ->
            NavGraphDirections.actionGlobalPlayerNavGraph(params)
        }
        val shows = AndroidNavigationDestination.fromNoParams(
            SubscribedShowsLocation,
            showsDestinationId,
        )
        return NavigationFrameworkModule(
            shows = shows,
            player = player,
            account = account,
            downloads = downloads,
            search = search,
            library = library,
            navigationController = DefaultNavigationController(
                mapOf(
                    Pair(showsDestinationId, shows),
                    Pair(libraryDestinationId, library),
                    Pair(loginDestinationId, account),
                    Pair(downloadsDestinationId, downloads)
                ),
                navHostFragmentId,
                navGraphId,
            )
        )
    }
}