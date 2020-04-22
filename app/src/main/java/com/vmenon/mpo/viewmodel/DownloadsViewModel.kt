package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.DownloadRepository
import com.vmenon.mpo.model.QueuedDownloadModel
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DownloadsViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
    schedulerProvider: SchedulerProvider
) : ViewModel() {
    val downloads: Flowable<List<QueuedDownloadModel>> =
        Flowable.interval(0L, 2L, TimeUnit.SECONDS)
            .flatMap {
                downloadRepository.getAllQueued()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.main())
            }
}