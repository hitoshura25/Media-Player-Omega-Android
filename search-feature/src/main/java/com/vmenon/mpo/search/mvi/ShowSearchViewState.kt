package com.vmenon.mpo.search.mvi

import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.search.domain.ShowSearchResultModel

data class ShowSearchViewState(
    val currentResults: List<ShowSearchResultModel>,
    val previousResults: List<ShowSearchResultModel>,
    val diffResult: DiffUtil.DiffResult? = null,
    val loading: Boolean = false,
    val errored: Boolean = false
)