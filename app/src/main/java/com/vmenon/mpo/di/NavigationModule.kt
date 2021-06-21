package com.vmenon.mpo.di

import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.NavGraphDirections
import com.vmenon.mpo.R
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.downloads.domain.DownloadsLocation
import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationLocation
import com.vmenon.mpo.my_library.domain.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.ActivityDestination
import com.vmenon.mpo.navigation.framework.AndroidNavigationDestination
import com.vmenon.mpo.navigation.framework.DefaultNavigationController
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.search.domain.SearchNavigationLocation
import com.vmenon.mpo.view.activity.HomeActivity
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {
    @Provides
    @AppScope
    fun providesNavigationController(
        shows: NavigationDestination<SubscribedShowsLocation>,
        library: NavigationDestination<MyLibraryNavigationLocation>,
        account: NavigationDestination<LoginNavigationLocation>,
        downloads: NavigationDestination<DownloadsLocation>,
        system: System
    ): NavigationController = DefaultNavigationController(
        mapOf(
            Pair(R.id.subscribed_shows_nav_graph, shows),
            Pair(R.id.my_library_nav_graph, library),
            Pair(R.id.login_nav_graph, account),
            Pair(R.id.downloads_nav_graph, downloads)
        ),
        system,
        R.id.nav_host_fragment,
        R.navigation.nav_graph
    )

    @Provides
    fun provideHomeDestination(): NavigationDestination<HomeLocation> =
        ActivityDestination(
            activityClass = HomeActivity::class.java,
            location = HomeLocation
        )

    @Provides
    fun provideDownloadsNavigationDestination(): NavigationDestination<DownloadsLocation> =
        AndroidNavigationDestination.fromNoParams(DownloadsLocation, R.id.downloads_nav_graph)

    @Provides
    fun provideLibraryNavigationDestination(): NavigationDestination<MyLibraryNavigationLocation> =
        AndroidNavigationDestination.fromNoParams(
            MyLibraryNavigationLocation,
            R.id.my_library_nav_graph
        )

    @Provides
    fun provideShowsNavigationDestination(): NavigationDestination<SubscribedShowsLocation> =
        AndroidNavigationDestination.fromNoParams(
            SubscribedShowsLocation,
            R.id.subscribed_shows_nav_graph
        )

    @Provides
    fun provideLoginNavigationDestination(): NavigationDestination<LoginNavigationLocation> =
        AndroidNavigationDestination.fromNoParams(LoginNavigationLocation, R.id.login_nav_graph)

    @Provides
    fun providePlayerNavigationDestination(): NavigationDestination<PlayerNavigationLocation> =
        AndroidNavigationDestination.fromParams(
            PlayerNavigationLocation,
            R.id.player_nav_graph
        ) { params ->
            NavGraphDirections.actionGlobalPlayerNavGraph(params)
        }

    @Provides
    fun provideSearchNavigationDestination(): NavigationDestination<SearchNavigationLocation> =
        AndroidNavigationDestination.fromParams(
            SearchNavigationLocation,
            R.id.search_nav_graph
        ) { params ->
            NavGraphDirections.actionGlobalSearchNavGraph(params)
        }
}