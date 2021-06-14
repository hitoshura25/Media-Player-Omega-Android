package com.vmenon.mpo.di

import com.vmenon.mpo.R
import com.vmenon.mpo.downloads.domain.DownloadsLocation
import com.vmenon.mpo.library.view.fragment.EpisodeDetailsFragmentDirections
import com.vmenon.mpo.library.view.fragment.LibraryFragmentDirections
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragmentDirections
import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.my_library.domain.EpisodeDetailsLocation
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationLocation
import com.vmenon.mpo.my_library.domain.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.AndroidNavigationDestination
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.search.domain.SearchNavigationLocation
import com.vmenon.mpo.search.domain.ShowDetailsLocation
import com.vmenon.mpo.search.view.fragment.ShowSearchResultsFragmentDirections
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {
    @Provides
    fun provideDownloadsNavigationDestination(): NavigationDestination<DownloadsLocation> =
        AndroidNavigationDestination.fromNoParams(DownloadsLocation, R.id.nav_downloads)

    @Provides
    fun provideLibraryNavigationDestination(): NavigationDestination<MyLibraryNavigationLocation> =
        AndroidNavigationDestination.fromNoParams(MyLibraryNavigationLocation, R.id.nav_downloads)

    @Provides
    fun provideShowsNavigationDestination(): NavigationDestination<SubscribedShowsLocation> =
        AndroidNavigationDestination.fromNoParams(SubscribedShowsLocation, R.id.nav_home)

    @Provides
    fun provideEpisodeDetailsDestination(): NavigationDestination<EpisodeDetailsLocation> =
        AndroidNavigationDestination.fromParams(EpisodeDetailsLocation) { params ->
            LibraryFragmentDirections.actionLibraryFragmentToEpisodeDetailsFragment(params)
        }

    @Provides
    fun provideLoginNavigationDestination(): NavigationDestination<LoginNavigationLocation> =
        AndroidNavigationDestination.fromNoParams(LoginNavigationLocation, R.id.nav_account)

    @Provides
    fun providePlayerNavigationDestination(): NavigationDestination<PlayerNavigationLocation> =
        AndroidNavigationDestination.fromParams(PlayerNavigationLocation) { params ->
            EpisodeDetailsFragmentDirections.actionEpisodeDetailsFragmentToMediaPlayerFragment(
                params
            )
        }


    @Provides
    fun provideSearchNavigationDestination(): NavigationDestination<SearchNavigationLocation> =
        AndroidNavigationDestination.fromParams(SearchNavigationLocation) { params ->
            SubscribedShowsFragmentDirections.actionNavHomeToShowSearchResultsFragment(
                params
            )
        }

    @Provides
    fun provideShowDetailsNavigationDestination(): NavigationDestination<ShowDetailsLocation> =
        AndroidNavigationDestination.fromParams(ShowDetailsLocation) { params ->
            ShowSearchResultsFragmentDirections.actionShowSearchResultsFragmentToShowDetailsFragment(
                params
            )
        }
}