package com.vmenon.mpo.search.persistence

import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import kotlinx.coroutines.flow.Flow

interface ShowSearchPersistence {
    fun getBySearchTermOrderedByName(searchTerm: String): Flow<List<ShowSearchResultModel>>
    fun getSearchResultById(id: Long): Flow<ShowSearchResultModel>
    suspend fun save(results: SearchResultsModel)
}