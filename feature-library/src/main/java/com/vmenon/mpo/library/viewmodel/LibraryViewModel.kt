package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.shows.repository.EpisodeRepository
import io.reactivex.Flowable
import javax.inject.Inject

class LibraryViewModel : ViewModel() {
    @Inject
    lateinit var episodeRepository: EpisodeRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    fun allEpisodes(): Flowable<List<EpisodeModel>> =
        episodeRepository.getAll()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}