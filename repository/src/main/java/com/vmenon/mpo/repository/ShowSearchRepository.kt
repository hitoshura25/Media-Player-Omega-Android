package com.vmenon.mpo.repository

import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.persistence.ShowSearchPersistence
import io.reactivex.Completable
import io.reactivex.Flowable

class ShowSearchRepository(
    private val api: MediaPlayerOmegaApi,
    private val showSearchPersistence: ShowSearchPersistence
) {
    fun getShowSearchResultsForTerm(term: String): Flowable<List<ShowSearchResultModel>> {
        return showSearchPersistence.getBySearchTerm(term)
    }

    fun getShowDetails(showSearchResultId: Long): Flowable<ShowSearchResultDetailsModel> =
        showSearchPersistence.getSearchResultById(
            showSearchResultId
        ).flatMap { showSearchResult ->
            createShowDetailsModel(showSearchResult)
        }

    fun searchShows(keyword: String): Completable = api.searchPodcasts(keyword)
        .flatMapCompletable { shows ->
            Completable.fromAction {
                val showSearchResults = shows.map { it.toSearchResultsModel() }
                showSearchPersistence.save(
                    SearchResultsModel(
                        searchTerm = keyword,
                        shows = showSearchResults
                    )
                )
            }
        }

    private fun createShowDetailsModel(
        showSearchResult: ShowSearchResultModel
    ): Flowable<ShowSearchResultDetailsModel> =
        api.getPodcastDetails(
            showSearchResult.feedUrl,
            10
        ).flatMapPublisher { showDetails ->
            Flowable.just(
                ShowSearchResultDetailsModel(
                    show = showSearchResult,
                    episodes = showDetails.episodes.map { it.toModel() }
                )
            )
        }
}