package com.vmenon.mpo.search.mvi

import com.vmenon.mpo.search.domain.ShowSearchResultModel

sealed class ShowSearchViewEvent {
    data class SearchRequestedEvent(
        val keyword: String,
        val currentResults: List<ShowSearchResultModel>
    ) : ShowSearchViewEvent()
}