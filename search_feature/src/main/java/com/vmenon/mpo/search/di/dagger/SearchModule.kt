package com.vmenon.mpo.search.di.dagger

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.AndroidNavigationDestination
import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.domain.ShowDetailsLocation
import com.vmenon.mpo.search.domain.ShowSearchService

import com.vmenon.mpo.search.usecases.*
import com.vmenon.mpo.search.view.fragment.ShowSearchResultsFragmentDirections
import dagger.Module
import dagger.Provides

@Module
class SearchModule {
    @Provides
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