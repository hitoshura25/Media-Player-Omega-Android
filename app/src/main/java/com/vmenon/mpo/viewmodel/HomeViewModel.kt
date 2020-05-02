package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.shows.repository.ShowRepository
import io.reactivex.Flowable
import javax.inject.Inject

class HomeViewModel : ViewModel() {
    @Inject
    lateinit var showRepository: ShowRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    init {
        println("HomeViewModel()")
    }

    fun subscribedShows(): Flowable<List<ShowModel>> =
        showRepository.getSubscribed()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}