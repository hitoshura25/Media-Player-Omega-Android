package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.*
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
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
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var downloadRepository: DownloadRepository

    @Inject
    lateinit var showUpdateManager: ShowUpdateManager

    private val showSubscribed = MutableLiveData<ShowModel>()

    fun showSubscribed(): LiveData<ShowModel> = showSubscribed

    fun getShowDetails(showSearchResultId: Long): LiveData<ShowSearchResultDetailsModel> =
        showSearchRepository.getShowDetails(showSearchResultId).asLiveData()

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
    ) = downloadRepository.queueDownload(show, episode)
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())

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