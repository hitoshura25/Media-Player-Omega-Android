package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchService

class GetShowDetails(private val showSearchService: ShowSearchService) {
    suspend operator fun invoke(showId: Long): ShowSearchResultDetailsModel =
        showSearchService.getShowDetails(showId)
}