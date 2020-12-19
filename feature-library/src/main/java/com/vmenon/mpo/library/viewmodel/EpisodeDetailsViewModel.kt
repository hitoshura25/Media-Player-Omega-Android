package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vmenon.mpo.shows.repository.EpisodeRepository
import javax.inject.Inject

class EpisodeDetailsViewModel : ViewModel() {

    @Inject
    lateinit var episodeRepository: EpisodeRepository

    fun getEpisodeDetails(id: Long) = liveData{
        episodeRepository.getById(id)?.let { episode -> emit(episode) }
    }
}