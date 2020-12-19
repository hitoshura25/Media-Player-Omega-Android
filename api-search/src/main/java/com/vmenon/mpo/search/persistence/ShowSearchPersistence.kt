package com.vmenon.mpo.search.persistence

import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.model.ShowSearchResultModel

interface ShowSearchPersistence {
    suspend fun getBySearchTermOrderedByName(searchTerm: String): List<ShowSearchResultModel>
    suspend fun getSearchResultById(id: Long): ShowSearchResultModel?
    suspend fun save(results: SearchResultsModel)
}