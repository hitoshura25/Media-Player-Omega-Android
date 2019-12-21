package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.MPORepository
import com.vmenon.mpo.model.ShowModel
import io.reactivex.Flowable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val mpoRepository: MPORepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun subscribedShows(): Flowable<List<ShowModel>> =
        mpoRepository.getAllSubscribedShows()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}