package com.vmenon.mpo.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.search.repository.ShowSearchRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class ShowSearchResultsViewModel : ViewModel() {

    @Inject
    lateinit var showSearchRepository: ShowSearchRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    fun searchShows(keyword: String): Completable =
        showSearchRepository.searchShows(keyword)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun getShowSearchResultsForTerm(keyword: String): Flowable<List<ShowSearchResultModel>> =
        showSearchRepository.getShowSearchResultsForTerm(keyword)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())

    fun getDiff(
        newSearchResults: List<ShowSearchResultModel>,
        callback: DiffUtil.Callback
    ): Single<Pair<List<ShowSearchResultModel>, DiffUtil.DiffResult>> =
        Single.just(Pair(newSearchResults, DiffUtil.calculateDiff(callback)))
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.main())
}