package com.vmenon.mpo.downloads.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import com.vmenon.mpo.model.QueuedDownloadModel
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DownloadsViewModel : ViewModel() {

    @Inject
    lateinit var downloadRepository: DownloadRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    init {
        println("DownloadViewModel()")
    }

    val downloads: Flowable<List<QueuedDownloadModel>> =
        Flowable.interval(0L, 2L, TimeUnit.SECONDS)
            .flatMap {
                downloadRepository.getAllQueued()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.main())
            }
}