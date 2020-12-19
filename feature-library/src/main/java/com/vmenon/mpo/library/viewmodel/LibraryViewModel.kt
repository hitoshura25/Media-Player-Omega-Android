package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vmenon.mpo.shows.repository.EpisodeRepository
import javax.inject.Inject

class LibraryViewModel : ViewModel() {
    @Inject
    lateinit var episodeRepository: EpisodeRepository

    fun allEpisodes() = liveData{ emit(episodeRepository.getAll())  }
}