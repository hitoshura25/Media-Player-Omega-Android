package com.vmenon.mpo.search.presentation.di.dagger

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.AndroidNavigationDestination
import com.vmenon.mpo.search.presentation.R
import com.vmenon.mpo.navigation.domain.search.ShowDetailsLocation
import com.vmenon.mpo.search.domain.ShowSearchService

import com.vmenon.mpo.search.usecases.*
import com.vmenon.mpo.search.presentation.fragment.ShowSearchResultsFragmentDirections
import dagger.Module
import dagger.Provides

@Module
class SearchModule {
    @Provides
    @SearchScope
    fun provideSearchInteractors(
        showSearchService: ShowSearchService,
        myLibraryService: MyLibraryService,
        downloadsService: DownloadsService
    ): SearchInteractors = SearchInteractors(
        searchForShows = SearchForShows(showSearchService),
        getShowDetails = GetShowDetails(showSearchService),
        subscribeToShow = SubscribeToShow(myLibraryService, downloadsService),
        queueDownloadForShow = QueueDownloadForShow(myLibraryService, downloadsService)
    )

    @Provides
    @SearchScope
    fun provideShowDetailsNavigationDestination(): NavigationDestination<ShowDetailsLocation> =
        AndroidNavigationDestination.fromParams(
            ShowDetailsLocation,
            R.id.nav_search_details
        ) { params ->
            ShowSearchResultsFragmentDirections.actionShowSearchResultsFragmentToShowDetailsFragment(
                params
            )
        }
}