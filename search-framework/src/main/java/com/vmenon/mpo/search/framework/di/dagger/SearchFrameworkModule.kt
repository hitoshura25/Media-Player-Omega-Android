package com.vmenon.mpo.search.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
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
    fun provideSearchApiDataSource(
        commonFrameworkComponent: CommonFrameworkComponent
    ): SearchApiDataSource = MpoRetrofitApiSearchApiDataSource(commonFrameworkComponent.api())

    @Provides
    fun searchCacheDataSource(
        commonFrameworkComponent: CommonFrameworkComponent
    ): SearchCacheDataSource = RoomSearchCacheDataSource(
        commonFrameworkComponent.persistenceComponent().showSearchResultsDao()
    )

    @Provides
    fun provideSearchService(
        searchApiDataSource: SearchApiDataSource,
        searchCacheDataSource: SearchCacheDataSource
    ): ShowSearchService = SearchRepository(searchApiDataSource, searchCacheDataSource)
}