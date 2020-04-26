package com.vmenon.mpo.repository

import android.util.Log
import android.util.LruCache
import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.api.model.Episode
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.persistence.room.entity.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

class ShowSearchRepository(
    private val api: MediaPlayerOmegaApi,
    private val showSearchResultDao: ShowSearchResultDao
) {
    private val showSearchResultsProcessors =
        SearchResultsProcessorCache()

    fun getShowSearchResultsForTerm(term: String): Flowable<List<ShowSearchResultModel>> {
        return showSearchResultsProcessors[term].startWith(
            showSearchResultDao.getBySearchTerm(term).firstElement().toFlowable()
        ).map { searchResultsEntity ->
            searchResultsEntity.map { it.toModel() }
        }
    }

    fun getShowDetails(showSearchResultId: Long): Flowable<ShowSearchResultDetailsModel> =
        showSearchResultDao.getSearchResultById(
            showSearchResultId
        ).flatMap { showSearchResult ->
            createShowDetailsModel(showSearchResult)
        }

    fun searchShows(keyword: String): Completable = api.searchPodcasts(keyword)
        .flatMapCompletable { shows ->
            Completable.fromAction {
                val showSearch = showSearchResultDao.getSearchForTerm(keyword).blockingGet()
                val showSearchId: Long
                if (showSearch != null) {
                    showSearchId = showSearch.showSearchId
                    showSearchResultDao.deleteResultsForSearch(showSearch.showSearchId)
                } else {
                    val newSearch =
                        ShowSearchEntity(
                            showSearchId = BaseEntity.UNSAVED_ID,
                            searchTerm = keyword
                        )
                    showSearchId = showSearchResultDao.save(newSearch)
                }
                val searchResults = ArrayList<ShowSearchResultsEntity>()
                // TODO try sorting on server...
                shows.sortedBy { it.name }.forEach { show ->
                    show.feedUrl?.let {
                        searchResults.add(
                            ShowSearchResultsEntity(
                                showDetails = ShowDetailsEntity(
                                    showName = show.name,
                                    showArtworkUrl = show.artworkUrl,
                                    author = show.author,
                                    feedUrl = it,
                                    genres = show.genres,
                                    showDescription = "",
                                    lastEpisodePublished = 0L,
                                    lastUpdate = 0L,
                                    isSubscribed = false
                                ),
                                showSearchResultsId = 0L,
                                showSearchResultsSearchId = showSearchId
                            )
                        )
                    } ?: Log.e("ShowSearchRepository", "FeedUrl null! $show")
                }
                showSearchResultDao.save(searchResults).forEachIndexed { index, id ->
                    searchResults[index] = searchResults[index].copy(showSearchResultsId = id)
                }
                showSearchResultsProcessors[keyword].offer(searchResults)
            }
        }

    private fun createShowDetailsModel(
        showSearchResult: ShowSearchResultsEntity
    ): Flowable<ShowSearchResultDetailsModel> =
        api.getPodcastDetails(
            showSearchResult.showDetails.feedUrl,
            10
        ).flatMapPublisher { showDetails ->
            Flowable.just(
                ShowSearchResultDetailsModel(
                    show = showSearchResult.toModel(),
                    episodes = showDetails.episodes.map { it.toModel() }
                )
            )
        }

    private fun ShowSearchResultsEntity.toModel() = ShowSearchResultModel(
        id = showSearchResultsId,
        name = showDetails.showName,
        artWorkUrl = showDetails.showArtworkUrl,
        author = showDetails.author,
        description = showDetails.showDescription,
        feedUrl = showDetails.feedUrl,
        genres = showDetails.genres
    )

    private fun Episode.toModel() = ShowSearchResultEpisodeModel(
        name = name,
        artworkUrl = artworkUrl,
        description = description,
        downloadUrl = downloadUrl,
        length = length,
        published = published,
        type = type
    )

    class SearchResultsProcessorCache :
        LruCache<String, PublishProcessor<List<ShowSearchResultsEntity>>>(5) {
        override fun create(key: String?): PublishProcessor<List<ShowSearchResultsEntity>> =
            PublishProcessor.create()
    }
}