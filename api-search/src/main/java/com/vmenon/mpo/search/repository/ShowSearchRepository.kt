package com.vmenon.mpo.search.repository

import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultModel

interface ShowSearchRepository {
    suspend fun getShowSearchResultsForTerm(term: String): List<ShowSearchResultModel>?
    suspend fun getShowDetails(showSearchResultId: Long): ShowSearchResultDetailsModel?
    suspend fun searchShows(keyword: String)
}