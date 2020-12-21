package com.vmenon.mpo.search.data

import com.vmenon.mpo.search.domain.ShowSearchResultModel

interface SearchCacheDataSource {
    suspend fun loadSearchResultsForTerm(searchTerm: String): List<ShowSearchResultModel>?
    suspend fun getSearchResultById(id: Long): ShowSearchResultModel?
    suspend fun store(searchTerm: String, results: List<ShowSearchResultModel>)
}