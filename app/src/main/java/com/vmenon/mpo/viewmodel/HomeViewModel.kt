package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vmenon.mpo.shows.repository.ShowRepository
import javax.inject.Inject

class HomeViewModel : ViewModel() {
    @Inject
    lateinit var showRepository: ShowRepository

    init {
        println("HomeViewModel()")
    }

    fun subscribedShows() = liveData { emit(showRepository.getSubscribed()) }
}