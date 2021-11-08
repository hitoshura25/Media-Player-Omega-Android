package com.vmenon.mpo.search.presentation.mvi

import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel

data class ShowDetailsViewState(
    val showDetails: ShowSearchResultDetailsModel? = null,
    val loading: Boolean = false,
    val error: Boolean = false
)