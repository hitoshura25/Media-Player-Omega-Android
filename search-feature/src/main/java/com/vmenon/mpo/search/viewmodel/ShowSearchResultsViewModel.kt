package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.usecases.SearchInteractors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShowSearchResultsViewModel : ViewModel() {

    @Inject
    lateinit var searchInteractors: SearchInteractors

    fun searchShows(keyword: String) = liveData<ResultState<List<ShowSearchResultModel>>> {
        emitSource(searchInteractors.searchForShows(keyword).asLiveData())
    }

    suspend fun calculateDiff(
        newSearchResults: List<ShowSearchResultModel>,
        callback: DiffUtil.Callback
    ): Pair<List<ShowSearchResultModel>, DiffUtil.DiffResult> {
        return getDiff(newSearchResults, callback)
    }

    private suspend fun getDiff(
        newSearchResults: List<ShowSearchResultModel>,
        callback: DiffUtil.Callback
    ): Pair<List<ShowSearchResultModel>, DiffUtil.DiffResult> =
        withContext(Dispatchers.Default) {
            Pair(newSearchResults, DiffUtil.calculateDiff(callback))
        }
}