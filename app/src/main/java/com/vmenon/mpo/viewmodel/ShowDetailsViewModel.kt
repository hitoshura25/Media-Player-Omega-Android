package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.DownloadManager
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.ShowUpdateManager
import com.vmenon.mpo.core.repository.ShowRepository
import com.vmenon.mpo.core.repository.ShowSearchRepository
import com.vmenon.mpo.model.*
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class ShowDetailsViewModel @Inject constructor(
    private val showSearchRepository: ShowSearchRepository,
    private val showRepository: ShowRepository,
    private val schedulerProvider: SchedulerProvider,
    private val downloadManager: DownloadManager,
    private val showUpdateManager: ShowUpdateManager
) : ViewModel() {
    fun getShowDetails(showSearchResultId: Long): Flowable<ShowDetailsAndEpisodesModel> =
        showSearchRepository.getShowDetails(showSearchResultId)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun subscribeToShow(showDetails: ShowDetailsModel): Single<ShowModel> =
        showRepository.save(
            ShowModel(
                showDetails = showDetails,
                lastEpisodePublished = 0L,
                lastUpdate = 0L,
                isSubscribed = true
            )
        ).flatMap(this::fetchInitialUpdate)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun queueDownload(showDetails: ShowDetailsModel, episode: EpisodeModel) =
        downloadManager.queueDownload(showDetails, episode)

    private fun fetchInitialUpdate(show: ShowModel): Single<ShowModel> =
        showUpdateManager.updateShow(show).andThen(Single.just(show))
}