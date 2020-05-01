package com.vmenon.mpo.search.persistence

import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import io.reactivex.Flowable

interface ShowSearchPersistence {
    fun getBySearchTerm(searchTerm: String): Flowable<List<ShowSearchResultModel>>
    fun getSearchResultById(id: Long): Flowable<ShowSearchResultModel>
    fun save(results: SearchResultsModel)
}