package com.vmenon.mpo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.shows.repository.ShowRepository
import javax.inject.Inject

class HomeViewModel : ViewModel() {
    @Inject
    lateinit var showRepository: ShowRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    init {
        println("HomeViewModel()")
    }

    fun subscribedShows(): LiveData<List<ShowModel>> =
        showRepository.getSubscribed().asLiveData()
}