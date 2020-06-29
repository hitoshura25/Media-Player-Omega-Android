package com.vmenon.mpo.persistence.room

import android.annotation.SuppressLint
import android.util.LruCache
import com.vmenon.mpo.model.SearchResultsModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.persistence.room.base.entity.BaseEntity
import com.vmenon.mpo.persistence.room.dao.ShowSearchResultDao
import com.vmenon.mpo.persistence.room.entity.ShowSearchEntity
import com.vmenon.mpo.persistence.room.entity.ShowSearchResultsEntity
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.search.persistence.ShowSearchPersistence
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

class ShowSearchPersistenceRoom(
    private val showSearchResultDao: ShowSearchResultDao,
    private val schedulerProvider: SchedulerProvider
) : ShowSearchPersistence {

    // We only want to emit events for updated search results, and don't want to emit for when
    // any existing search results are cleared. So we'll use our own publisher to only emit the
    // updated results. We'll also cache a few publishers based on searchTerm
    private val searchResultsPublishers = SearchResultsProcessorCache(5)

    override fun getBySearchTermOrderedByName(searchTerm: String): Flowable<List<ShowSearchResultModel>> {
        scheduleFirstSearchResultsLoad(searchTerm)
        return searchResultsPublishers[searchTerm].map { results ->
            results.map { it.toModel() }
        }
    }

    override fun getSearchResultById(id: Long): Flowable<ShowSearchResultModel> =
        showSearchResultDao.getSearchResultById(id).map { it.toModel() }

    override fun save(results: SearchResultsModel) {
        val showSearch = showSearchResultDao.getSearchForTerm(results.searchTerm).blockingGet()
        val showSearchId: Long
        if (showSearch != null) {
            showSearchId = showSearch.showSearchId
            showSearchResultDao.deleteResultsForSearch(showSearch.showSearchId)
        } else {
            val newSearch =
                ShowSearchEntity(
                    showSearchId = BaseEntity.UNSAVED_ID,
                    searchTerm = results.searchTerm
                )
            showSearchId = showSearchResultDao.save(newSearch)
        }
        val searchResults = ArrayList<ShowSearchResultsEntity>()
        results.shows.forEach { show ->
            show.feedUrl.let {
                searchResults.add(
                    ShowSearchResultsEntity(
                        showName = show.name,
                        showArtworkUrl = show.artworkUrl,
                        author = show.author,
                        feedUrl = it,
                        genres = show.genres,
                        showDescription = "",
                        lastEpisodePublished = 0L,
                        lastUpdate = 0L,
                        isSubscribed = false,
                        showSearchResultsId = 0L,
                        showSearchResultsSearchId = showSearchId
                    )
                )
            }
        }
        showSearchResultDao.save(searchResults)
        emitSearchResults(results.searchTerm)
    }

    @SuppressLint("CheckResult")
    private fun scheduleFirstSearchResultsLoad(searchTerm: String) {
        Completable.fromAction {
            emitSearchResults(searchTerm, true)
        }.subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.io())
            .subscribe {

            }
    }

    private fun emitSearchResults(searchTerm: String, ignoreEmpty: Boolean = false) {
        val results = showSearchResultDao.getBySearchTermOrderedByName(searchTerm)
            .blockingFirst(emptyList())
        if (!ignoreEmpty || results.isNotEmpty()) {
            searchResultsPublishers[searchTerm].onNext(results)
        }
    }

    private class SearchResultsProcessorCache(
        maxSize: Int
    ) : LruCache<String, BehaviorProcessor<List<ShowSearchResultsEntity>>>(maxSize) {
        override fun create(key: String): BehaviorProcessor<List<ShowSearchResultsEntity>> {
            return BehaviorProcessor.create()
        }
    }
}