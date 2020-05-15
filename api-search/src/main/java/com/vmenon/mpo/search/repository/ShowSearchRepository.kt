package com.vmenon.mpo.search.repository

import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import kotlinx.coroutines.flow.Flow

interface ShowSearchRepository {
    fun getShowSearchResultsForTerm(term: String): Flow<List<ShowSearchResultModel>>
    fun getShowDetails(showSearchResultId: Long): Flow<ShowSearchResultDetailsModel>
    suspend fun searchShows(keyword: String)
}