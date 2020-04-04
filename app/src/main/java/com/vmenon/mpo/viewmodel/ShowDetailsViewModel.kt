package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.ShowRepository
import com.vmenon.mpo.core.repository.ShowSearchRepository
import com.vmenon.mpo.model.*
import com.vmenon.mpo.service.MediaPlayerOmegaService
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class ShowDetailsViewModel @Inject constructor(
    private val showSearchRepository: ShowSearchRepository,
    private val showRepository: ShowRepository,
    private val service: MediaPlayerOmegaService,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun getShowDetails(showSearchResultId: Long): Flowable<ShowDetailsAndEpisodesModel> =
        showSearchRepository.getSearchResultById(
            showSearchResultId
        ).flatMap { showSearchResult ->
            createShowDetailsModel(showSearchResult)
        }
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
        )
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    private fun createShowDetailsModel(
        showSearchResult: ShowSearchResultsModel
    ): Flowable<ShowDetailsAndEpisodesModel> =
        service.getPodcastDetails(
            showSearchResult.showDetails.feedUrl,
            10
        ).flatMap { showDetails ->
            Flowable.just(
                ShowDetailsAndEpisodesModel(
                    showDetails = showSearchResult.showDetails,
                    showDescription = showDetails.description,
                    episodes = showDetails.episodes.map { episode ->
                        EpisodeModel(
                            name = episode.name,
                            artworkUrl = episode.artworkUrl,
                            description = episode.description,
                            downloadUrl = episode.downloadUrl,
                            length = episode.length,
                            published = episode.published,
                            type = episode.type,
                            showId = 0L,
                            filename = ""
                        )
                    })
            )
        }
}