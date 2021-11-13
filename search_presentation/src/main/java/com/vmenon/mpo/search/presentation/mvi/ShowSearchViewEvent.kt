package com.vmenon.mpo.search.presentation.mvi

sealed class ShowSearchViewEvent {
    data class SearchRequestedEvent(
        val keyword: String
    ) : ShowSearchViewEvent()
}