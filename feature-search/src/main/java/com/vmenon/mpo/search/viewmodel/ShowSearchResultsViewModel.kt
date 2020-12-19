package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.search.repository.ShowSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShowSearchResultsViewModel : ViewModel() {

    @Inject
    lateinit var showSearchRepository: ShowSearchRepository

    private val searchResults = MutableLiveData<List<ShowSearchResultModel>>()

    fun searchShows(keyword: String) {
        viewModelScope.launch {
            showSearchRepository.searchShows(keyword)
            searchResults.postValue(showSearchRepository.getShowSearchResultsForTerm(keyword))
        }
    }

    fun getShowSearchResultsForTerm(keyword: String): LiveData<List<ShowSearchResultModel>> = searchResults

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