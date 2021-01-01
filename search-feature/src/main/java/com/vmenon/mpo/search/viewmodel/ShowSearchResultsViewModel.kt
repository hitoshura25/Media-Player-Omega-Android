package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.search.mvi.ShowSearchViewEvent
import com.vmenon.mpo.search.mvi.ShowSearchViewState
import com.vmenon.mpo.search.usecases.SearchInteractors
import com.vmenon.mpo.search.view.adapter.diff.ShowSearchResultsDiff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShowSearchResultsViewModel : ViewModel() {

    @Inject
    lateinit var searchInteractors: SearchInteractors

    private val initialState = ShowSearchViewState(
        previousResults = emptyList(),
        currentResults = emptyList(),
        loading = true
    )
    private val states = MutableLiveData(ContentEvent(initialState))

    private var currentState: ShowSearchViewState
        get() = states.value?.anyContent() ?: initialState
        set(value) {
            states.postValue(ContentEvent(value))
        }

    fun states(): LiveData<ContentEvent<ShowSearchViewState>> = states

    fun send(event: ShowSearchViewEvent) {
        viewModelScope.launch {
            when (event) {
                is ShowSearchViewEvent.SearchRequestedEvent -> searchShows(event.keyword)
            }
        }
    }

    private suspend fun searchShows(keyword: String) {
        val newResults = searchInteractors.searchForShows(keyword) ?: emptyList()
        val diffResult = getDiff(
            ShowSearchResultsDiff(currentState.currentResults, newResults)
        )

        currentState = currentState.copy(
            previousResults = currentState.currentResults,
            currentResults = newResults,
            diffResult = diffResult,
            loading = false
        )
    }

    private suspend fun getDiff(callback: DiffUtil.Callback): DiffUtil.DiffResult =
        withContext(Dispatchers.Default) {
            DiffUtil.calculateDiff(callback)
        }
}