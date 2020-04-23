package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.vmenom.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.core.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class ShowSearchResultsViewModel @Inject constructor(
    private val showSearchRepository: com.vmenon.mpo.repository.ShowSearchRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {

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