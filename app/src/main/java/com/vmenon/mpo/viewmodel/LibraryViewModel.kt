package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.EpisodeRepository
import com.vmenon.mpo.model.EpisodeAndShowModel
import io.reactivex.Flowable
import javax.inject.Inject

class LibraryViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun allEpisodes(): Flowable<List<EpisodeAndShowModel>> =
        episodeRepository.getAllEpisodes()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}