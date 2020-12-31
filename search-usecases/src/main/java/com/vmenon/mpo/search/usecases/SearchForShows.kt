package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.domain.ShowSearchService

class SearchForShows(private val showSearchService: ShowSearchService) {
    suspend operator fun invoke(keyword: String): List<ShowSearchResultModel>? =
        showSearchService.searchShows(keyword)
}