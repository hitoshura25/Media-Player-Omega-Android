package com.vmenon.mpo.core.repository

import android.util.Log
import android.util.LruCache
import com.vmenon.mpo.core.persistence.ShowSearchResultDao
import com.vmenon.mpo.model.*
import com.vmenon.mpo.service.MediaPlayerOmegaService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

class ShowSearchRepository(
    private val service: MediaPlayerOmegaService,
    private val showSearchResultDao: ShowSearchResultDao
) {
    private val showSearchResultsProcessors = SearchResultsProcessorCache()

    fun getShowSearchResultsForTerm(term: String): Flowable<List<ShowSearchResultsModel>> {
        return showSearchResultsProcessors[term].startWith(
            showSearchResultDao.loadSearchResults(term).firstElement().toFlowable()
        )
    }

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
                    showSearchId = showSearch.showSearchId
                    showSearchResultDao.deleteResultsForSearch(showSearch.showSearchId)
                } else {
                    val newSearch = ShowSearchModel(searchTerm = keyword)
                    showSearchId = showSearchResultDao.saveSearch(newSearch)
                }
                val searchResults = ArrayList<ShowSearchResultsModel>()
                // TODO try sorting on server...
                shows.sortedBy { it.name }.forEach { show ->
                    show.feedUrl?.let {
                        searchResults.add(
                            ShowSearchResultsModel(
                                showDetails = ShowDetailsModel(
                                    showName = show.name,
                                    showArtworkUrl = show.artworkUrl,
                                    author = show.author,
                                    feedUrl = it,
                                    genres = show.genres
                                ),
                                showSearchResultsId = 0L,
                                showSearchResultsSearchId = showSearchId
                            )
                        )
                    } ?: Log.e("ShowSearchRepository", "FeedUrl null! $show")
                }
                showSearchResultDao.saveSearchResults(searchResults).forEachIndexed { index, id ->
                    searchResults[index] = searchResults[index].copy(showSearchResultsId = id)
                }
                showSearchResultsProcessors[keyword].offer(searchResults)
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
                            episodeName = episode.name,
                            episodeArtworkUrl = episode.artworkUrl,
                            description = episode.description,
                            downloadUrl = episode.downloadUrl,
                            length = episode.length,
                            published = episode.published,
                            type = episode.type,
                            episodeShowId = 0L,
                            filename = ""
                        )
                    })
            )
        }

    class SearchResultsProcessorCache :
        LruCache<String, PublishProcessor<List<ShowSearchResultsModel>>>(5) {
        override fun create(key: String?): PublishProcessor<List<ShowSearchResultsModel>> =
            PublishProcessor.create()
    }
}