package com.vmenon.mpo.navigation.framework.di.dagger

import com.vmenon.mpo.navigation.domain.downloads.DownloadsLocation
import com.vmenon.mpo.navigation.domain.login.LoginNavigationLocation
import com.vmenon.mpo.navigation.domain.my_library.MyLibraryNavigationLocation
import com.vmenon.mpo.navigation.domain.my_library.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import dagger.Module
import dagger.Provides

@Module
class NavigationFrameworkModule(
    private val navigationController: NavigationController,
    private val shows: NavigationDestination<SubscribedShowsLocation>,
    private val library: NavigationDestination<MyLibraryNavigationLocation>,
    private val account: NavigationDestination<LoginNavigationLocation>,
    private val downloads: NavigationDestination<DownloadsLocation>,
    private val player: NavigationDestination<PlayerNavigationLocation>,
    private val search: NavigationDestination<SearchNavigationLocation>

) {
    @Provides
    @NavigationFrameworkScope
    fun providesNavigationController(): NavigationController = navigationController

    @Provides
    @NavigationFrameworkScope
    fun provideDownloadsNavigationDestination(): NavigationDestination<DownloadsLocation> =
        downloads

    @Provides
    @NavigationFrameworkScope
    fun provideLibraryNavigationDestination(): NavigationDestination<MyLibraryNavigationLocation> =
        library

    @Provides
    @NavigationFrameworkScope
    fun provideShowsNavigationDestination(): NavigationDestination<SubscribedShowsLocation> =
        shows

    @Provides
    @NavigationFrameworkScope
    fun provideLoginNavigationDestination(): NavigationDestination<LoginNavigationLocation> =
        account

    @Provides
    @NavigationFrameworkScope
    fun providePlayerNavigationDestination(): NavigationDestination<PlayerNavigationLocation> =
        player

    @Provides
    @NavigationFrameworkScope
    fun provideSearchNavigationDestination(): NavigationDestination<SearchNavigationLocation> =
        search
}