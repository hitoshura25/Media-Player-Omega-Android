package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.core.ShowUpdateManager

import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class ShowDetailsViewModel @Inject constructor(
    private val showSearchRepository: com.vmenon.mpo.search.repository.ShowSearchRepository,
    private val showRepository: com.vmenon.mpo.repository.ShowRepository,
    private val schedulerProvider: com.vmenon.mpo.rx.scheduler.SchedulerProvider,
    private val downloadRepository: com.vmenon.mpo.repository.DownloadRepository,
    private val showUpdateManager: ShowUpdateManager
) : ViewModel() {
    fun getShowDetails(showSearchResultId: Long): Flowable<ShowSearchResultDetailsModel> =
        showSearchRepository.getShowDetails(showSearchResultId)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun subscribeToShow(showDetails: ShowSearchResultDetailsModel): Single<ShowModel> =
        showRepository.save(
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
        ).flatMap(this::fetchInitialUpdate)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ) =
        downloadRepository.queueDownload(show, episode)

    private fun fetchInitialUpdate(show: ShowModel): Single<ShowModel> =
        showUpdateManager.updateShow(show).andThen(Single.just(show))
}