package com.vmenon.mpo.core.repository

import android.util.Log
import com.vmenon.mpo.core.persistence.ShowSearchResultDao
import com.vmenon.mpo.model.ShowDetailsModel
import com.vmenon.mpo.model.ShowSearchResultsModel
import com.vmenon.mpo.service.MediaPlayerOmegaService
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.Executors

class ShowSearchRepository(
    private val service: MediaPlayerOmegaService,
    private val showSearchResultDao: ShowSearchResultDao
) {
    private val discExecutor = Executors.newSingleThreadExecutor()

    fun getSearchResultById(id: Long): Single<ShowSearchResultsModel> = Single.create { emitter ->
        emitter.onSuccess(showSearchResultDao.getById(id))
    }

    fun searchShows(keyword: String): Flowable<List<ShowSearchResultsModel>> {
        discExecutor.submit {
            showSearchResultDao.deleteAll()
            val shows = service.searchPodcasts(keyword).blockingFirst()
            shows.forEach { show ->
                show.feedUrl?.let {
                    val searchResult = ShowSearchResultsModel(
                        showDetails = ShowDetailsModel(
                            name = show.name,
                            artworkUrl = show.artworkUrl,
                            author = show.author,
                            feedUrl = it,
                            genres = show.genres
                        ),
                        id = 0L
                    )
                    showSearchResultDao.save(searchResult)
                } ?: Log.e("ShowSearchRepository", "FeedUrl null!")
            }
        }
        return showSearchResultDao.load()
    }
}