package com.vmenon.mpo.search.repository.impl

import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.repository.toModel
import com.vmenon.mpo.repository.toSearchResultsModel
import com.vmenon.mpo.search.persistence.ShowSearchPersistence
import com.vmenon.mpo.search.repository.ShowSearchRepository
import com.vmenon.mpo.shows.persistence.ShowPersistence
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class ShowSearchRepositoryImpl(
    private val api: MediaPlayerOmegaApi,
    private val showSearchPersistence: ShowSearchPersistence,
    private val showPersistence: ShowPersistence
) : ShowSearchRepository {
    override fun getShowSearchResultsForTermOrderedByName(term: String): Flowable<List<ShowSearchResultModel>> {
        return showSearchPersistence.getBySearchTermOrderedByName(term)
    }

    override fun getShowDetails(showSearchResultId: Long): Flowable<ShowSearchResultDetailsModel> =
        showSearchPersistence.getSearchResultById(
            showSearchResultId
        ).flatMap { showSearchResult ->
            createShowDetailsModel(showSearchResult)
        }

    override fun searchShows(keyword: String): Completable = api.searchPodcasts(keyword)
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
        checkSubscribed(showSearchResult).flatMap { subscribed ->
            getDetailsFromApi(showSearchResult, subscribed)
        }.toFlowable()

    private fun getDetailsFromApi(
        showSearchResult: ShowSearchResultModel,
        subscribed: Boolean
    ): Single<ShowSearchResultDetailsModel> =
        api.getPodcastDetails(
            showSearchResult.feedUrl,
            10
        ).map { showDetails ->
            ShowSearchResultDetailsModel(
                show = showSearchResult,
                episodes = showDetails.episodes.map { it.toModel() },
                subscribed = subscribed
            )
        }

    private fun checkSubscribed(showSearchResult: ShowSearchResultModel): Single<Boolean> =
        showPersistence.getByName(showSearchResult.name).map { show ->
            show.isSubscribed
        }.toSingle(false)
}