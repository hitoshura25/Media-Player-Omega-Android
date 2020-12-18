package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.shows.repository.EpisodeRepository
import javax.inject.Inject

class MediaPlayerViewModel : ViewModel() {
    @Inject
    lateinit var episodeRepository: EpisodeRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    fun getEpisodeDetails(id: Long) =
        liveData { episodeRepository.getById(id)?.let { episode -> emit(episode) } }
}