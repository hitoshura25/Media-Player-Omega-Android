package com.vmenon.mpo.library.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.shows.repository.EpisodeRepository
import io.reactivex.Flowable
import javax.inject.Inject

class EpisodeDetailsViewModel : ViewModel() {

    @Inject
    lateinit var episodeRepository: EpisodeRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    fun getEpisodeDetails(id: Long): Flowable<EpisodeModel> =
        episodeRepository.getById(id)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}