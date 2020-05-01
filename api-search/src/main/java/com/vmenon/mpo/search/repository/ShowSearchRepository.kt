package com.vmenon.mpo.search.repository

import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import io.reactivex.Completable
import io.reactivex.Flowable

interface ShowSearchRepository {
    fun getShowSearchResultsForTerm(term: String): Flowable<List<ShowSearchResultModel>>
    fun getShowDetails(showSearchResultId: Long): Flowable<ShowSearchResultDetailsModel>
    fun searchShows(keyword: String): Completable
}