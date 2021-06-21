package com.vmenon.mpo.library.di.dagger

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.view.fragment.LibraryFragmentDirections
import com.vmenon.mpo.my_library.domain.*
import com.vmenon.mpo.my_library.framework.di.dagger.LibraryFrameworkComponent
import com.vmenon.mpo.my_library.usecases.*
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.AndroidNavigationDestination
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.search.domain.SearchNavigationLocation
import dagger.Module
import dagger.Provides

@Module
object LibraryModule {

    @Provides
    fun provideMyLibraryInteractors(
        myLibraryService: MyLibraryService,
        downloadService: DownloadsService,
        navigationController: NavigationController,
        requestMapper: PlayerRequestMapper<EpisodeModel>,
        playerNavigationDestination: NavigationDestination<PlayerNavigationLocation>,
        searchNavigationDestination: NavigationDestination<SearchNavigationLocation>
    ): MyLibraryInteractors =
        MyLibraryInteractors(
            GetAllEpisodes(myLibraryService),
            GetEpisodeDetails(myLibraryService),
            UpdateAllShows(myLibraryService, downloadService),
            GetSubscribedShows(myLibraryService),
            PlayEpisode(
                myLibraryService,
                requestMapper,
                navigationController,
                playerNavigationDestination
            ),
            SearchForShows(navigationController, searchNavigationDestination)
        )

    @Provides
    fun provideEpisodeDetailsLocation(): NavigationDestination<EpisodeDetailsLocation> =
        AndroidNavigationDestination.fromParams(
            EpisodeDetailsLocation,
            R.id.nav_episode_details,
            { params ->
                LibraryFragmentDirections.actionLibraryFragmentToEpisodeDetailsFragment(params)
            }
        )
}