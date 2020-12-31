package com.vmenon.mpo.search.domain

import com.vmenon.mpo.common.domain.ResultState
import kotlinx.coroutines.flow.Flow

interface ShowSearchService {
    suspend fun searchShows(keyword: String): List<ShowSearchResultModel>?
    suspend fun getShowDetails(showSearchResultId: Long): Flow<ResultState<ShowSearchResultDetailsModel>>
}