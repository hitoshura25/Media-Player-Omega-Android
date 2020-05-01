package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.search.repository.ShowSearchRepository
import com.vmenon.mpo.shows.ShowUpdateManager
import com.vmenon.mpo.shows.repository.ShowRepository

import io.reactivex.Flowable
import io.reactivex.Single
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
    ) = downloadRepository.queueDownload(show, episode)

    private fun fetchInitialUpdate(show: ShowModel): Single<ShowModel> =
        showUpdateManager.updateShow(show).andThen(Single.just(show))
}