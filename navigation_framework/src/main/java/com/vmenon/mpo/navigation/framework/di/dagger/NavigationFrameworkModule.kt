package com.vmenon.mpo.navigation.framework.di.dagger

import com.vmenon.mpo.navigation.domain.downloads.DownloadsLocation
import com.vmenon.mpo.navigation.domain.login.LoginNavigationLocation
import com.vmenon.mpo.navigation.domain.my_library.MyLibraryNavigationLocation
import com.vmenon.mpo.navigation.domain.my_library.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.*
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object NavigationFrameworkModule {
    @Provides
    @NavigationFrameworkScope
    fun providesNavigationController(
        @Named("navigationHostFragmentId") hostFragmentId: Int,
        shows: NavigationDestination<SubscribedShowsLocation>,
        library: NavigationDestination<MyLibraryNavigationLocation>,
        account: NavigationDestination<LoginNavigationLocation>,
        downloads: NavigationDestination<DownloadsLocation>
    ): NavigationController = DefaultNavigationController(
        mapOf(
            Pair(R.id.subscribed_shows_nav_graph, shows),
            Pair(R.id.my_library_nav_graph, library),
            Pair(R.id.login_nav_graph, account),
            Pair(R.id.downloads_nav_graph, downloads)
        ),
        hostFragmentId,
        R.navigation.nav_graph
    )

    @Provides
    @NavigationFrameworkScope
    fun provideDownloadsNavigationDestination(): NavigationDestination<DownloadsLocation> =
        AndroidNavigationDestination.fromNoParams(DownloadsLocation, R.id.downloads_nav_graph)

    @Provides
    @NavigationFrameworkScope
    fun provideLibraryNavigationDestination(): NavigationDestination<MyLibraryNavigationLocation> =
        AndroidNavigationDestination.fromNoParams(
            MyLibraryNavigationLocation,
            R.id.my_library_nav_graph
        )

    @Provides
    @NavigationFrameworkScope
    fun provideShowsNavigationDestination(): NavigationDestination<SubscribedShowsLocation> =
        AndroidNavigationDestination.fromNoParams(
            SubscribedShowsLocation,
            R.id.subscribed_shows_nav_graph
        )

    @Provides
    @NavigationFrameworkScope
    fun provideLoginNavigationDestination(): NavigationDestination<LoginNavigationLocation> =
        AndroidNavigationDestination.fromNoParams(LoginNavigationLocation, R.id.login_nav_graph)

    @Provides
    @NavigationFrameworkScope
    fun providePlayerNavigationDestination(): NavigationDestination<PlayerNavigationLocation> =
        AndroidNavigationDestination.fromParams(
            PlayerNavigationLocation,
            R.id.player_nav_graph
        ) { params ->
            NavGraphDirections.actionGlobalPlayerNavGraph(params)
        }

    @Provides
    @NavigationFrameworkScope
    fun provideSearchNavigationDestination(): NavigationDestination<SearchNavigationLocation> =
        AndroidNavigationDestination.fromParams(
            SearchNavigationLocation,
            R.id.search_nav_graph
        ) { params ->
            NavGraphDirections.actionGlobalSearchNavGraph(params)
        }
}