package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.core.SchedulerProvider
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DownloadsViewModel @Inject constructor(
    downloadRepository: com.vmenon.mpo.repository.DownloadRepository,
    schedulerProvider: SchedulerProvider
) : ViewModel() {
    val downloads: Flowable<List<DownloadModel>> =
        Flowable.interval(0L, 2L, TimeUnit.SECONDS)
            .flatMap {
                downloadRepository.getAllQueued()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.main())
            }
}