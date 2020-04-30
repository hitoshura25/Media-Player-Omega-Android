package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.shows.repository.ShowRepository
import io.reactivex.Flowable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val showRepository: ShowRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun subscribedShows(): Flowable<List<ShowModel>> =
        showRepository.getSubscribed()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}