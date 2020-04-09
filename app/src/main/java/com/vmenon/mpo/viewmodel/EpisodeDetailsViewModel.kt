package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.EpisodeRepository
import com.vmenon.mpo.core.repository.ShowRepository
import com.vmenon.mpo.model.EpisodeDetailsModel
import io.reactivex.Flowable
import javax.inject.Inject

class EpisodeDetailsViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository,
    private val showRepository: ShowRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun getEpisodeDetails(id: Long): Flowable<EpisodeDetailsModel> =
        episodeRepository.getEpisode(id)
            .map { episode ->
                EpisodeDetailsModel(
                    episode,
                    showRepository.getShow(episode.showId).blockingFirst()
                )
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}