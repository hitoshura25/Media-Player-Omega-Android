package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.domain.ShowSearchService
import kotlinx.coroutines.flow.Flow

class SearchForShows(private val showSearchService: ShowSearchService) {
    suspend operator fun invoke(keyword: String): Flow<ResultState<List<ShowSearchResultModel>>> =
        showSearchService.searchShows(keyword)
}