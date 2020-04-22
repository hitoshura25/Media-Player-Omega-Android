package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.ShowRepository
import com.vmenon.mpo.model.ShowModel
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