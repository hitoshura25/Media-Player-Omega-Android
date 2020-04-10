package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.ShowSearchRepository
import com.vmenon.mpo.model.ShowSearchResultsModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class ShowSearchResultsViewModel @Inject constructor(
    private val showSearchRepository: ShowSearchRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {

    fun searchShows(keyword: String): Completable =
        showSearchRepository.searchShows(keyword)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun getShowSearchResultsForTerm(keyword: String): Flowable<List<ShowSearchResultsModel>> =
        showSearchRepository.getShowSearchResultsForTerm(keyword)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun getDiff(
        newSearchResults: List<ShowSearchResultsModel>,
        callback: DiffUtil.Callback
    ): Single<Pair<List<ShowSearchResultsModel>, DiffUtil.DiffResult>> =
        Single.just(Pair(newSearchResults, DiffUtil.calculateDiff(callback)))
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.main())
}