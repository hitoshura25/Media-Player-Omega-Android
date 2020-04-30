package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import io.reactivex.Flowable
import javax.inject.Inject

class EpisodeDetailsViewModel @Inject constructor(
    private val episodeRepository: com.vmenon.mpo.repository.EpisodeRepository,
    private val schedulerProvider: com.vmenon.mpo.rx.scheduler.SchedulerProvider
) : ViewModel() {
    fun getEpisodeDetails(id: Long): Flowable<EpisodeModel> =
        episodeRepository.getById(id)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}