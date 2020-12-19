package com.vmenon.mpo.search.repository.impl

import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.repository.toModel
import com.vmenon.mpo.repository.toSearchResultsModel
import com.vmenon.mpo.search.persistence.ShowSearchPersistence
import com.vmenon.mpo.search.repository.ShowSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShowSearchRepositoryImpl(
    private val api: MediaPlayerOmegaApi,
    private val showSearchPersistence: ShowSearchPersistence
) : ShowSearchRepository {
    override suspend fun getShowSearchResultsForTerm(term: String): List<ShowSearchResultModel> {
        return showSearchPersistence.getBySearchTermOrderedByName(term)
    }

    override suspend fun getShowDetails(showSearchResultId: Long): ShowSearchResultDetailsModel? =
        showSearchPersistence.getSearchResultById(
            showSearchResultId
        )?.let { showSearchResult -> createShowDetailsModel(showSearchResult) }

    override suspend fun searchShows(keyword: String) {
        val showSearchResults = withContext(Dispatchers.IO) {
            api.searchPodcasts(keyword).blockingGet()
        }.map { it.toSearchResultsModel() }

        showSearchPersistence.save(
            SearchResultsModel(
                searchTerm = keyword,
                shows = showSearchResults
            )
        )
    }

    private fun createShowDetailsModel(
        showSearchResult: ShowSearchResultModel
    ): ShowSearchResultDetailsModel {
            val showDetails = api.getPodcastDetails(
                showSearchResult.feedUrl,
                10
            ).blockingGet()

            return ShowSearchResultDetailsModel(
                show = showSearchResult,
                episodes = showDetails.episodes.map { it.toModel() },
                subscribed = false
            )
    }
}