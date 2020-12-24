package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.*
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.usecases.SearchInteractors

import kotlinx.coroutines.launch
import javax.inject.Inject

class ShowDetailsViewModel : ViewModel() {

    @Inject
    lateinit var searchInteractors: SearchInteractors

    private val showSubscribed = MutableLiveData<ShowModel>()
    private val downloadQueued = MutableLiveData<DownloadModel>()

    fun showSubscribed(): LiveData<ShowModel> = showSubscribed

    fun downloadQueued(): LiveData<DownloadModel> = downloadQueued

    fun getShowDetails(showSearchResultId: Long): LiveData<ResultState<ShowSearchResultDetailsModel>> =
        liveData {
            emitSource(searchInteractors.getShowDetails(showSearchResultId).asLiveData())
        }

    fun subscribeToShow(showDetails: ShowSearchResultDetailsModel) {
        viewModelScope.launch {
            showSubscribed.postValue(searchInteractors.subscribeToShow(showDetails))
        }
    }

    fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ) {
        viewModelScope.launch {
            downloadQueued.postValue(searchInteractors.queueDownloadForShow(show, episode))
        }
    }
}