package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenom.mpo.model.EpisodeModel
import com.vmenon.mpo.core.SchedulerProvider
import io.reactivex.Flowable
import javax.inject.Inject

class LibraryViewModel @Inject constructor(
    private val episodeRepository: com.vmenon.mpo.repository.EpisodeRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    fun allEpisodes(): Flowable<List<EpisodeModel>> =
        episodeRepository.getAll()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}