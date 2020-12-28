package com.vmenon.mpo.search.di.dagger

import androidx.fragment.app.Fragment
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.data.SearchApiDataSource
import com.vmenon.mpo.search.data.SearchCacheDataSource
import com.vmenon.mpo.search.data.SearchRepository
import com.vmenon.mpo.search.domain.SearchNavigationDestination
import com.vmenon.mpo.search.domain.ShowSearchService
import com.vmenon.mpo.search.framework.MpoRetrofitApiSearchApiDataSource
import com.vmenon.mpo.search.framework.RoomSearchCacheDataSource
import com.vmenon.mpo.search.usecases.*
import com.vmenon.mpo.search.view.fragment.ShowSearchResultsFragment
import dagger.Module
import dagger.Provides

@Module
class SearchModule {
    @Provides
    fun provideSearchApiDataSource(service: MediaPlayerOmegaRetrofitService): SearchApiDataSource =
        MpoRetrofitApiSearchApiDataSource(service)

    @Provides
    fun provideSearchDao(searchResultDao: ShowSearchResultDao): SearchCacheDataSource =
        RoomSearchCacheDataSource(searchResultDao)

    @Provides
    fun provideSearchService(
        searchApiDataSource: SearchApiDataSource,
        searchCacheDataSource: SearchCacheDataSource
    ): ShowSearchService = SearchRepository(searchApiDataSource, searchCacheDataSource)

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
    fun provideSearchNavigationDestination(): SearchNavigationDestination =
        object : FragmentDestination, SearchNavigationDestination {
            override val fragmentCreator: () -> Fragment
                get() = { ShowSearchResultsFragment() }
            override val containerId: Int = R.id.fragmentContainerLayout
            override val tag: String
                get() = ShowSearchResultsFragment::class.java.name
        }
}