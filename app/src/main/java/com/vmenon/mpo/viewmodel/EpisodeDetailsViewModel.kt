package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.EpisodeRepository
import com.vmenon.mpo.model.EpisodeWithShowDetailsModel
import io.reactivex.Flowable
import javax.inject.Inject

class EpisodeDetailsViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun getEpisodeDetails(id: Long): Flowable<EpisodeWithShowDetailsModel> =
        episodeRepository.getEpisodeWithShow(id)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}