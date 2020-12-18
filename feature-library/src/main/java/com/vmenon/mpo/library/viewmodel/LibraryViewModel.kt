package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.shows.repository.EpisodeRepository
import javax.inject.Inject

class LibraryViewModel : ViewModel() {
    @Inject
    lateinit var episodeRepository: EpisodeRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    fun allEpisodes() = liveData{ emit(episodeRepository.getAll())  }
}