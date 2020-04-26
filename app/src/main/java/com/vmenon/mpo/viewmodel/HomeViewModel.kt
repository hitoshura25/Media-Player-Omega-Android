package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.core.SchedulerProvider
import io.reactivex.Flowable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val showRepository: com.vmenon.mpo.repository.ShowRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun subscribedShows(): Flowable<List<ShowModel>> =
        showRepository.getSubscribed()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}