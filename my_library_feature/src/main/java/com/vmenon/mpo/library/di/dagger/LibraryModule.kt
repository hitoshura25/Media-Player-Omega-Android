package com.vmenon.mpo.library.di.dagger

import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.view.fragment.LibraryFragmentDirections
import com.vmenon.mpo.my_library.domain.*
import com.vmenon.mpo.my_library.usecases.*
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.my_library.EpisodeDetailsLocation
import com.vmenon.mpo.navigation.framework.AndroidNavigationDestination
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import dagger.Module
import dagger.Provides

@Module
object LibraryModule {

    @Provides
    fun provideMyLibraryInteractors(
        myLibraryService: MyLibraryService,
        navigationController: NavigationController,
        playerNavigationDestination: NavigationDestination<PlayerNavigationLocation>,
        searchNavigationDestination: NavigationDestination<SearchNavigationLocation>
    ): MyLibraryInteractors =
        MyLibraryInteractors(
            GetAllEpisodes(myLibraryService),
            GetEpisodeDetails(myLibraryService),
            GetSubscribedShows(myLibraryService),
            PlayEpisode(
                myLibraryService,
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