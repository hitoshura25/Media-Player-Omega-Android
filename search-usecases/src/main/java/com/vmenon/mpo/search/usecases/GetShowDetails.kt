package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchService
import kotlinx.coroutines.flow.Flow

class GetShowDetails(private val showSearchService: ShowSearchService) {
    suspend operator fun invoke(showId: Long): Flow<ResultState<ShowSearchResultDetailsModel>> =
        showSearchService.getShowDetails(showId)
}