package com.vmenon.mpo.search.mvi

sealed class ShowSearchViewEvent {
    data class SearchRequestedEvent(
        val keyword: String
    ) : ShowSearchViewEvent()
}