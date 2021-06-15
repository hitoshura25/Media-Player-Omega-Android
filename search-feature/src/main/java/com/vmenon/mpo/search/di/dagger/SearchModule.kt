package com.vmenon.mpo.search.di.dagger

import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.search.data.SearchApiDataSource
import com.vmenon.mpo.search.data.SearchCacheDataSource
import com.vmenon.mpo.search.data.SearchRepository
import com.vmenon.mpo.search.domain.ShowSearchService
import com.vmenon.mpo.search.framework.MpoRetrofitApiSearchApiDataSource
import com.vmenon.mpo.search.framework.RoomSearchCacheDataSource
import com.vmenon.mpo.search.usecases.*
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
}