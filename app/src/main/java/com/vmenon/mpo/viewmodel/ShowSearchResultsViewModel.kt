package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.ShowSearchRepository
import com.vmenon.mpo.model.ShowSearchResultsModel
import io.reactivex.Completable
import io.reactivex.Flowable
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
}