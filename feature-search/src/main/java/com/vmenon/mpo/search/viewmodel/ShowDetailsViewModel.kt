package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.*
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.model.*
import com.vmenon.mpo.search.repository.ShowSearchRepository
import com.vmenon.mpo.shows.ShowUpdateManager
import com.vmenon.mpo.shows.repository.ShowRepository
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShowDetailsViewModel : ViewModel() {

    @Inject
    lateinit var showSearchRepository: ShowSearchRepository

    @Inject
    lateinit var showRepository: ShowRepository

    @Inject
    lateinit var downloadRepository: DownloadRepository

    @Inject
    lateinit var showUpdateManager: ShowUpdateManager

    private val showSubscribed = MutableLiveData<ShowModel>()
    private val downloadQueued = MutableLiveData<DownloadModel>()

    fun showSubscribed(): LiveData<ShowModel> = showSubscribed

    fun downloadQueued(): LiveData<DownloadModel> = downloadQueued

    fun getShowDetails(showSearchResultId: Long): LiveData<ShowSearchResultDetailsModel> =
        liveData(Dispatchers.IO) {
            showSearchRepository.getShowDetails(showSearchResultId)
                ?.let { details -> emit(details) }
        }

    fun subscribeToShow(showDetails: ShowSearchResultDetailsModel) {
        viewModelScope.launch {
            val savedShow = saveShow(showDetails)
            showSubscribed.postValue(savedShow)
            performUpdate(savedShow)
        }
    }

    fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ) = viewModelScope.launch {
        downloadQueued.postValue(downloadRepository.queueDownload(show, episode))
    }

    private suspend fun saveShow(showDetails: ShowSearchResultDetailsModel) =
        withContext(Dispatchers.Default) {
            val show = showRepository.save(
                ShowModel(
                    name = showDetails.show.name,
                    artworkUrl = showDetails.show.artworkUrl,
                    description = showDetails.show.description,
                    genres = showDetails.show.genres,
                    feedUrl = showDetails.show.feedUrl,
                    author = showDetails.show.author,
                    lastEpisodePublished = 0L,
                    lastUpdate = 0L,
                    isSubscribed = true
                )
            )

            show
        }

    private suspend fun performUpdate(show: ShowModel) =
        withContext(Dispatchers.Default) {
            showUpdateManager.updateShow(show)
        }
}