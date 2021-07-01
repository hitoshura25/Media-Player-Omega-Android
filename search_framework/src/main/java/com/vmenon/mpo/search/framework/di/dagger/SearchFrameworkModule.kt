package com.vmenon.mpo.search.framework.di.dagger

import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.search.data.SearchApiDataSource
import com.vmenon.mpo.search.data.SearchCacheDataSource
import com.vmenon.mpo.search.data.SearchRepository
import com.vmenon.mpo.search.domain.ShowSearchService
import com.vmenon.mpo.search.framework.MpoRetrofitApiSearchApiDataSource
import com.vmenon.mpo.search.framework.RoomSearchCacheDataSource
import dagger.Module
import dagger.Provides

@Module
object SearchFrameworkModule {
    @Provides
    @SearchFrameworkScope
    fun provideSearchApiDataSource(
        api: MediaPlayerOmegaRetrofitService
    ): SearchApiDataSource = MpoRetrofitApiSearchApiDataSource(api)

    @Provides
    @SearchFrameworkScope
    fun searchCacheDataSource(
        showSearchResultDao: ShowSearchResultDao
    ): SearchCacheDataSource = RoomSearchCacheDataSource(
        showSearchResultDao
    )

    @Provides
    @SearchFrameworkScope
    fun provideSearchService(
        searchApiDataSource: SearchApiDataSource,
        searchCacheDataSource: SearchCacheDataSource
    ): ShowSearchService = SearchRepository(searchApiDataSource, searchCacheDataSource)
}