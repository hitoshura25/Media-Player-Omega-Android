package com.vmenon.mpo.core.repository

import android.util.Log
import com.vmenon.mpo.core.persistence.ShowSearchResultDao
import com.vmenon.mpo.model.ShowDetailsModel
import com.vmenon.mpo.model.ShowSearchModel
import com.vmenon.mpo.model.ShowSearchResultsModel
import com.vmenon.mpo.service.MediaPlayerOmegaService
import io.reactivex.Flowable
import java.util.concurrent.Executors

class ShowSearchRepository(
    private val service: MediaPlayerOmegaService,
    private val showSearchResultDao: ShowSearchResultDao
) {
    private val discExecutor = Executors.newSingleThreadExecutor()

    fun getSearchResultById(id: Long): Flowable<ShowSearchResultsModel> =
        showSearchResultDao.getSearchResultById(id)

    fun searchShows(keyword: String): Flowable<List<ShowSearchResultsModel>> {
        discExecutor.submit {
            val shows = service.searchPodcasts(keyword).blockingFirst()
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
        return showSearchResultDao.loadSearchResults(keyword)
    }
}