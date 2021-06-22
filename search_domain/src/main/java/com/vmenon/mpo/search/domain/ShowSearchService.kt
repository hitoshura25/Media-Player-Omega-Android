package com.vmenon.mpo.search.domain

interface ShowSearchService {
    suspend fun searchShows(keyword: String): List<ShowSearchResultModel>?
    suspend fun getShowDetails(showSearchResultId: Long): ShowSearchResultDetailsModel
}