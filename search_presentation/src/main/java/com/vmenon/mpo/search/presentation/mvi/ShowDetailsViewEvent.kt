package com.vmenon.mpo.search.presentation.mvi

import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.domain.ShowSearchResultModel

sealed class ShowDetailsViewEvent {
    data class LoadShowDetailsEvent(val showSearchResultId: Long) : ShowDetailsViewEvent()
    data class SubscribeToShowEvent(
        val showDetails: ShowSearchResultDetailsModel
    ) : ShowDetailsViewEvent()

    data class QueueDownloadEvent(
        val show: ShowSearchResultModel,
        val episode: ShowSearchResultEpisodeModel
    ) : ShowDetailsViewEvent()
}