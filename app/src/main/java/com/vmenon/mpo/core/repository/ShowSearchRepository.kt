package com.vmenon.mpo.core.repository

import android.util.Log
import com.vmenon.mpo.core.persistence.ShowSearchResultDao
import com.vmenon.mpo.model.*
import com.vmenon.mpo.service.MediaPlayerOmegaService
import io.reactivex.Completable
import io.reactivex.Flowable

class ShowSearchRepository(
    private val service: MediaPlayerOmegaService,
    private val showSearchResultDao: ShowSearchResultDao
) {
    fun getShowSearchResultsForTerm(term: String): Flowable<List<ShowSearchResultsModel>> =
        showSearchResultDao.loadSearchResults(term)

    fun getShowDetails(showSearchResultId: Long): Flowable<ShowDetailsAndEpisodesModel> =
        showSearchResultDao.getSearchResultById(
            showSearchResultId
        ).flatMap { showSearchResult ->
            createShowDetailsModel(showSearchResult)
        }

    fun searchShows(keyword: String): Completable = service.searchPodcasts(keyword)
        .flatMapCompletable { shows ->
            Completable.fromAction {
                val showSearch = showSearchResultDao.getSearchForTerm(keyword).blockingGet()
                val showSearchId: Long
                if (showSearch != null) {
                    showSearchId = showSearch.id
                    showSearchResultDao.deleteResultsForSearch(showSearch.id)
                } else {
                    val newSearch = ShowSearchModel(searchTerm = keyword)
                    showSearchId = showSearchResultDao.saveSearch(newSearch)
                }
                val searchResults = ArrayList<ShowSearchResultsModel>()
                shows.forEach { show ->
                    show.feedUrl?.let {
                        searchResults.add(
                            ShowSearchResultsModel(
                                showDetails = ShowDetailsModel(
                                    name = show.name,
                                    artworkUrl = show.artworkUrl,
                                    author = show.author,
                                    feedUrl = it,
                                    genres = show.genres
                                ),
                                id = 0L,
                                showSearchId = showSearchId
                            )
                        )
                    } ?: Log.e("ShowSearchRepository", "FeedUrl null!")
                }
                showSearchResultDao.saveSearchResults(searchResults)
            }
        }

    private fun createShowDetailsModel(
        showSearchResult: ShowSearchResultsModel
    ): Flowable<ShowDetailsAndEpisodesModel> =
        service.getPodcastDetails(
            showSearchResult.showDetails.feedUrl,
            10
        ).flatMapPublisher { showDetails ->
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