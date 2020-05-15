package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.search.repository.ShowSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShowSearchResultsViewModel : ViewModel() {

    @Inject
    lateinit var showSearchRepository: ShowSearchRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    fun searchShows(keyword: String) {
        viewModelScope.launch {
            showSearchRepository.searchShows(keyword)
        }
    }

    fun getShowSearchResultsForTerm(keyword: String): LiveData<List<ShowSearchResultModel>> =
        showSearchRepository.getShowSearchResultsForTerm(keyword).asLiveData()

    suspend fun calculateDiff(
        newSearchResults: List<ShowSearchResultModel>,
        callback: DiffUtil.Callback
    ): Pair<List<ShowSearchResultModel>, DiffUtil.DiffResult> {
        println("Thread ${Thread.currentThread().name}")
        return getDiff(newSearchResults, callback)
    }

    private suspend fun getDiff(
        newSearchResults: List<ShowSearchResultModel>,
        callback: DiffUtil.Callback
    ): Pair<List<ShowSearchResultModel>, DiffUtil.DiffResult> =
        withContext(Dispatchers.Default) {
            println("Thread ${Thread.currentThread().name}")
            Pair(newSearchResults, DiffUtil.calculateDiff(callback))
        }

}