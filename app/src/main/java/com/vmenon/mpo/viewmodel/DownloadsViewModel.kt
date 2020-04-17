package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.DownloadRepository
import com.vmenon.mpo.model.DownloadListItem
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DownloadsViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
    schedulerProvider: SchedulerProvider
) : ViewModel() {
    val downloads: Flowable<List<DownloadListItem>> =
        Flowable.interval(0L, 2L, TimeUnit.SECONDS)
            .flatMap {
                downloadRepository.getAllDownloads()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.main())
            }
}