package com.vmenon.mpo.search.presentation.viewmodel

import androidx.lifecycle.*
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEffect
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEvent
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewState
import com.vmenon.mpo.search.usecases.SearchInteractors

import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class ShowDetailsViewModel : ViewModel() {

    @Inject
    lateinit var searchInteractors: SearchInteractors

    private val initialState = ShowDetailsViewState(loading = true)
    private val states =
        MutableLiveData<ContentEvent<ShowDetailsViewState>>(ContentEvent(initialState))
    private val effects = MutableLiveData<ContentEvent<ShowDetailsViewEffect>>()

    private var currentState: ShowDetailsViewState
        get() = states.value?.anyContent() ?: initialState
        set(value) {
            states.postValue(ContentEvent(value))
        }

    fun states(): LiveData<ContentEvent<ShowDetailsViewState>> = states
    fun effects(): LiveData<ContentEvent<ShowDetailsViewEffect>> = effects

    fun send(event: ShowDetailsViewEvent) {
        viewModelScope.launch {
            when (event) {
                is ShowDetailsViewEvent.LoadShowDetailsEvent -> getShowDetails(
                    event.showSearchResultId
                )
                is ShowDetailsViewEvent.SubscribeToShowEvent -> subscribeToShow(event.showDetails)
                is ShowDetailsViewEvent.QueueDownloadEvent -> queueDownload(
                    event.show,
                    event.episode
                )
            }
        }
    }

    private suspend fun getShowDetails(showSearchResultId: Long) {
        currentState = try {
            val details = searchInteractors.getShowDetails(showSearchResultId)
            currentState.copy(
                showDetails = details,
                loading = false,
                error = false
            )
        } catch (e: Exception) {
            currentState.copy(
                loading = false,
                error = true
            )
        }
    }

    private suspend fun subscribeToShow(showDetails: ShowSearchResultDetailsModel) {
        effects.postValue(
            ContentEvent(
                ShowDetailsViewEffect.ShowSubscribedViewEffect(
                    searchInteractors.subscribeToShow(showDetails)
                )
            )
        )
        currentState = currentState.copy(showDetails = showDetails.copy(subscribed = true))
    }

    private suspend fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ) {
        effects.postValue(
            ContentEvent(
                ShowDetailsViewEffect.DownloadQueuedViewEffect(
                    searchInteractors.queueDownloadForShow(show, episode)
                )
            )
        )
    }
}